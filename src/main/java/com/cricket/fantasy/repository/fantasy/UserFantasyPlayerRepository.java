package com.cricket.fantasy.repository.fantasy;

import com.cricket.fantasy.entity.fantasy.user.UserFantasyPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFantasyPlayerRepository extends JpaRepository<UserFantasyPlayer, Integer> {
}