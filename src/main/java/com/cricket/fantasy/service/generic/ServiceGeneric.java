package com.cricket.fantasy.service.generic;

import com.cricket.fantasy.entity.base.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface ServiceGeneric <T extends BaseEntity> {

    List<T> findAll();
    Optional<T> findById(int id);
    T save(T entity);
    void delete(int id);
}
