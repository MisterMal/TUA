package ssbd01.dto.addAsAdmin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ssbd01.dto.register.BasicAccountDto;

@EqualsAndHashCode(callSuper=false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPatientAccountDto extends BasicAccountDto {

  @Builder
  public AddPatientAccountDto(
          String login,
          String password,
          String email,
          String language,
          String name,
          String lastName,
          String phoneNumber,
          String pesel,
          String nip) {
    super(login, password, email, language);
    this.name = name;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.pesel = pesel;
    this.nip = nip;
  }

  @NotNull
  @Size(max = 50, min = 2)
  private String name;

  @NotNull
  @Size(max = 50, min = 2)
  private String lastName;

  @NotNull
  @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number")
  private String phoneNumber;

  @NotNull
  @Pattern(regexp = "^\\d{11}$", message = "Invalid PESEL")
  private String pesel;

  @NotNull
  @Pattern(regexp = "^\\d{10}$", message = "Invalid NIP")
  private String nip;
}
