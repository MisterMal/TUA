package ssbd01.mok.facades;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.PatientData;

@Stateless
public class PatientDataFacade extends AbstractFacade<PatientData> {
  @PersistenceContext(unitName = "ssbd01mokPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PatientDataFacade() {
    super(PatientData.class);
  }
}
