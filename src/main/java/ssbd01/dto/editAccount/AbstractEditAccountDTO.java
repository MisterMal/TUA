package ssbd01.dto.editAccount;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ssbd01.common.SignableEntity;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractEditAccountDTO implements SignableEntity {

    @NotNull
    private String login;

    @NotNull
    private Long version;

    @Override
    @JsonIgnore
    public String getSignablePayload() {
        return String.format("%s.%d", login, version);
    }
}
