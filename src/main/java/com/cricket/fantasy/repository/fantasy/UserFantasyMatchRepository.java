package com.cricket.fantasy.repository.fantasy;

import com.cricket.fantasy.entity.fantasy.user.UserFantasySquad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFantasyMatchRepository extends JpaRepository<UserFantasySquad, Integer> {
}