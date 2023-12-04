package ssbd01.dto.editAccount;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode(callSuper=false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditAccountDTO extends AbstractEditAccountDTO {

  @Builder
  public EditAccountDTO(String login, Long version, String email) {
    super(login, version);
    this.email =  email;
  }

  @NotNull
  @Email
  @Size(max = 50, min = 5)
  String email;
}
