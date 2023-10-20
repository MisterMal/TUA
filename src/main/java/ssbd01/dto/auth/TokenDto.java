package ssbd01.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    @NotNull
    private String jwtToken;
}
