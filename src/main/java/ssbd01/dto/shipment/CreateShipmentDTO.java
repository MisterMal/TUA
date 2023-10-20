package ssbd01.dto.shipment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateShipmentDTO {
    private String shipmentDate;

    @NotEmpty
    private List<@Valid CreateShipmentMedicationDTO> shipmentMedications;
}
