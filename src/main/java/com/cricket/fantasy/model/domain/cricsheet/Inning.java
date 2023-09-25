package com.cricket.fantasy.model.domain.cricsheet; 
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Inning{
    private String team;
    private ArrayList<Over> overs;
    private ArrayList<Powerplay> powerplays;
    private Target target;
}
