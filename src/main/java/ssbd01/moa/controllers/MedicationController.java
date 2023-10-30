package ssbd01.moa.controllers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import ssbd01.common.AbstractController;
import ssbd01.config.EntityIdentitySignerVerifier;
import ssbd01.dto.medication.AddMedicationDTO;
import ssbd01.dto.medication.MedicationDTO;
import ssbd01.entities.Medication;
import ssbd01.moa.managers.CategoryManager;
import ssbd01.moa.managers.MedicationManager;
import ssbd01.util.converters.MedicationConverter;


import java.util.List;

@Path("medication")
@RequestScoped
@DenyAll
@Log
public class MedicationController extends AbstractController {

    @Inject
    public MedicationManager medicationManager;

    @Inject
    public CategoryManager categoryManager;

    @Inject
    public EntityIdentitySignerVerifier entityIdentitySignerVerifier;


    //moa 1
    @GET
    @Path("/")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<MedicationDTO> getAllMedications() {
        List<Medication> medications =
                repeatTransaction(medicationManager, () -> medicationManager.getAllMedications());
        return medications.stream().map(MedicationConverter::mapMedicationToMedicationDTO).toList();
    }

    //chyba moa 19 ale może w shimpment
    @POST
    @Path("/add-medication")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMedication(@Valid AddMedicationDTO addMedicationDTO) {
        Medication medication =
                Medication.builder()
                        .name(addMedicationDTO.getName())
                        .stock(addMedicationDTO.getStock())
                        .currentPrice(addMedicationDTO.getPrice())
                        .build();

        repeatTransaction(medicationManager, () -> medicationManager.createMedication(medication,
                addMedicationDTO.getCategoryName()));
        return Response.status(Response.Status.CREATED).build();
    }

    //moa 20
    @PUT
    @Path("/{id}/edit-medication")
    @PermitAll
    public AddMedicationDTO editMedication(AddMedicationDTO addMedicationDTO) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //moa 2???
    @GET
    @Path("/{id}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedicationDetails(@PathParam("id") Long id) {
        Medication medication = repeatTransaction(medicationManager, () -> medicationManager.getMedicationDetails(id));
        MedicationDTO medicationDTO = MedicationConverter.mapMedicationToMedicationDTO(medication);
        String etag = entityIdentitySignerVerifier.calculateEntitySignature(medicationDTO);
        return Response.ok(medicationDTO).tag(etag).build();
    }

}