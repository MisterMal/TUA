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
import jakarta.inject.Qualifier;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.java.Log;
import ssbd01.common.AbstractManager;
import ssbd01.common.CommonManagerLocalInterface;
import ssbd01.entities.Account;
import ssbd01.entities.Category;
import ssbd01.exceptions.ApplicationException;
import ssbd01.interceptors.GenericManagerExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;
import ssbd01.moa.facades.AccountFacade;
import ssbd01.moa.facades.CategoryFacade;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({GenericManagerExceptionsInterceptor.class, TrackerInterceptor.class})
@Log
@Stateful
@DenyAll
@ApplicationScoped
public class CategoryManager extends AbstractManager implements SessionSynchronization, CommonManagerLocalInterface {
    public CategoryManager() {
    }

    @Inject
    public CategoryFacade categoryFacade;

    @Inject
    public AccountFacade accountFacade;

    @Context
    private SecurityContext context;

    @RolesAllowed("getAllCategories")
    public List<Category> getAllCategories() {
        return categoryFacade.findAll();    }

    @RolesAllowed("createCategory")
    public Category createCategory(Category category) {
        Category existingCategory = categoryFacade.findByName(category.getName());
        if (existingCategory != null) {
            throw ApplicationException.createCategoryAlreadyExistsException();
        }
        category.setCreatedBy(getCurrentUserLogin());
        categoryFacade.create(category);
        return category;
    }

    @PermitAll
    public Category getCategory(Long id) {
        Optional<Category> category = categoryFacade.find(id);
        if (category.isPresent()) {
            return category.get();
        } else {
            throw ApplicationException.createEntityNotFoundException();
        }
    }

    @RolesAllowed("editCategory")
    public Category editCategory(Long id, Category category, Long version) {
        Optional<Category> categoryOptional = categoryFacade.find(id);
        if (categoryOptional.isPresent()) {
            Category categoryToUpdate = categoryOptional.get();
            if (!Objects.equals(categoryToUpdate.getVersion(), version)) {
                throw ApplicationException.createOptimisticLockException();
            }
            categoryToUpdate.setName(category.getName());
            categoryToUpdate.setIsOnPrescription(category.getIsOnPrescription());
            categoryToUpdate.setModifiedBy(getCurrentUserLogin());
            categoryToUpdate.setModificationDate(new Date());
            categoryFacade.edit(categoryToUpdate);
            return categoryToUpdate;
        } else {
            throw ApplicationException.createEntityNotFoundException();
        }
    }

    @PermitAll
    public Category findByName(String name) {
        return categoryFacade.findByName(name);
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
