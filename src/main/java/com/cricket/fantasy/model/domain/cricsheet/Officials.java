package com.cricket.fantasy.model.domain.cricsheet; 
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Officials{
    private ArrayList<String> match_referees;
    private ArrayList<String> umpires;
}
