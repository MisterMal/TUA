package ssbd01.util.converters;

import ssbd01.dto.prescrription.PrescriptionDTO;
import ssbd01.entities.Prescription;

public class PrescriptionConverter {

  private PrescriptionConverter() {}

  public static PrescriptionDTO mapPrescriptionToPrescriptionDTO(Prescription prescription) {
    return PrescriptionDTO.builder()
        .prescriptionNumber(prescription.getPrescriptionNumber())
        .build();
  }
}
