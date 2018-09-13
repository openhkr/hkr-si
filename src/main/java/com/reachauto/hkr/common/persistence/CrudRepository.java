package com.reachauto.hkr.common.persistence;

import java.util.List;

/**
 * Created by haojr on 17/06/05.
 */
public interface CrudRepository<T> {

    void create(T t);

    int update(T t);

    int delete(Long id);

    int delete(List<Long> ids);

    T findById(Long id);

}
