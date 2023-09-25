package com.cricket.fantasy.controller.generic.impl;

import com.cricket.fantasy.controller.generic.ControllerGeneric;
import com.cricket.fantasy.entity.base.BaseEntity;
import com.cricket.fantasy.service.generic.ServiceGeneric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ResponseBody
public class ControllerGenericImpl<T extends BaseEntity> implements ControllerGeneric<T> {

    @Autowired
    private ServiceGeneric<T> genericService;

    @Override
    @PostMapping
    public ResponseEntity<Object> save(T entity) {
        return new ResponseEntity(genericService.save(entity), HttpStatus.OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<T> findAll() {
        return new ResponseEntity(genericService.findAll(), HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        genericService.delete(id);
        return new ResponseEntity("Sucesso ao apagar!", HttpStatus.OK);
    }
}
