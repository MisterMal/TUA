package ssbd01.moa.facades;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.Category;
import ssbd01.interceptors.GenericFacadeExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;

import java.util.List;
import java.util.Optional;
@ApplicationScoped
@Stateless(name = "CategoryFacade")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@DenyAll
@Interceptors({
        GenericFacadeExceptionsInterceptor.class,
        TrackerInterceptor.class
})
public class CategoryFacade extends AbstractFacade<Category> {
  @Inject
  public EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public CategoryFacade() {
    super(Category.class);
  }

  @Override
  @PermitAll
  public List<Category> findAll() {
    TypedQuery<Category> tq = em.createNamedQuery("category.findAll", Category.class);
    return tq.getResultList();
  }

  @Override
  @RolesAllowed("createCategory")
  public void create(Category category) {super.create(category);}

  @Override
  @PermitAll
  public void edit(Category category) {super.edit(category);}

  @PermitAll
  public Optional<Category> find(Long id) {return super.find(id);}

  @PermitAll
  public Category findByName(String name) {
    try {
      TypedQuery<Category> tq = em.createNamedQuery("category.findByName", Category.class);
      tq.setParameter(1, name);
      return tq.getSingleResult();
    } catch (NoResultException ex) {
      return null;
    }
  }
}
