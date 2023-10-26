package ssbd01.moa.controllers;

import jakarta.annotation.security.DenyAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import ssbd01.common.AbstractController;
import ssbd01.dto.prescrription.PrescriptionDTO;
import ssbd01.moa.managers.PrescriptionManager;

import java.util.List;

@Path("prescription")
@RequestScoped
@DenyAll
public class PrescriptionController extends AbstractController {

    @Inject private PrescriptionManager prescriptionManagerLocal;

    @GET
    @Path("/")
    @DenyAll
    public List<PrescriptionDTO> readAllPrescriptions() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GET
    @Path("/{id}")
    @DenyAll
    public Response readPrescription(@PathParam("id") Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @POST
    @Path("/create-prescription")
    @DenyAll
    public Response createPrescription(PrescriptionDTO prescriptionDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PUT
    @Path("/update-prescription")
    @DenyAll
    public PrescriptionDTO updatePrescription(PrescriptionDTO prescriptionDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
