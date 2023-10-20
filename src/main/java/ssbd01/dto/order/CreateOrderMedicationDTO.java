package ssbd01.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderMedicationDTO {
    @NotNull
    private String name;
    @NotNull
    private Integer quantity;
}

