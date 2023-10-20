package ssbd01.moa.facades;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.ChemistData;

@Stateless(name = "ChemistDataFacadeMoa")
public class ChemistDataFacade extends AbstractFacade<ChemistData> {

  @PersistenceContext(unitName = "ssbd01moaPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ChemistDataFacade() {
    super(ChemistData.class);
  }
}
