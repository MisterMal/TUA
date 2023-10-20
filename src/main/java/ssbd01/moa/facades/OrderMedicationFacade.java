package ssbd01.moa.facades;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.OrderMedication;
import ssbd01.interceptors.GenericFacadeExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;

@Stateless
@Interceptors({
        GenericFacadeExceptionsInterceptor.class,
        TrackerInterceptor.class
})
public class OrderMedicationFacade extends AbstractFacade<OrderMedication> {
  @PersistenceContext(unitName = "ssbd01moaPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public OrderMedicationFacade() {
    super(OrderMedication.class);
  }
}
