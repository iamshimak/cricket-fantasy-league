package com.cricket.fantasy.repository;

import com.cricket.fantasy.entity.Player;
import com.cricket.fantasy.entity.FantasyPlayer;
import com.cricket.fantasy.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FantasyPlayerRepository extends JpaRepository<FantasyPlayer, Integer> {
    Optional<FantasyPlayer> findByPlayer(Player player);
    Optional<FantasyPlayer> findByPlayerAndMatch(Player player, Match match);
    List<FantasyPlayer> findByMatch(Match match);
}