package com.cricket.fantasy.repository.cricket;

import com.cricket.fantasy.entity.cricket.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    boolean existsByName(String name);
    Optional<Player> findByName(String name);
    List<Player> findByTeam_Name(String name);
}