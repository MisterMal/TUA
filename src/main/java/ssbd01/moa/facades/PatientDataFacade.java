package ssbd01.moa.facades;

import jakarta.annotation.security.DenyAll;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.PatientData;
import ssbd01.interceptors.GenericFacadeExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;

import java.util.List;

@Stateless
@Interceptors({
        GenericFacadeExceptionsInterceptor.class,
        TrackerInterceptor.class
})
public class PatientDataFacade extends AbstractFacade<PatientData> {

  @PersistenceContext(unitName = "ssbd01moaPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PatientDataFacade() {
    super(PatientData.class);
  }
  @DenyAll
  public List<PatientData> findAll() {
    TypedQuery<PatientData> tq = em.createNamedQuery("patientData.findAll", PatientData.class);
    return tq.getResultList();
  }
}
