package com.cricket.fantasy.repository.cricket;

import com.cricket.fantasy.entity.cricket.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    boolean existsByName(String name);
    Optional<Team> findByName(String name);
}