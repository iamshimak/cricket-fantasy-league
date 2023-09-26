package com.cricket.fantasy.common;

import com.cricket.fantasy.entity.Player;
import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;

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

    @SneakyThrows
    public static CricSheetMatchData getMatchData(CricksheetMatch cricksheetMatch) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(cricksheetMatch.getJson(), CricSheetMatchData.class);
    }
}
