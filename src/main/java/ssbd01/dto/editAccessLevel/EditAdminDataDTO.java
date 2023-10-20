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
public class EditAdminDataDTO extends AbstractEditAccessLevelDTO {

  @Builder
  public EditAdminDataDTO(Long version, String workPhoneNumber) {
    super(version, Role.ADMIN);
    this.workPhoneNumber = workPhoneNumber;
  }

  @NotNull
  @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number")
  private String workPhoneNumber;
}
