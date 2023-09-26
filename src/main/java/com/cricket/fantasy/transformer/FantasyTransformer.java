package com.cricket.fantasy.transformer;

import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;

public final class FantasyTransformer {

    public static CricksheetMatch toCricksheetMatch(CricSheetMatchData matchData, String json) {
        CricksheetMatch match = new CricksheetMatch();
        match.setBallPerOver(matchData.getInfo().getBalls_per_over());
        match.setDate(String.join(",", matchData.getInfo().getDates()));
        match.setEventName(matchData.getInfo().getEvent().getName());
        match.setEventMatchNumber(matchData.getInfo().getEvent().getMatchNumber());
        match.setGender(matchData.getInfo().getGender());
        match.setMatchType(matchData.getInfo().getMatch_type());
        match.setOvers(matchData.getInfo().getOvers());
        match.setSeason(matchData.getInfo().getSeason());
        match.setTeamType(matchData.getInfo().getTeam_type());
        match.setTeams(String.join(",",matchData.getInfo().getTeams()));
        match.setJson(json);

        return match;
    }
}
