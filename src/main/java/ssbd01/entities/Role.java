package ssbd01.entities;

import io.quarkus.security.jpa.RolesValue;
import lombok.Getter;

@Getter
public enum Role implements CharSequence {
  ADMIN("ADMIN", "Administrator"),
  PATIENT("PATIENT", "Pacjent"),
  CHEMIST("CHEMIST", "Farmaceuta");

  @RolesValue
  private String roleName;
  private String roleDescription;

  Role(String roleName, String roleDescription) {
    this.roleName = roleName;
    this.roleDescription = roleDescription;
  }

  @Override
  public int length() {
    // Implement the length method based on either roleName or roleDescription
    // Return the length of the chosen string.
    return roleName.length(); // Or use roleDescription.length();
  }

  @Override
  public char charAt(int index) {
    // Implement the charAt method based on either roleName or roleDescription
    // Return the character at the specified index of the chosen string.
    return roleName.charAt(index); // Or use roleDescription.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    // Implement the subSequence method based on either roleName or roleDescription
    // Return a subsequence of the chosen string based on the start and end indices.
    return roleName.subSequence(start, end); // Or use roleDescription.subSequence(start, end);
  }
}
