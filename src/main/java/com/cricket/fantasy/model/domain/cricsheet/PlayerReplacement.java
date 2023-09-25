package com.cricket.fantasy.model.domain.cricsheet;

import lombok.Data;

@Data
public class PlayerReplacement {
    private String in;
    private String out;
    private String team;
    private String reason;
    private String role;
}
