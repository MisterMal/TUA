package ssbd01.security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.vertx.ext.web.RoutingContext;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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

import java.security.Security;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log
@Path("/auth")
@DenyAll
public class AuthController extends AbstractController {
  @Inject
  public JwtUtils jwtUtils;
  @Inject
  RoutingContext context;
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

    boolean result = false;

    if(account.getLogin().equals(loginDto.getLogin()) && account.getPassword().equals(loginDto.getPassword())) {
      result = true;
    }


    if (result) {
      repeatTransactionVoid(
          accountManager,
          () ->
              accountManager.updateAuthInformation(
                  account.getLogin(),
                  context.request().host(),
                  Date.from(Instant.now()),
                  false));
      throw AuthApplicationException.createInvalidLoginOrPasswordException();
    }

    repeatTransactionVoid(
        accountManager,
        () ->
            accountManager.updateAuthInformation(
                    account.getLogin(),
                    context.request().host(),
                Date.from(Instant.now()),
                true));
    Set<Role> set = new HashSet<>();
    account.getAccessLevels().forEach(accessLevel -> {
      set.add(accessLevel.getRole());
    });
    return Response.ok(new TokenDto(jwtUtils.create(account.getLogin(), set))).build();
  }

  @POST
  @Path("/notify-access-level-change/{role}")
  @PermitAll
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
