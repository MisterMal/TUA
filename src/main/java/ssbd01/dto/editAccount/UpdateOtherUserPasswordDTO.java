package ssbd01.dto.editAccount;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode(callSuper=false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOtherUserPasswordDTO extends AbstractEditAccountDTO {

  @Builder
  public UpdateOtherUserPasswordDTO(String login, Long version, String password) {
    super(login, version);
    this.password = password;
  }

  @NotNull
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Invalid password")
  @Size(max = 50, min = 8)
  String password;
}
