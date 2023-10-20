package ssbd01.dto.addAsAdmin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssbd01.dto.register.BasicAccountDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAdminAccountDto extends BasicAccountDto {

  @NotNull
  @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number")
  private String workPhoneNumber;

  @Builder
  public AddAdminAccountDto(
      String login,
      String password,
      String email,
      String language,
      String workPhoneNumber) {
    super(login, password, email, language);
    this.workPhoneNumber = workPhoneNumber;
  }
}
