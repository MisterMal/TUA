package ssbd01.common;

public interface AbstractFacadeLocal<T> {
  void create(T entity);

  T find(Object id);

  void edit(T entity);

  void remove(T entity);
}
