package ssbd01.dto.editAccessLevel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ssbd01.common.SignableEntity;
import ssbd01.entities.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractEditAccessLevelDTO implements SignableEntity {

    @NotNull
    private Long version;

    @NotNull
    private Role role;

    @Override
    @JsonIgnore
    public String getSignablePayload() {
        return String.format("%s.%d", role, version);
    }
}
