package com.cricket.fantasy.model.domain.cricsheet; 
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Wicket {

    @JsonProperty("player_out")
    private String playerOut;
    private String kind;
    private List<Fielder> fielders;
}
