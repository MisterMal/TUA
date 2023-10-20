package ssbd01.dto.editAccount.grant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ssbd01.dto.editAccount.AbstractEditAccountDTO;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrantChemistDataDTO extends AbstractEditAccountDTO {

  @Builder
  public GrantChemistDataDTO(String login, Long version, String licenseNumber) {
    super(login, version);
    this.licenseNumber = licenseNumber;
  }

  @NotNull
  @Pattern(regexp = "^\\d{6}$", message = "Invalid license number")
  private String licenseNumber;
}
