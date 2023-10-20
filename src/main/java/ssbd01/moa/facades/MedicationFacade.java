package ssbd01.moa.facades;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.Medication;
import ssbd01.interceptors.GenericFacadeExceptionsInterceptor;
import ssbd01.interceptors.MedicationFacadeExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;

import java.util.List;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
        GenericFacadeExceptionsInterceptor.class,
        MedicationFacadeExceptionsInterceptor.class,
        TrackerInterceptor.class
})
public class MedicationFacade extends AbstractFacade<Medication> {
  @PersistenceContext(unitName = "ssbd01moaPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public MedicationFacade() {
    super(Medication.class);
  }

  @RolesAllowed("getAllMedications")
  public List<Medication> findAll() {
    TypedQuery<Medication> tq = em.createNamedQuery("medication.findAll", Medication.class);
    return tq.getResultList();
  }

  @Override
  @RolesAllowed("createMedication")
  public void create(Medication medication) {
    super.create(medication);
  }

  @Override
  @PermitAll
  public void edit(Medication medication) { super.edit(medication);}

  @Override
  @PermitAll
  public Optional<Medication> find(Object id) { return super.find(id);}

  @Override
  @PermitAll
  public Optional<Medication> findAndRefresh(Object id) {
    return super.findAndRefresh(id);
  }

  @PermitAll
  public Medication findByName(String name) {
    TypedQuery<Medication> tq = em.createNamedQuery("medication.findByName", Medication.class);
    tq.setParameter(1, name);
    List<Medication> medications = tq.getResultList();
    if (!medications.isEmpty()) {
      return medications.get(0);
    }
    return null;
  }
}
