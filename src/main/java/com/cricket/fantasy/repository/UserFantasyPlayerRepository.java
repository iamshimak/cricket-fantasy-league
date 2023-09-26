package com.cricket.fantasy.repository;

import com.cricket.fantasy.entity.UserFantasyPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFantasyPlayerRepository extends JpaRepository<UserFantasyPlayer, Integer> {
}