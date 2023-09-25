package com.cricket.fantasy.service.fantasy;

import com.cricket.fantasy.entity.fantasy.FantasyPlayer;
import com.cricket.fantasy.entity.fantasy.Match;
import com.cricket.fantasy.entity.fantasy.user.UserFantasyPlayer;
import com.cricket.fantasy.entity.fantasy.user.UserFantasySquad;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.repository.cricket.PlayerRepository;
import com.cricket.fantasy.service.cricket.MatchCalculationService;
import com.cricket.fantasy.service.cricket.MatchService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FantasyService {

    private static final Logger logger = LoggerFactory.getLogger(MatchCalculationService.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerRepository playerRepository;

    public void calculatePoints(CricSheetMatchData matchData, List<UserFantasySquad> squadList) {
        Match match = matchService.findMatch(String.join(",", matchData.getInfo().getTeams()));
        List<FantasyPlayer> players = match.getPlayers();

        for (UserFantasySquad fantasySquad : squadList) {
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
                .findFirst().orElseThrow(() -> new EntityNotFoundException(
                        String.format("Player %s not found", userFantasyPlayer.getId()))
                );
    }
}
