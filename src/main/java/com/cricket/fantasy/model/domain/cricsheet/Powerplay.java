package com.cricket.fantasy.model.domain.cricsheet; 
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Powerplay{
    private double from;
    @JsonProperty("to") 
    private double myto;
    private String type;
}
