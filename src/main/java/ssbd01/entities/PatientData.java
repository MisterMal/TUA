package ssbd01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "patient_data")
@DiscriminatorValue("PATIENT")
@NoArgsConstructor
@ToString(callSuper = true)
@NamedQuery(name = "patientData.findAll", query = "SELECT o FROM PatientData o")
@AllArgsConstructor
public class PatientData extends AccessLevel implements Serializable {

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
}
