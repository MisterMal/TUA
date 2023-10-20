package ssbd01.util;


import ssbd01.entities.AccessLevel;
import ssbd01.entities.Account;
import ssbd01.entities.PatientData;
import ssbd01.entities.Role;
import ssbd01.exceptions.ApplicationException;

public class AccessLevelFinder {
  public static AccessLevel findAccessLevel(Account account, Role role) {
    for (AccessLevel next : account.getAccessLevels()) {
      if (next.getRole().equals(role)) {
        return next;
      }
    }
    throw ApplicationException.createEntityNotFoundException();
  }

  public static PatientData findPatientData(Account account) {
    AccessLevel accessLevel = findAccessLevel(account, Role.PATIENT);
    return (PatientData) accessLevel;
  }
}
