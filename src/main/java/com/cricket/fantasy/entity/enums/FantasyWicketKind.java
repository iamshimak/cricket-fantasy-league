package com.cricket.fantasy.entity.enums;

import jakarta.persistence.EntityNotFoundException;

public enum FantasyWicketKind {
    CAUGHT,
    BOWLED,
    LBW,
    RUN_OUT,
    STUMPED;

    public static FantasyWicketKind fromCricksheetValue(String value) {
        switch (value) {
            case "caught" -> {
                return CAUGHT;
            }
            case "bowled" -> {
                return BOWLED;
            }
            case "lbw" -> {
                return LBW;
            }
            case "run out" -> {
                return RUN_OUT;
            }
            case "stumped" -> {
                return STUMPED;
            }
        }

        throw new EntityNotFoundException(String.format("FantasyWicketKind for %s not found", value));
    }
}
