package com.cricket.fantasy.model.domain.cricsheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Officials {
    private List<String> match_referees;
    private List<String> umpires;

    @JsonProperty("reserve_umpires")
    private List<String> reserveUmpires;

    @JsonProperty("tv_umpires")
    private List<String> tvUmpires;
}
