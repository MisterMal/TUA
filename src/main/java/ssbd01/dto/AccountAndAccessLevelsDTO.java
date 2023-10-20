package ssbd01.dto;

import lombok.*;

import java.util.Set;

@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountAndAccessLevelsDTO extends AccountDTO {

  @Builder(builderMethodName = "accountAndAccessLevelsBuilder")
  public AccountAndAccessLevelsDTO(
      Long id,
      Long version,
      Set<AccessLevelDTO> accessLevels,
      String login,
      String email,
      Boolean active,
      Boolean confirmed) {
    super(id, version, login, active, confirmed, email);
    this.accessLevels = accessLevels;
  }

  @ToString.Exclude Set<AccessLevelDTO> accessLevels;
}
