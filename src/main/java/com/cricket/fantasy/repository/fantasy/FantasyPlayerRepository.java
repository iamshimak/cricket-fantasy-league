package com.cricket.fantasy.repository.fantasy;

import com.cricket.fantasy.entity.cricket.Player;
import com.cricket.fantasy.entity.fantasy.FantasyPlayer;
import com.cricket.fantasy.entity.fantasy.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FantasyPlayerRepository extends JpaRepository<FantasyPlayer, Integer> {

    Optional<FantasyPlayer> findByPlayer(Player player);
    Optional<FantasyPlayer> findByPlayerAndMatch(Player player, Match match);
}