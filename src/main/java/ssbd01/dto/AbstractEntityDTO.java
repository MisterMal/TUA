package ssbd01.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractEntityDTO {

  @NotNull
  private Long id;

  @NotNull
  private Long version;
}
