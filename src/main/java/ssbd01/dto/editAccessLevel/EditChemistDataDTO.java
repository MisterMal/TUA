package ssbd01.dto.editAccessLevel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ssbd01.entities.Role;

@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditChemistDataDTO extends AbstractEditAccessLevelDTO {

  @Builder
  public EditChemistDataDTO(Long version, String licenseNumber) {
    super(version, Role.CHEMIST);
    this.licenseNumber = licenseNumber;
  }

  @NotNull
  @Pattern(regexp = "^\\d{6}$", message = "Invalid license number")
  private String licenseNumber;
}
