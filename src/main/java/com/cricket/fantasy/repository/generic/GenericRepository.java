package com.cricket.fantasy.repository.generic;

import com.cricket.fantasy.entity.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GenericRepository <T extends BaseEntity> extends JpaRepository<T, Integer> {
}
