package ssbd01.exceptions;

import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.EXPECTATION_FAILED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static ssbd01.common.i18n.*;

@jakarta.ejb.ApplicationException(rollback = true)
public class TokenException extends ApplicationException {

  private TokenException(Response.Status status, String key) {
    super(status, key);
  }

  private TokenException(Response.Status status, String key, Exception e) {
    super(status, key, e);
  }

  public static TokenException tokenExpiredException() {
    return new TokenException(EXPECTATION_FAILED, EXCEPTION_TOKEN_EXPIRED);
  }

  public static TokenException tokenAlreadyUsedException() {
    return new TokenException(EXPECTATION_FAILED, EXCEPTION_TOKEN_ALREADY_USED);
  }

  public static TokenException tokenNotFoundException() {
    return new TokenException(NOT_FOUND, EXCEPTION_TOKEN_NOT_FOUND);
  }

  public static TokenException incorrectTokenTypeException() {
    return new TokenException(EXPECTATION_FAILED, EXCEPTION_TOKEN_BAD_TYPE);
  }
}
