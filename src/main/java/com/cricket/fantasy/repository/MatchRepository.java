package com.cricket.fantasy.repository;

import com.cricket.fantasy.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Integer> {

    Optional<Match> findByTeams(String teams);
}