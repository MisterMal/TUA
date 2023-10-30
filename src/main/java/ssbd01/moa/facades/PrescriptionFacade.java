package ssbd01.moa.facades;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.Prescription;

import java.util.List;
@ApplicationScoped
@Stateless
public class PrescriptionFacade extends AbstractFacade<Prescription> {
  @Inject
  public EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PrescriptionFacade() {
    super(Prescription.class);
  }

  @PermitAll
  @Override
  public List<Prescription> findAll() {
    return super.findAll();
  }

  // idk todo
}
