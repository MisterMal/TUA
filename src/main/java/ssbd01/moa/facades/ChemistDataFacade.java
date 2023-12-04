package ssbd01.moa.facades;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.ChemistData;
@ApplicationScoped
@Stateless(name = "ChemistDataFacadeMoa")
public class ChemistDataFacade extends AbstractFacade<ChemistData> {

  @Inject
  public EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ChemistDataFacade() {
    super(ChemistData.class);
  }
}
