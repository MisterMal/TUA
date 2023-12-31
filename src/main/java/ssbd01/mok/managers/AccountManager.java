package ssbd01.mok.managers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ssbd01.common.AbstractManager;
import ssbd01.common.CommonManagerLocalInterface;
import ssbd01.entities.*;
import ssbd01.exceptions.AccountApplicationException;
import ssbd01.exceptions.ApplicationException;
import ssbd01.exceptions.AuthApplicationException;
import ssbd01.interceptors.GenericManagerExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;
import ssbd01.mok.facades.AccountFacade;
import ssbd01.security.HashAlgorithmImpl;
import ssbd01.util.AccessLevelFinder;
import ssbd01.util.email.EmailService;
import ssbd01.util.mergers.AccessLevelMerger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static ssbd01.exceptions.AuthApplicationException.accountBlockedException;

@Stateful
@ApplicationScoped
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({GenericManagerExceptionsInterceptor.class, TrackerInterceptor.class})
@Log
@PermitAll
@Transactional
public class AccountManager extends AbstractManager implements SessionSynchronization, CommonManagerLocalInterface {

  @Inject
  public AccountFacade accountFacade;

  @Inject
  public TokenManager verificationManager;

  public int UNCONFIRMED_ACCOUNT_DELETION_TIMEOUT_HOURS = 24;


  public int UNBLOCKING_ACCOUNT_TIMEOUT_HOURS = 12;

  public int MAX_INCORRECT_LOGIN_ATTEMPTS = 3;

  public int TEMPORARY_ACCOUNT_BLOCK_HOURS = 24;

  @Inject private EmailService emailService;
  @Context
  private SecurityContext context;


  @PermitAll
  public List<Account> getAllAccounts() {
    return accountFacade.findAll();
  }


  @PermitAll
  public Account findByLogin(String login) {
    return accountFacade.findByLogin(login);
  }


  @PermitAll
  public Account getCurrentUser() {
    return accountFacade.findByLogin(getCurrentUserLogin());
  }


  @PermitAll
  public String getCurrentUserLogin() {
    return "patient123";
  }


  @PermitAll
  public Account getCurrentUserWithAccessLevels() {
    return accountFacade.findByLoginAndRefresh(getCurrentUserLogin());
  }


  @PermitAll
  public Account getAccountAndAccessLevels(Long id) {
    Optional<Account> optionalAccount = accountFacade.findAndRefresh(id);
    if (optionalAccount.isEmpty()) {
      throw ApplicationException.createEntityNotFoundException();
    }
    return optionalAccount.get();
  }


  @PermitAll
  public Account grantAccessLevel(Long id, AccessLevel accessLevel, String login, Long version) {
    Account account = getAccount(id);
    if(!account.getLogin().equals(login)) {
      throw ApplicationException.createMismatchedPayloadException();
    }
    if(!account.getVersion().equals(version)) {
      throw ApplicationException.createOptimisticLockException();
    }
    accessLevel.setAccount(account);
    accessLevel.setCreatedBy(getCurrentUserLogin());
    account.getAccessLevels().add(accessLevel);
    account.setModifiedBy(getCurrentUserLogin());
    account.setModificationDate(new Date()); // force version increment on account
    accountFacade.edit(account);
    return account;
  }


  @PermitAll
  public AccessLevel getAccessLevel(Long id, Role role) {
    Account account = getAccount(id);
    return AccessLevelFinder.findAccessLevel(account, role);
  }


  @PermitAll
  public void deactivateAccessLevel(Long id, Role role) {
    Account account = getAccountAndAccessLevels(id);
    if(Objects.equals(account.getLogin(), getCurrentUserLogin()) && role == Role.ADMIN)
      throw AccountApplicationException.createDeactivationOfSelf();
    AccessLevel accessLevel = AccessLevelFinder.findAccessLevel(account, role);
    if(!accessLevel.getActive()) return;
    int numberActive = 0;
    for(AccessLevel l : account.getAccessLevels())
      if(l.getActive()) numberActive++;
    if(numberActive <= 1)
      throw AccountApplicationException.createDeactivateLastAccessLevel();
    accessLevel.setActive(false);
    account.setModifiedBy(getCurrentUserLogin());
    accessLevel.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
  }

  @PermitAll
  public void activateAccessLevel(Long id, Role role) {
    Account account = getAccountAndAccessLevels(id);
    AccessLevel accessLevel = AccessLevelFinder.findAccessLevel(account, role);
    if(accessLevel.getActive()) return;
    accessLevel.setActive(true);
    account.setModifiedBy(getCurrentUserLogin());
    accessLevel.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
  }


  @PermitAll
  public Account getAccount(Long id) {
    Optional<Account> optionalAccount = accountFacade.find(id);
    if (optionalAccount.isEmpty()) {
      throw ApplicationException.createEntityNotFoundException();
    }
    return optionalAccount.get();
  }


  @PermitAll
  public void confirmAccountRegistration(String verificationToken) {
    verificationManager.verifyAccount(verificationToken);
  }


  @PermitAll
  public Account registerAccount(Account account) {
    account.setPassword(HashAlgorithmImpl.generate(account.getPassword()));
    accountFacade.create(account);
    verificationManager.sendVerificationToken(account, null);
    return account;
  }


  @PermitAll
  public Account createAccount(Account account) {
    account.setPassword(HashAlgorithmImpl.generate(account.getPassword()));
    account.setCreatedBy(getCurrentUserLogin());
    accountFacade.create(account);
    return account;
  }


  @PermitAll
  public Account editAccessLevel(Long id, AccessLevel accessLevel, Long version) {
    Account account = getAccount(id);
    AccessLevel found = AccessLevelFinder.findAccessLevel(account, accessLevel.getRole());
    if (!Objects.equals(found.getVersion(), version)) {
      throw ApplicationException.createOptimisticLockException();
    }
    AccessLevelMerger.mergeAccessLevels(found, accessLevel);
    found.setModifiedBy(getCurrentUserLogin());
    account.setModificationDate(new Date());
    accountFacade.edit(account);
    return account;
  }


  @PermitAll
  public Account editSelfAccessLevel(AccessLevel accessLevel, Long version) {
    Account account = getCurrentUserWithAccessLevels();
    if (!Objects.equals(account.getVersion(), version)) {
      throw ApplicationException.createOptimisticLockException();
    }
    AccessLevel found = AccessLevelFinder.findAccessLevel(account, accessLevel.getRole());
    AccessLevelMerger.mergeAccessLevels(found, accessLevel);
    found.setModifiedBy(getCurrentUserLogin());
    account.setModificationDate(new Date());
    accountFacade.edit(account);
    return account;
  }


  @PermitAll
  public Account activateUserAccount(Long id) {
    Account account = getAccount(id);
    account.setConfirmed(true);
    emailService.sendEmailAccountActivated(
        account.getEmail(), account.getLogin(), account.getLanguage());
    account.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
    return account;
  }


  @PermitAll
  public void blockAccount(Long id) {
    Account account = getAccount(id);
    if (!account.getActive()) {
      return;
    }
    account.setActive(false);
    account.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
    emailService.sendEmailAccountBlocked(
        account.getEmail(), account.getLogin(), account.getLanguage());
  }


  @PermitAll
  public void unblockAccount(Long id) {
    Account account = getAccount(id);
    if (account.getActive()) {
      return;
    }
    account.setActive(true);
    account.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
    emailService.sendEmailAccountUnblocked(
        account.getEmail(), account.getLogin(), account.getLanguage());
  }


  @PermitAll
  public void sendResetPasswordToken(String email) {
    Account account = accountFacade.findByEmail(email);
    verificationManager.sendResetPasswordToken(account);
  }


  @PermitAll
  public void setNewPassword(String token, String newPassword) {
    String password = HashAlgorithmImpl.generate(newPassword);
    verificationManager.setNewPassword(token, password);
  }

  @PermitAll
  public Account updateUserPassword(Long id, String newPassword, String login, Long version) {
    Account account = getAccount(id);
    if(!account.getLogin().equals(login)) {
      throw ApplicationException.createMismatchedPayloadException();
    }
    if(!account.getVersion().equals(version)) {
      throw ApplicationException.createOptimisticLockException();
    }
    account.setPassword(HashAlgorithmImpl.generate(newPassword));
    account.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
    return account;
  }


  @PermitAll
  public Account updateOwnPassword(String oldPassword, String newPassword, String login, Long version) {
    Account account = getCurrentUser();
    if(!account.getLogin().equals(login)) {
      throw ApplicationException.createMismatchedPayloadException();
    }
    if(!account.getVersion().equals(version)) {
      throw ApplicationException.createOptimisticLockException();
    }
    if (!HashAlgorithmImpl.check(oldPassword, account.getPassword())) {
      throw AuthApplicationException.createInvalidLoginOrPasswordException();
    }
    if (HashAlgorithmImpl.check(newPassword, account.getPassword())) {
      throw ApplicationException.createPasswordNotChangedException();
    }
    account.setPassword(HashAlgorithmImpl.generate(newPassword));
    account.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
    return account;
  }


  @PermitAll
  public Account updateOwnEmail(String email, Long version) {
    Account account = getCurrentUser();
    if(!account.getVersion().equals(version)) {
      throw ApplicationException.createOptimisticLockException();
    }
    account.setConfirmed(false);
    account.setEmail(email);
    account.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
    verificationManager.sendEmailChangeEmail(account, email);
    return account;
  }


  @PermitAll
  public void changeAccountLanguage(String language) {
    try {
      LanguageType.valueOf(language);
    } catch(IllegalArgumentException e) {
      throw ApplicationException.createLanguageNotFoundException();
    }
    Locale locale = Locale.forLanguageTag(language);
    Account account = getCurrentUser();
    account.setLanguage(locale);
    accountFacade.edit(account);
  }


  @PermitAll
  public Account updateUserEmail(Long id, String email, String login, Long version) {
    Account account = getAccount(id);
    if(!account.getLogin().equals(login)) {
      throw ApplicationException.createMismatchedPayloadException();
    }
    if(!account.getVersion().equals(version)) {
      throw ApplicationException.createOptimisticLockException();
    }
    account.setEmail(email);
    account.setConfirmed(true);
    account.setModifiedBy(getCurrentUserLogin());
    accountFacade.edit(account);
    return account;
  }


  @PermitAll
  public void purgeUnactivatedAccounts() {
    List<Account> accountsToPurge = accountFacade.findNotConfirmed();

    for (Account account : accountsToPurge) {
      LocalDateTime timeoutThreshold =
          LocalDateTime.now().minusHours(UNCONFIRMED_ACCOUNT_DELETION_TIMEOUT_HOURS);
      LocalDateTime creationDate =
          Instant.ofEpochMilli(account.getCreationDate().getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDateTime();
      if (creationDate.isBefore(timeoutThreshold)) {
        accountFacade.remove(account);
        emailService.sendEmailWhenRemovedDueToNotConfirmed(account.getEmail(), account.getLogin(),
                account.getLanguage());
      }
    }
  }


  @PermitAll
  public void activateBlockedAccounts() {
    List<Account> blockedAccounts = accountFacade.findNotActiveAndIncorrectLoginAttemptsEqual(
            MAX_INCORRECT_LOGIN_ATTEMPTS);

    for (Account account : blockedAccounts) {
      LocalDateTime timeoutThreshold = LocalDateTime.now()
              .minusHours(UNBLOCKING_ACCOUNT_TIMEOUT_HOURS);
      LocalDateTime lastNegativeLogin = Instant.ofEpochMilli(account.getLastNegativeLogin().getTime())
                      .atZone(ZoneId.systemDefault())
                      .toLocalDateTime();
      if (lastNegativeLogin.isBefore(timeoutThreshold)) {
        account.setActive(true);
        accountFacade.edit(account);
        emailService.sendEmailAccountActivated(account.getEmail(), account.getLogin(), account.getLanguage());
      }
    }
  }


  @PermitAll
  public void updateAuthInformation(String caller, String remoteAddr, Date now, Boolean isCorrect) {
    Account account = accountFacade.findByLogin(caller);
    if (account.getIncorrectLoginAttempts() >= MAX_INCORRECT_LOGIN_ATTEMPTS) {
      LocalDateTime timeoutThreshold =
          LocalDateTime.now().minusHours(TEMPORARY_ACCOUNT_BLOCK_HOURS);
      LocalDateTime lastIncorrectLogin =
          Instant.ofEpochMilli(account.getLastNegativeLogin().getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDateTime();
      if (lastIncorrectLogin.isBefore(timeoutThreshold)) {
        account.setActive(true);
      }
    }

    if (!account.getActive()) {
      throw accountBlockedException();
    }
    if (isCorrect) {
      account.setLastPositiveLogin(now);
      account.setLogicalAddress(remoteAddr);
      account.setIncorrectLoginAttempts(0);
    } else {
      account.setLastNegativeLogin(now);
      account.setLogicalAddress(remoteAddr);
      account.setIncorrectLoginAttempts(account.getIncorrectLoginAttempts() + 1);
      if (account.getIncorrectLoginAttempts() >= MAX_INCORRECT_LOGIN_ATTEMPTS) {
        account.setActive(false);
        emailService.sendEmailAccountBlockedTooManyLogins(account.getEmail(), account.getLogin(),
                account.getLanguage());
      }
    }
  }


  @PermitAll
  public void sendVerificationTokenIfPreviousWasNotSent() {

    Date halfExpirationDate =
        Date.from(
            Instant.now().minus(UNCONFIRMED_ACCOUNT_DELETION_TIMEOUT_HOURS / 2, ChronoUnit.HOURS));

    List<Token> tokensToResend =
        verificationManager.findTokensByTokenTypeAndExpirationDateBefore(
            TokenType.VERIFICATION, halfExpirationDate);

    tokensToResend.forEach(
        token -> {
          token.setWasPreviousTokenSent(true);
          verificationManager.sendVerificationToken(token.getAccount(), token.getCode());
        });
  }


  @PermitAll
  public void confirmEmailChange(String code) {
    verificationManager.confirmEmailChange(code);
  }
}
