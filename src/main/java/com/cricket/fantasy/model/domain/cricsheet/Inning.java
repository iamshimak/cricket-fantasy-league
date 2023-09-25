package com.cricket.fantasy.model.domain.cricsheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Inning {
    private String team;
    private List<Over> overs;
    private List<Powerplay> powerplays;
    private Target target;
    @JsonProperty("absent_hurt")
    private List<String> absentHurt;
    @JsonProperty("super_over")
    private boolean superOver;
    @JsonProperty("miscounted_overs")
    private Map<String, Object> miscountedOvers;
}
