package ssbd01.moa.facades;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.Shipment;
import ssbd01.interceptors.GenericFacadeExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;

import java.util.List;
import java.util.Optional;
@ApplicationScoped
@Stateless
@Interceptors({
        GenericFacadeExceptionsInterceptor.class,
        TrackerInterceptor.class
})
public class ShipmentFacade extends AbstractFacade<Shipment> {

  @Inject
  public EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ShipmentFacade() {
    super(Shipment.class);
  }

  @PermitAll
  public void create(Shipment shipment) {
    super.create(shipment);
  }

  @PermitAll
  public List<Shipment> findAllAndRefresh() {
    return getEntityManager()
            .createQuery("select s from Shipment s left join fetch s.shipmentMedications")
            .getResultList();
  }

  public List<Shipment> findAllNotAlreadyProcessed() {
    return getEntityManager().createNamedQuery("Shipment.findAllNotProcessed", Shipment.class).getResultList();
  }

  @PermitAll
  public Optional<Shipment> findAndRefresh(Long id) {
    return super.findAndRefresh(id);
  }
}
