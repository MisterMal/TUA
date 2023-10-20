package ssbd01.dto.prescrription;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ssbd01.dto.AbstractEntityDTO;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PrescriptionDTO extends AbstractEntityDTO {

    @NotNull
    private String prescriptionNumber;

    @Builder
    public PrescriptionDTO(
            Long id,
            Long version,
            String prescriptionNumber
    ) {
        super(id, version);
        this.prescriptionNumber = prescriptionNumber;
    }
}
