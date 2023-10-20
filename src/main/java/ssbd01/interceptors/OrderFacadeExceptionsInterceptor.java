package ssbd01.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import ssbd01.exceptions.AccountApplicationException;
import ssbd01.exceptions.ApplicationException;
import ssbd01.exceptions.OrderException;

public class OrderFacadeExceptionsInterceptor {
  @AroundInvoke
  public Object intercept(InvocationContext invocationContext) throws Exception {
    try {
      return invocationContext.proceed();
    } catch (OptimisticLockException e) {
      throw e;
    } catch(NoResultException e) {
      throw ApplicationException.createEntityNotFoundException();
    } catch (PersistenceException | java.sql.SQLException e) {
      if (e.getCause() instanceof ConstraintViolationException csv) {
        PSQLException cause = (PSQLException) csv.getCause();
        if(cause.getMessage().contains("prescription_number")) {
          throw OrderException.createPrescriptionAlreadyExists();
        }
      }
      throw AccountApplicationException.createAccountConstraintViolationException(e);
    }
  }
}

