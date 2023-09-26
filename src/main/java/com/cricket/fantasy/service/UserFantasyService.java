package com.cricket.fantasy.service;

import com.cricket.fantasy.entity.FantasyPlayer;
import com.cricket.fantasy.entity.Match;
import com.cricket.fantasy.entity.UserFantasyPlayer;
import com.cricket.fantasy.entity.UserFantasySquad;
import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import com.cricket.fantasy.repository.FantasyPlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFantasyService {

    private static final Logger logger = LoggerFactory.getLogger(UserFantasyService.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private FantasyPlayerRepository fantasyPlayerRepository;

    /**
     * Calculate fantasy points for given user fantasy squad list
     * @param cricksheetMatch
     * @param squadList
     */
    public void calculatePoints(CricksheetMatch cricksheetMatch, List<UserFantasySquad> squadList) {
        Match match = matchService.findMatch(cricksheetMatch.getTeams());
        List<FantasyPlayer> players = fantasyPlayerRepository.findByMatch(match);

        for (UserFantasySquad fantasySquad : squadList) {
            logger.info("***********************************************");
            logger.info("USER: {}", fantasySquad.getUser().getUsername());
            double totalPoints = calculatePoints(fantasySquad, players);
            logger.info("POINTS: {}", totalPoints);
        }
    }

    private double calculatePoints(UserFantasySquad squad, List<FantasyPlayer> playerList) {
        double squadTotalPoints = 0.0;
        for (UserFantasyPlayer userFantasyPlayer : squad.getPlayers()) {
            FantasyPlayer fantasyPlayer = findPlayer(playerList, userFantasyPlayer);
            double totalPoints = fantasyPlayer.getPoints().getTotalPoints();
            switch (userFantasyPlayer.getType()) {
                case CAPTAIN -> totalPoints = totalPoints * 2;
                case VICE_CAPTAIN -> totalPoints = totalPoints * 1.5;
            }

            userFantasyPlayer.setTotalPoints(totalPoints);
            squadTotalPoints += userFantasyPlayer.getTotalPoints();

            logger.info("{}\t | {} | {}",
                    userFantasyPlayer.getTotalPoints(),
                    userFantasyPlayer.getPlayer().getName(),
                    userFantasyPlayer.getType().name()
            );
        }

        return squadTotalPoints;
    }

    private FantasyPlayer findPlayer(List<FantasyPlayer> playerList, UserFantasyPlayer userFantasyPlayer) {
        return playerList.stream()
                .filter(searchPlayer -> searchPlayer.getPlayer().getId() == userFantasyPlayer.getPlayer().getId())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Player %s not found", userFantasyPlayer.getId()))
                );
    }
}
