package ssbd01.moa.facades;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.OrderMedication;
import ssbd01.interceptors.GenericFacadeExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;
@ApplicationScoped
@Stateless
@Interceptors({
        GenericFacadeExceptionsInterceptor.class,
        TrackerInterceptor.class
})
public class OrderMedicationFacade extends AbstractFacade<OrderMedication> {
  @Inject
  public EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public OrderMedicationFacade() {
    super(OrderMedication.class);
  }
}
