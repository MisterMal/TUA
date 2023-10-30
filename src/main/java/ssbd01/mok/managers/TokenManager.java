package ssbd01.mok.managers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ssbd01.common.AbstractManager;
import ssbd01.entities.Account;
import ssbd01.entities.Token;
import ssbd01.entities.TokenType;
import ssbd01.exceptions.TokenException;
import ssbd01.interceptors.GenericManagerExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;
import ssbd01.mok.facades.TokenFacade;
import ssbd01.util.email.EmailService;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static ssbd01.exceptions.TokenException.*;

@ApplicationScoped
@Stateful
@Log
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
        GenericManagerExceptionsInterceptor.class,
        TrackerInterceptor.class
})
@PermitAll
@Transactional
public class TokenManager extends AbstractManager
    implements SessionSynchronization {

  @Inject
  TokenFacade tokenFacade;

  @Inject
  EmailService emailService;

  private int VERIFICATION_TOKEN_EXPIRATION_HOURS = 24;

  private int RESET_PASSWORD_TOKEN_EXPIRATION_HOURS = 24;

  @PermitAll
  public void sendVerificationToken(Account account, String code) {
    Token token = makeToken(account, code, TokenType.VERIFICATION);
    tokenFacade.create(token);
    emailService.sendRegistrationEmail(
        account.getEmail(), account.getLogin(), account.getLanguage(), token.getCode());
  }

  @PermitAll
  public Token makeToken(Account account, String code, TokenType tokenType) {
    Token token =
        Token.builder()
            .account(account)
            .code(code == null ? RandomStringUtils.randomAlphanumeric(8) : code)
            .tokenType(tokenType)
            .wasPreviousTokenSent(false)
            .expirationDate(createExpirationDate(tokenType))
            .build();

    token.setCreatedBy(account.getLogin());

//    tokenFacade.create(token);
//    emailService.sendRegistrationEmail(account.getEmail(), account.getLogin(),
//            account.getLanguage(), token.getCode());
    return token;
  }

  @PermitAll
  public Date createExpirationDate(TokenType type) {
    long hours = (type == TokenType.VERIFICATION) ? VERIFICATION_TOKEN_EXPIRATION_HOURS :
            RESET_PASSWORD_TOKEN_EXPIRATION_HOURS;
    return new Date(
        Instant.now()
            .plusSeconds(hours * 60 * 60)
            .toEpochMilli());
  }

  @PermitAll
  public void verifyAccount(String code) {
    Token token = tokenFacade.findByCode(code);
    checkIfTokenIsValid(token, TokenType.VERIFICATION);
    Account account = token.getAccount();
    account.setConfirmed(true);
    account.setModifiedBy(account.getLogin());
    token.setUsed(true);
    token.setModifiedBy(account.getLogin());
    tokenFacade.edit(token);
    emailService.sendEmailAccountActivated(
        account.getEmail(), account.getLogin(), account.getLanguage());
  }

  @PermitAll
  public void sendEmailChangeEmail(Account account, String new_email) {
    Token token = makeToken(account, encodeEmail(new_email), TokenType.VERIFICATION);
    tokenFacade.create(token);
    emailService.sendEmailChangeEmail(new_email, account.getLogin(), account.getLanguage(), token.getCode());
  }

  @PermitAll
  public String encodeEmail(String new_email) {
    return RandomStringUtils.randomAlphanumeric(8)
        + Base64.getEncoder().encodeToString(new_email.getBytes());
  }
  @PermitAll
  public String decodeEmail(String code) {
    return Base64.getDecoder().decode((code.substring(8, code.length())).getBytes()).toString();
  }

  @PermitAll
  public void confirmEmailChange(String code) {
    Token token = tokenFacade.findByCode(code);
    checkIfTokenIsValid(token, TokenType.VERIFICATION);
    Account account = token.getAccount();
    account.setEmail(decodeEmail(code));
    account.setModifiedBy(account.getLogin());
    token.setUsed(true);
    emailService.sendEmailAccountActivated(
        account.getEmail(), account.getLogin(), account.getLanguage());
    tokenFacade.edit(token);
  }

  @PermitAll
  public List<Token> findTokensByTokenTypeAndExpirationDateBefore(
      TokenType verification, Date halfExpirationDate) {
    return tokenFacade.findByTypeAndBeforeGivenData(verification, halfExpirationDate);
  }

  @PermitAll
  public void sendResetPasswordToken(Account account) {
    Token token = makeToken(account, null, TokenType.PASSWORD_RESET);
    tokenFacade.create(token);
    emailService.sendEmailResetPassword(
        account.getEmail(), account.getLogin(), account.getLanguage(), token.getCode());
  }

  @PermitAll
  public void setNewPassword(String token, String newPassword) {
    Token foundToken = tokenFacade.findByCode(token);
    checkIfTokenIsValid(foundToken, TokenType.PASSWORD_RESET);

    Account account = foundToken.getAccount();
    account.setPassword(newPassword);
    account.setModifiedBy(account.getLogin());
    foundToken.setUsed(true);
    foundToken.setModifiedBy(account.getLogin());

    tokenFacade.edit(foundToken);
  }

  @PermitAll
  public void checkIfTokenIsValid(Token token, TokenType type) {
    if (token == null) {
      throw TokenException.tokenNotFoundException();
    }
    Account account = token.getAccount();

    if (token.getExpirationDate().before(new Date())) {
      throw tokenExpiredException();
    }
    if (token.isUsed()) {
      throw tokenAlreadyUsedException();
    }
    if (type == TokenType.VERIFICATION
        && account.getConfirmed()) {
      throw tokenAlreadyUsedException();
    }
    if (token.getTokenType() != type) {
      throw incorrectTokenTypeException();
    }
  }
}
