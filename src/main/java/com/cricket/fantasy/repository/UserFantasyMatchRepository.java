package com.cricket.fantasy.repository;

import com.cricket.fantasy.entity.UserFantasySquad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFantasyMatchRepository extends JpaRepository<UserFantasySquad, Integer> {
}