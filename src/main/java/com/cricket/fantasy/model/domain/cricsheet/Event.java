package com.cricket.fantasy.model.domain.cricsheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Event {
    private String name;
    private String stage;

    @JsonProperty("match_number")
    private int matchNumber;
}
