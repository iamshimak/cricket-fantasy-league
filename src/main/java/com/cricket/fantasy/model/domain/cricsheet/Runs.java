package com.cricket.fantasy.model.domain.cricsheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Runs {
    private int batter;
    private int extras;
    private int total;
    @JsonProperty("non_boundary")
    private boolean nonBoundary;
}
