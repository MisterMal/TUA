package ssbd01.dto.editAccount.grant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ssbd01.dto.editAccount.AbstractEditAccountDTO;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrantPatientDataDTO extends AbstractEditAccountDTO {

  @Builder
  public GrantPatientDataDTO(String login, Long version, String pesel,
                             String firstName, String lastName, String phoneNumber,
                             String nip) {
    super(login, version);
    this.pesel = pesel;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.nip = nip;
  }

  @NotNull
  @Pattern(regexp = "^\\d{11}$", message = "Invalid PESEL")
  private String pesel;

  @NotNull
  @Size(max = 50, min = 2)
  private String firstName;

  @NotNull
  @Size(max = 50, min = 2)
  private String lastName;

  @NotNull
  @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number")
  private String phoneNumber;

  @NotNull
  @Pattern(regexp = "^\\d{10}$", message = "Invalid NIP")
  private String nip;
}
