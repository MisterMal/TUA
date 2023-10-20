package ssbd01.exceptions;

import ssbd01.common.i18n;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

@jakarta.ejb.ApplicationException(rollback = true)
public class ApplicationExceptionEntityNotFound extends ApplicationException {

  ApplicationExceptionEntityNotFound() {
    super(NOT_FOUND, i18n.EXCEPTION_ENTITY_NOT_FOUND);
  }
}
