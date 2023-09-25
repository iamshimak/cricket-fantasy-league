package com.cricket.fantasy.model.domain.cricsheet; 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
public class Delivery{
    private String batter;
    private String bowler;
    private String non_striker;
    private Runs runs;
    private List<Wicket> wickets;
    private Review review;
    private Extras extras;
    private Replacements replacements;
}
