package com.cricket.fantasy.model.domain.cricsheet;

import lombok.Data;

@Data
public class Outcome{
    private String winner;
    private By by;
    private String method;
}
