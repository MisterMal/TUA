package ssbd01.moa.managers;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import ssbd01.common.AbstractManager;
import ssbd01.common.CommonManagerLocalInterface;
import ssbd01.entities.EtagVerification;
import ssbd01.entities.EtagVersion;
import ssbd01.entities.Medication;
import ssbd01.entities.Shipment;
import ssbd01.exceptions.ApplicationException;
import ssbd01.interceptors.GenericManagerExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;
import ssbd01.moa.facades.MedicationFacade;
import ssbd01.moa.facades.ShipmentFacade;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Transactional
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({GenericManagerExceptionsInterceptor.class,
        TrackerInterceptor.class})
@Log
@PermitAll
@ApplicationScoped
public class ShipmentManager extends AbstractManager implements SessionSynchronization, CommonManagerLocalInterface {

  @Inject
  public ShipmentFacade shipmentFacade;

  @Inject
  public MedicationFacade medicationFacade;

  @PermitAll
  public void createShipment(Shipment shipment, EtagVerification etagVerification) {
    shipment.getShipmentMedications().forEach(sm -> {
      EtagVersion etagVersion = etagVerification.getEtagVersionList().get(sm.getMedication().getName());
      Medication medication = medicationFacade.findByName(sm.getMedication().getName());
      if(!etagVersion.getVersion().equals(medication.getVersion())) {
        throw ApplicationException.createOptimisticLockException();
      }
      BigDecimal newPrice = sm.getMedication().getCurrentPrice();

      sm.setShipment(shipment);
      sm.setMedication(medication);
      sm.getMedication().setCurrentPrice(newPrice);
    });
    shipmentFacade.create(shipment);
  }

  @PermitAll
  public List<Shipment> getAllShipments() {
    return shipmentFacade.findAllAndRefresh();
  }

  @PermitAll
  public Shipment getShipment(Long id) {
    Optional<Shipment> shipmentOpt = shipmentFacade.findAndRefresh(id);
    if (shipmentOpt.isEmpty()) {
      throw ApplicationException.createEntityNotFoundException();
    }
    return shipmentOpt.get();
  }
}
