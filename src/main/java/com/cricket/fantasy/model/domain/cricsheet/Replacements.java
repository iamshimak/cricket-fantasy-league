package com.cricket.fantasy.model.domain.cricsheet;

import lombok.Data;

import java.util.List;

@Data
public class Replacements {
    private List<PlayerReplacement> match;
    private List<PlayerReplacement> role;
}
