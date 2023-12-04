package ssbd01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.stream.IntStream;

@Entity
@Getter
@Setter
@Table(name = "patient_data")
@DiscriminatorValue("PATIENT")
@NoArgsConstructor
@ToString(callSuper = true)
@NamedQuery(name = "patientData.findAll", query = "SELECT o FROM PatientData o")
@AllArgsConstructor
public class PatientData extends AccessLevel implements Serializable, CharSequence {

  private static final long serialVersionUID = 1L;

  @Builder
  public PatientData(
      Long id, String pesel, String firstName, String lastName, String phoneNumber, String NIP) {
    super(id, Role.PATIENT);
    this.pesel = pesel;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.NIP = NIP;
  }

  @Column(nullable = false, unique = true)
  @Pattern(regexp = "^[0-9]{11}$", message = "Invalid PESEL")
  private String pesel;

  @Column(nullable = false, name = "first_name")
  @Size(max = 50, min = 2)
  private String firstName;

  @Column(nullable = false, name = "last_name")
  @Size(max = 50, min = 2)
  private String lastName;

  @Column(nullable = false, unique = true ,name = "phone_number")
  @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number")
  private String phoneNumber;

  @Column(nullable = false, unique = true)
  @Pattern(regexp = "^\\d{10}$", message = "Invalid NIP")
  private String NIP;
  @Override
  public int length() {
    // Calculate the total length of the concatenated string
    return pesel.length() + firstName.length() + lastName.length() + phoneNumber.length() + NIP.length();
  }

  @Override
  public char charAt(int index) {
    // Implement charAt based on the concatenated string
    int peselLength = pesel.length();
    int firstNameLength = firstName.length();
    int lastNameLength = lastName.length();
    int phoneNumberLength = phoneNumber.length();

    if (index < peselLength) {
      return pesel.charAt(index);
    } else if (index < peselLength + firstNameLength) {
      return firstName.charAt(index - peselLength);
    } else if (index < peselLength + firstNameLength + lastNameLength) {
      return lastName.charAt(index - peselLength - firstNameLength);
    } else if (index < peselLength + firstNameLength + lastNameLength + phoneNumberLength) {
      return phoneNumber.charAt(index - peselLength - firstNameLength - lastNameLength);
    } else {
      return NIP.charAt(index - peselLength - firstNameLength - lastNameLength - phoneNumberLength);
    }
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    // Implement subSequence based on the concatenated string
    int peselLength = pesel.length();
    int firstNameLength = firstName.length();
    int lastNameLength = lastName.length();
    int phoneNumberLength = phoneNumber.length();

    if (start < peselLength && end <= peselLength) {
      return pesel.subSequence(start, end);
    } else if (start >= peselLength && end <= peselLength + firstNameLength) {
      return firstName.subSequence(start - peselLength, end - peselLength);
    } else if (start >= peselLength + firstNameLength && end <= peselLength + firstNameLength + lastNameLength) {
      return lastName.subSequence(start - peselLength - firstNameLength, end - peselLength - firstNameLength);
    } else if (start >= peselLength + firstNameLength + lastNameLength && end <= peselLength + firstNameLength + lastNameLength + phoneNumberLength) {
      return phoneNumber.subSequence(start - peselLength - firstNameLength - lastNameLength, end - peselLength - firstNameLength - lastNameLength);
    } else {
      return NIP.subSequence(start - peselLength - firstNameLength - lastNameLength - phoneNumberLength, end - peselLength - firstNameLength - lastNameLength - phoneNumberLength);
    }
  }
}
