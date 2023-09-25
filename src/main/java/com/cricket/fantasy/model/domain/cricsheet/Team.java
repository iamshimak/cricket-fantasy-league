package com.cricket.fantasy.model.domain.cricsheet;

import lombok.Data;

import java.util.List;

@Data
public class Team {
    private String name;
    private List<CricsheetPlayer> players;
}
