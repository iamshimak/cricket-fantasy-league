package com.cricket.fantasy.model.domain.cricsheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CricSheetInfo {
    private int balls_per_over;
    private String city;
    private List<String> dates;
    private Event event;
    private String gender;
    private String match_type;
    private Officials officials;
    private Outcome outcome;
    private int overs;
    private Map<String, List<String>> players;
    private Registry registry;
    private String season;
    private String team_type;
    private List<String> teams;
    private Toss toss;
    private String venue;
    @JsonProperty("player_of_match")
    private List<String> playerOfMatch;
}