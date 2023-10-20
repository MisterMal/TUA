package ssbd01.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import lombok.extern.java.Log;
import ssbd01.exceptions.ApplicationException;

import java.sql.SQLException;

@Log
public class GenericFacadeExceptionsInterceptor {

  @AroundInvoke
  public Object intercept(InvocationContext ictx) throws ApplicationException {
    try {
      return ictx.proceed();
    } catch (OptimisticLockException e) {
      throw ApplicationException.createOptimisticLockException();
    } catch (PersistenceException | SQLException e) {
      throw ApplicationException.createPersistenceException(e);
    } catch (ApplicationException e) {
      throw e;
    } catch (Exception e) {
      log.warning(e.getMessage());
      throw ApplicationException.createGeneralException(e);
    }
  }
}
