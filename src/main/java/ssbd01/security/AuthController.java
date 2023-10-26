package ssbd01.security;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import ssbd01.common.AbstractController;
import ssbd01.dto.auth.LoginDTO;
import ssbd01.dto.auth.TokenDto;
import ssbd01.entities.Account;
import ssbd01.entities.Role;
import ssbd01.exceptions.AccountApplicationException;
import ssbd01.exceptions.ApplicationException;
import ssbd01.exceptions.ApplicationExceptionEntityNotFound;
import ssbd01.exceptions.AuthApplicationException;
import ssbd01.mok.managers.AccountManager;
import ssbd01.util.AccessLevelFinder;

import java.time.Instant;
import java.util.Date;

@Log
@Path("/auth")
@DenyAll
public class AuthController extends AbstractController {
  @Inject
  @RequestScoped
  public IdentityStoreHandler identityStoreHandler;
  @Inject
  public JwtUtils jwtUtils;
  @Context
  public HttpServletRequest httpServletRequest;
  @Inject
  public AccountManager accountManager;

  @POST
  @Path("/login")
  @PermitAll
  public Response authenticate(@Valid LoginDTO loginDto) {
    Account account;
    try {
      account = repeatTransaction(accountManager, () -> accountManager.findByLogin(loginDto.getLogin()));
    } catch (ApplicationExceptionEntityNotFound e) {
      throw AuthApplicationException.createInvalidLoginOrPasswordException();
    }

    if (!account.getConfirmed()) {
        throw AccountApplicationException.createAccountNotConfirmedException();
    }

    UsernamePasswordCredential credential =
        new UsernamePasswordCredential(loginDto.getLogin(), loginDto.getPassword());
    CredentialValidationResult result = identityStoreHandler.validate(credential);
    if (result.getStatus() != CredentialValidationResult.Status.VALID) {
      repeatTransactionVoid(
          accountManager,
          () ->
              accountManager.updateAuthInformation(
                  credential.getCaller(),
                  httpServletRequest.getRemoteAddr(),
                  Date.from(Instant.now()),
                  false));
      throw AuthApplicationException.createInvalidLoginOrPasswordException();
    }

    repeatTransactionVoid(
        accountManager,
        () ->
            accountManager.updateAuthInformation(
                credential.getCaller(),
                httpServletRequest.getRemoteAddr(),
                Date.from(Instant.now()),
                true));
    return Response.ok(new TokenDto(jwtUtils.create(result))).build();
  }

  @POST
  @Path("/notify-access-level-change/{role}")
  @RolesAllowed("notifyAccessLevelChange")
  public Response notifyAccessLevelChange(@PathParam("role") String role) {
    Account account = repeatTransaction(accountManager, () -> accountManager.getCurrentUserWithAccessLevels());
    Role roleEnum;
    try {
      roleEnum = Role.valueOf(role);
    } catch(IllegalArgumentException e) {
      throw AccountApplicationException.createUndefinedAccessLevelException();
    }
    try {
      AccessLevelFinder.findAccessLevel(account, roleEnum);
    } catch(ApplicationException e) {
      log.info(String.format("User %s tried to change role to %s, which they do not have",
              accountManager.getCurrentUserLogin(), role));
      throw ApplicationException.createUnauthorisedException();
    }
    log.info(String.format("User %s changed role to %s",
            accountManager.getCurrentUserLogin(), role));
    return Response.ok().build();
  }
}
