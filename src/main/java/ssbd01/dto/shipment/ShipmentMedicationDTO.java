package ssbd01.dto.shipment;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssbd01.dto.medication.MedicationDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentMedicationDTO {
    @Min(value = 0)
    private Integer quantity;
    private MedicationDTO medication;
}
