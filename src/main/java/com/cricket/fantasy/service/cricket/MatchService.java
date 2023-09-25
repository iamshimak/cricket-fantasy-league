package com.cricket.fantasy.service.cricket;

import com.cricket.fantasy.entity.fantasy.Match;
import com.cricket.fantasy.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    public Match findMatch(String teams) {
        Optional<Match> optionalMatch = matchRepository.findByTeams(teams);

        if (optionalMatch.isEmpty()) {
            Match newMatch = new Match();
            newMatch.setTeams(teams);
            matchRepository.save(newMatch);
            matchRepository.flush();
        }

        Optional<Match> match = matchRepository.findByTeams(teams);
        return match.get();
    }
}
