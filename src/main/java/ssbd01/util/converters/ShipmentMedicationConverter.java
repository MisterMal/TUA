package ssbd01.util.converters;

import ssbd01.dto.shipment.CreateShipmentMedicationDTO;
import ssbd01.dto.shipment.MedicationCreateShipmentDTO;
import ssbd01.dto.shipment.ShipmentMedicationDTO;
import ssbd01.entities.Medication;
import ssbd01.entities.ShipmentMedication;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShipmentMedicationConverter {

    public static List<ShipmentMedicationDTO> mapShipmentMedsToShipmentMedsDto(
            List<ShipmentMedication> shipmentMedications) {
        return shipmentMedications == null ?
                null :
                shipmentMedications.stream()
                        .filter(Objects::nonNull)
                        .map(ShipmentMedicationConverter::mapShipmentMedToShipmentMedDto)
                        .collect(Collectors.toList());
    }

    public static ShipmentMedicationDTO mapShipmentMedToShipmentMedDto(
            ShipmentMedication shipmentMedication) {
        return ShipmentMedicationDTO.builder()
                .quantity(shipmentMedication.getQuantity())
                .medication(MedicationConverter
                        .mapMedicationToMedicationDTO(shipmentMedication.getMedication()))
                .build();
    }

    public static List<ShipmentMedication> mapCreateShipmentMedsDtoToShipmentMeds(
            List<CreateShipmentMedicationDTO> createShipmentMedicationDTOS) {
        return createShipmentMedicationDTOS == null ?
                null :
                createShipmentMedicationDTOS.stream()
                        .filter(Objects::nonNull)
                        .map(ShipmentMedicationConverter::mapCreateShipmentMedDtoToShipmentMed)
                        .collect(Collectors.toList());
    }

    public static ShipmentMedication mapCreateShipmentMedDtoToShipmentMed(
            CreateShipmentMedicationDTO createShipmentMedicationDTO) {
        return ShipmentMedication.builder()
                .medication(ShipmentMedicationConverter.mapMedicationCreateShipmentDtoToMedication(
                                createShipmentMedicationDTO.getMedication()))
                .quantity(createShipmentMedicationDTO.getQuantity())
                .build();
    }

    public static Medication mapMedicationCreateShipmentDtoToMedication(
            MedicationCreateShipmentDTO medicationCreateShipmentDTO) {
        return Medication.createShipmentBuilder()
                .name(medicationCreateShipmentDTO.getName())
                .currentPrice(medicationCreateShipmentDTO.getPrice())
                .build();
    }
}
