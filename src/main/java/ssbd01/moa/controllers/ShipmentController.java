package ssbd01.moa.controllers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ssbd01.common.AbstractController;
import ssbd01.config.EntityIdentitySignerVerifier;
import ssbd01.dto.shipment.CreateShipmentDTO;
import ssbd01.dto.shipment.ShipmentDTO;
import ssbd01.entities.EtagVerification;
import ssbd01.entities.EtagVersion;
import ssbd01.entities.Shipment;
import ssbd01.exceptions.ApplicationException;
import ssbd01.moa.managers.ShipmentManager;
import ssbd01.util.converters.ShipmentConverter;

import java.util.List;

@Path("shipment")
@RequestScoped
@DenyAll
public class ShipmentController extends AbstractController {

  @Inject private ShipmentManager shipmentManager;

  @Inject
  private EntityIdentitySignerVerifier entityIdentitySignerVerifier;

  @GET
  @Path("/")
  @RolesAllowed("readAllShipments")
  @Produces(MediaType.APPLICATION_JSON)
  public List<ShipmentDTO> readAllShipments() {
    List<Shipment> shipments = repeatTransaction(shipmentManager,
            () -> shipmentManager.getAllShipments());
    return ShipmentConverter.mapShipmentsToShipmentsDto(shipments);
  }

  @GET
  @Path("/{id}")
  @RolesAllowed("readShipment")
  @Produces(MediaType.APPLICATION_JSON)
  public ShipmentDTO readShipment(@PathParam("id") Long id) {
    Shipment shipment = repeatTransaction(shipmentManager,
            () -> shipmentManager.getShipment(id));
    return ShipmentConverter.mapShipmentToShipmentDto(shipment);
  }

  @POST
  @Path("/")
  @RolesAllowed("createShipment")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createShipment(@Valid CreateShipmentDTO shipmentDTO) {
    EtagVerification etagVerification = ShipmentConverter
            .mapCreateShipmentDtoToEtagVerification(shipmentDTO);

    shipmentDTO.getShipmentMedications().forEach(sm -> {
      EtagVersion etagVersion = etagVerification.getEtagVersionList().get(sm.getMedication().getName());
      if(!entityIdentitySignerVerifier.validateEntitySignature(etagVersion.getEtag()) ||
         !entityIdentitySignerVerifier.verifyEntityIntegrity(sm.getMedication(), etagVersion.getEtag())) {
        throw ApplicationException.createEtagNotValidException();
      }
    });
    Shipment shipment = ShipmentConverter.mapCreateShipmentDtoToShipment(shipmentDTO);

    repeatTransactionVoid(shipmentManager,
            () -> shipmentManager.createShipment(shipment, etagVerification));
    return Response.status(Response.Status.CREATED).build();
  }
}
