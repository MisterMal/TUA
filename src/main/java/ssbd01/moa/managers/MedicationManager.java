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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.java.Log;
import ssbd01.common.AbstractManager;
import ssbd01.common.CommonManagerLocalInterface;
import ssbd01.entities.Account;
import ssbd01.entities.Category;
import ssbd01.entities.Medication;
import ssbd01.exceptions.ApplicationException;
import ssbd01.interceptors.GenericManagerExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;
import ssbd01.moa.facades.AccountFacade;
import ssbd01.moa.facades.CategoryFacade;
import ssbd01.moa.facades.MedicationFacade;

import java.util.List;
import java.util.Optional;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({GenericManagerExceptionsInterceptor.class,
        TrackerInterceptor.class})
@Log
@DenyAll
@ApplicationScoped
public class MedicationManager extends AbstractManager implements SessionSynchronization, CommonManagerLocalInterface {

    @Inject
    public MedicationFacade medicationFacade;

    @Inject
    public CategoryFacade categoryFacade;

    @Inject
    public AccountFacade accountFacade;

    @Context
    private SecurityContext context;


    @PermitAll
    public Medication findByName(String name) {
        return medicationFacade.findByName(name);
    }


    @RolesAllowed("createMedication")
    public Medication createMedication(Medication medication, String categoryName) {
        Category managedCategory = categoryFacade.findByName(categoryName);
        medication.setCategory(managedCategory);
        medication.setCreatedBy(getCurrentUserLogin());
        medicationFacade.create(medication);
        return medication;
    }


    @RolesAllowed("getAllMedications")
    public List<Medication> getAllMedications() {
        return medicationFacade.findAll();
    }


    @PermitAll
    public Medication getMedication(Long id) {
        throw new UnsupportedOperationException();
    }


    @DenyAll
    public Medication editMedication(Medication medication) {
        throw new UnsupportedOperationException();
    }


    @RolesAllowed("getMedicationDetails")
    public Medication getMedicationDetails(Long id) {
        Optional<Medication> medication = medicationFacade.findAndRefresh(id);
        if (medication.isEmpty()) {
            throw ApplicationException.createEntityNotFoundException();
        }
        return medication.get();
    }

    @RolesAllowed("getCurrentUser")
    public Account getCurrentUser() {
        return accountFacade.findByLogin(getCurrentUserLogin());
    }

    @PermitAll
    public String getCurrentUserLogin() {
        return context.getUserPrincipal().getName();
    }

}
