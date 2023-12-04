package ssbd01.moa.managers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.SessionSynchronization;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ssbd01.common.AbstractManager;
import ssbd01.common.CommonManagerLocalInterface;
import ssbd01.entities.Prescription;

import java.util.List;
@Transactional
@ApplicationScoped
public class PrescriptionManager extends AbstractManager implements SessionSynchronization, CommonManagerLocalInterface {
    @PermitAll
    public Prescription createPrescription(Prescription prescription) {
        throw new UnsupportedOperationException();
    }

    @PermitAll
    public Prescription getPrescription(Long id) {
        throw new UnsupportedOperationException();
    }

    @PermitAll
    public Prescription editPrescription(Prescription prescription) {
        throw new UnsupportedOperationException();
    }

    @PermitAll
    public List<Prescription> getAllPrescriptions() {
        throw new UnsupportedOperationException();
    }
}
