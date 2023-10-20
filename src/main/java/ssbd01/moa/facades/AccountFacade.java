package ssbd01.moa.facades;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.Account;
import ssbd01.interceptors.AccountFacadeExceptionsInterceptor;
import ssbd01.interceptors.GenericFacadeExceptionsInterceptor;
import ssbd01.interceptors.TrackerInterceptor;

import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
        GenericFacadeExceptionsInterceptor.class,
        AccountFacadeExceptionsInterceptor.class,
        TrackerInterceptor.class
})
@DenyAll
public class AccountFacade extends AbstractFacade<Account> {
    @PersistenceContext(unitName = "ssbd01moaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccountFacade() {
        super(Account.class);
    }

    @PermitAll
    public Account findByLogin(String login) {
        TypedQuery<Account> tq = em.createNamedQuery("account.findByLogin", Account.class);
        tq.setParameter(1, login);
        return tq.getSingleResult();
    }

    @PermitAll
    public Account findByLoginAndRefresh(String login) {
        TypedQuery<Account> tq = em.createNamedQuery("account.findByLogin", Account.class);
        tq.setParameter(1, login);
        Account foundAccount = tq.getSingleResult();
        getEntityManager().refresh(foundAccount);
        getEntityManager().flush();
        return foundAccount;
    }

    @PermitAll
    public Optional<Account> find(Long id) {
        return super.find(id);
    }
}


