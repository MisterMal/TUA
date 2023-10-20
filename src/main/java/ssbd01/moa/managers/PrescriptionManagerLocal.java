package ssbd01.moa.managers;


import ssbd01.common.CommonManagerLocalInterface;
import ssbd01.entities.Prescription;

import java.util.List;

public interface PrescriptionManagerLocal extends CommonManagerLocalInterface {

    Prescription createPrescription(Prescription prescription);

    Prescription getPrescription(Long id);

    Prescription editPrescription(Prescription prescription);

    List<Prescription> getAllPrescriptions();
}
