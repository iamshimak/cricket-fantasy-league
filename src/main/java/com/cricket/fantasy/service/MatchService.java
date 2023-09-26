package com.cricket.fantasy.service;

import com.cricket.fantasy.entity.Match;
import com.cricket.fantasy.repository.MatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatchService {

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

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
