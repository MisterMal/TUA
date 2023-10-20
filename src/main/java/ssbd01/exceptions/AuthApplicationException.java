package ssbd01.exceptions;

import jakarta.ws.rs.core.Response;

import static ssbd01.common.i18n.EXCEPTION_AUTH_BAD_CREDENTIALS;
import static ssbd01.common.i18n.EXCEPTION_AUTH_BLOCKED_ACCOUNT;

@jakarta.ejb.ApplicationException(rollback = true)
public class AuthApplicationException extends ApplicationException {
  private AuthApplicationException(Response.Status status, String key) {
    super(status, key);
  }

  private AuthApplicationException(Response.Status status, String key, Exception e) {
    super(status, key, e);
  }

  public static AuthApplicationException createInvalidLoginOrPasswordException() {
    return new AuthApplicationException(
        Response.Status.UNAUTHORIZED, EXCEPTION_AUTH_BAD_CREDENTIALS);
  }

  public static AuthApplicationException accountBlockedException() {
    return new AuthApplicationException(Response.Status.FORBIDDEN, EXCEPTION_AUTH_BLOCKED_ACCOUNT);
  }
}
