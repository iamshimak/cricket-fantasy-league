package com.cricket.fantasy.common;

import com.cricket.fantasy.entity.cricket.Player;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public final class CricksheetHelper {

    public static Player findPlayer(List<Player> players, String name) {
        Optional<Player> fantasyPlayer = players.stream()
                .filter(player -> player.getName().equals(name))
                .findFirst();

        if (fantasyPlayer.isEmpty()) {
            throw new EntityNotFoundException(String.format("Player %s not found", name));
        }

        return fantasyPlayer.get();
    }
}
