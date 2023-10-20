package ssbd01.dto;

import lombok.*;

import java.util.Locale;
import java.util.Set;

@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelfAccountWithAccessLevelDTO extends AccountAndAccessLevelsDTO {
    @Builder(builderMethodName = "childBuilder")
    public SelfAccountWithAccessLevelDTO(
            Long id,
            Long version,
            Set<AccessLevelDTO> accessLevels,
            String login,
            Boolean active,
            Boolean confirmed,
            String email,
            Locale language) {
        super(id, version, accessLevels, login, email, active, confirmed);
        this.language = language.getLanguage();
    }

    private String language;
}
