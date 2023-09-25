package com.cricket.fantasy.model.domain.cricsheet; 
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CricSheetInfo {
    private int balls_per_over;
    private String city;
    private ArrayList<String> dates;
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
    private ArrayList<String> teams;
    private Toss toss;
    private String venue;
}