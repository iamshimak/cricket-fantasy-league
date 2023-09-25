package com.cricket.fantasy.model.domain.cricsheet; 
import lombok.Data;

import java.util.ArrayList;

@Data
public class CricSheetMatchData {
    private Meta meta;
    private CricSheetInfo info;
    private ArrayList<Inning> innings;
}
