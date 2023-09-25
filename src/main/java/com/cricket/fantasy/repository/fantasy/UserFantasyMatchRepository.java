package com.cricket.fantasy.repository.fantasy;

import com.cricket.fantasy.entity.fantasy.user.UserFantasyMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFantasyMatchRepository extends JpaRepository<UserFantasyMatch, Integer> {
}