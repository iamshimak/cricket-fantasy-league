package com.cricket.fantasy.model.domain.cricsheet; 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CricSheetMatchData {
    private Meta meta;
    private CricSheetInfo info;
    private ArrayList<Inning> innings;
}
