package com.cricket.fantasy.controller.generic;

import com.cricket.fantasy.entity.base.BaseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface ControllerGeneric <T extends BaseEntity> {

    ResponseEntity<Object> save(@RequestBody T entity);

    ResponseEntity<T> findAll();

    ResponseEntity<String> delete(@PathVariable int id);
}
