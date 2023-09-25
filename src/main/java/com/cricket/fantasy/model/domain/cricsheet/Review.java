package com.cricket.fantasy.model.domain.cricsheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Review {
    private String by;
    private String umpire;
    private String batter;
    private String decision;
    private String type;
    @JsonProperty("umpires_call")
    private boolean umpiresCall;
}
