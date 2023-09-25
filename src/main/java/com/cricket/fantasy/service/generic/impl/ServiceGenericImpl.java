package com.cricket.fantasy.service.generic.impl;

import com.cricket.fantasy.entity.base.BaseEntity;
import com.cricket.fantasy.repository.generic.GenericRepository;
import com.cricket.fantasy.service.generic.ServiceGeneric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceGenericImpl <T extends BaseEntity> implements ServiceGeneric<T> {

    @Autowired
    protected GenericRepository<T> genericRepository;

    @Override
    public List<T> findAll() {
        return genericRepository.findAll();
    }

    @Override
    public Optional<T> findById(int id) {
        return genericRepository.findById(id);
    }

    @Override
    public T save(T entity) {
        return genericRepository.save(entity);
    }

    @Override
    public void delete(int id) {
        genericRepository.deleteById(id);
    }
}
