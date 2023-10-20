package ssbd01.common;

import jakarta.ejb.Local;

@Local
public interface CommonManagerLocalInterface {

  boolean isLastTransactionRollback();
}
