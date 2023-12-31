package ssbd01.mok.facades;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import ssbd01.common.AbstractFacade;
import ssbd01.entities.Token;
import ssbd01.entities.TokenType;
import ssbd01.interceptors.GenericFacadeExceptionsInterceptor;
import ssbd01.interceptors.TokenFacadeInterceptor;
import ssbd01.interceptors.TrackerInterceptor;

import java.util.Date;
import java.util.List;

import static jakarta.transaction.Transactional.TxType.MANDATORY;

@ApplicationScoped
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
        GenericFacadeExceptionsInterceptor.class,
        TokenFacadeInterceptor.class,
        TrackerInterceptor.class
})
@PermitAll
public class TokenFacade extends AbstractFacade<Token> {
  @Inject
  public EntityManager em;

  public TokenFacade() {
    super(Token.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  @PermitAll
  public void edit(Token token) {
    super.edit(token);
  }

  @PermitAll
  public List<Token> findByTypeAndBeforeGivenData(TokenType type, Date date) {
    TypedQuery<Token> tq =
        em.createNamedQuery(
            "token.findByTypeAndBeforeGivenDataAndNotUsedAndNotPreviouslySent", Token.class);
    tq.setParameter(1, type);
    tq.setParameter(2, date);
    return tq.getResultList();
  }

  @PermitAll
  public Token findByCode(String code) {
    TypedQuery<Token> tq = em.createNamedQuery("token.findByCode", Token.class);
    tq.setParameter(1, code);
    return tq.getSingleResult();
  }

  @Transactional(MANDATORY)
  @Override
  @PermitAll
  public void create(Token token) {
    super.create(token);
  }

  @Override
  @PermitAll
  public void remove(Token token) {
    super.remove(token);
  }
}
