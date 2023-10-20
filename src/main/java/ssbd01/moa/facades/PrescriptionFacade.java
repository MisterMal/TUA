package ssbd01.moa.facades;

import jakarta.annotation.security.DenyAll;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.Prescription;

import java.util.List;

@Stateless
public class PrescriptionFacade extends AbstractFacade<Prescription> {
  @PersistenceContext(unitName = "ssbd01moaPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PrescriptionFacade() {
    super(Prescription.class);
  }

  @DenyAll
  @Override
  public List<Prescription> findAll() {
    return super.findAll();
  }

  // idk todo
}
