package com.cricket.fantasy.model.domain.cricsheet; 
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Match {
    private List<Team> teams;
}
