package br.com.katalog.katalogweb.models;

import java.util.List;

/**
 * Created by luciano on 28/10/2016.
 */

public interface DAO<E> {

    public E getById(Object id);

    public String insert(E entity);

    public void delete(E entity);

    public List<E> getAll();
}
