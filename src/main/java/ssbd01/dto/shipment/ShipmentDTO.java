package ssbd01.dto.shipment;

import lombok.*;
import ssbd01.dto.AbstractEntityDTO;

import java.util.List;

@Data()
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDTO extends AbstractEntityDTO {

    private String shipmentDate;

    private List<ShipmentMedicationDTO> shipmentMedications;

    @Builder
    public ShipmentDTO(Long id, Long version, String shipmentDate,
                       List<ShipmentMedicationDTO> shipmentMedications) {
        super(id, version);
        this.shipmentDate = shipmentDate;
        this.shipmentMedications = shipmentMedications;
    }
}
