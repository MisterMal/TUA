package ssbd01.moa.managers;

import jakarta.annotation.security.DenyAll;
import jakarta.ejb.SessionSynchronization;
import jakarta.enterprise.context.ApplicationScoped;
import ssbd01.common.AbstractManager;
import ssbd01.common.CommonManagerLocalInterface;
import ssbd01.entities.Prescription;

import java.util.List;
@ApplicationScoped
public class PrescriptionManager extends AbstractManager implements SessionSynchronization, CommonManagerLocalInterface {
    @DenyAll
    public Prescription createPrescription(Prescription prescription) {
        throw new UnsupportedOperationException();
    }

    @DenyAll
    public Prescription getPrescription(Long id) {
        throw new UnsupportedOperationException();
    }

    @DenyAll
    public Prescription editPrescription(Prescription prescription) {
        throw new UnsupportedOperationException();
    }

    @DenyAll
    public List<Prescription> getAllPrescriptions() {
        throw new UnsupportedOperationException();
    }
}
