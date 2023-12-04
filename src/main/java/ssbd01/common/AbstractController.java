package ssbd01.common;

import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.TransactionException;

import java.util.function.Supplier;

@Log
public abstract class AbstractController {

  public int TRANSACTION_REPEAT_COUNT = 3;

  protected <T> T repeatTransaction(CommonManagerLocalInterface service, Supplier<T> method) {
    int retryTXCounter = TRANSACTION_REPEAT_COUNT;
    boolean rollbackTX = false;

    T result = null;
    do {
      try {
        result = method.get();
        rollbackTX = service.isLastTransactionRollback();
      } catch (EJBTransactionRolledbackException e) {
        rollbackTX = true;
      }
    } while (rollbackTX && --retryTXCounter > 0);

    if (rollbackTX && retryTXCounter == 0) {
      throw new TransactionException("Transaction failed");
    }
    return result;
  }

  protected void repeatTransactionVoid(CommonManagerLocalInterface service, VoidFI voidFI) {
    int retryTXCounter = TRANSACTION_REPEAT_COUNT;
    boolean rollbackTX = false;

    do {
      try {
        voidFI.action();
        rollbackTX = service.isLastTransactionRollback();
      } catch (EJBTransactionRolledbackException e) {
        rollbackTX = true;
      }
    } while (rollbackTX && --retryTXCounter > 0);

    if (rollbackTX && retryTXCounter == 0) {
      throw new TransactionException("Transaction failed");
    }
  }

  protected void repeatTransactionVoidWithOptimisticLock(CommonManagerLocalInterface service, VoidFI voidFI) {
    int retryTXCounter = TRANSACTION_REPEAT_COUNT;
    boolean rollbackTX = false;

    do {
      try {
        voidFI.action();
        rollbackTX = service.isLastTransactionRollback();
      } catch (EJBTransactionRolledbackException | OptimisticLockException e) {
        rollbackTX = true;
      }
    } while (rollbackTX && --retryTXCounter > 0);

    if (rollbackTX && retryTXCounter == 0) {
      throw new TransactionException("Transaction failed");
    }
  }



  @FunctionalInterface
  public interface VoidFI {
    void action();
  }
}
