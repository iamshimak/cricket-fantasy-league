package com.cricket.fantasy.service.cricket;

import com.cricket.fantasy.entity.enums.PlayerBattingRecordType;
import com.cricket.fantasy.entity.fantasy.FantasyPlayer;
import com.cricket.fantasy.entity.fantasy.FantasyPoints;
import com.cricket.fantasy.repository.cricket.PlayerRepository;
import com.cricket.fantasy.repository.cricket.TeamRepository;
import com.cricket.fantasy.repository.fantasy.FantasyPlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(MatchCalculationService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private FantasyPlayerRepository fantasyPlayerRepository;

    public void calculatePoints(List<FantasyPlayer> players) {
        // For now add total points with existing ones
        for (FantasyPlayer player : players) {
            double totalPoints = calculateBattingPoints(player);
            totalPoints += calculateBowlingPoints(player);
            totalPoints += calculateFieldingPoints(player);

            player.getPoints().setTotalPoints(totalPoints);
        }
    }

    private double calculateBattingPoints(FantasyPlayer player) {
        FantasyPoints points = player.getPoints();
        double totalPoints = 0.0;

        if (points.getRuns() >= 100) {
            points.setBattingRecord(PlayerBattingRecordType.CENTURY);
        } else if (points.getRuns() >= 50) {
            points.setBattingRecord(PlayerBattingRecordType.FIFTY);
        } else if (points.getRuns() >= 30) {
            points.setBattingRecord(PlayerBattingRecordType.THIRTY);
        }

        totalPoints += points.getRuns();
        totalPoints += points.getBoundaries();
        totalPoints += points.getSixes() * 2;

        switch (points.getBattingRecord()) {
            case CENTURY -> totalPoints += 8;
            case FIFTY -> totalPoints += 4;
            case THIRTY -> totalPoints += 2;
            case DUCK -> totalPoints -= 3;
        }

        // TODO - Excepts for bowlers
        if (points.getBallsFaced() >= 10) {
            double strikeRate = ((double) points.getRuns() / (double) points.getBallsFaced()) * 100;
            if (strikeRate >= 170) {
                totalPoints += 6;
            } else if (strikeRate >= 150) {
                totalPoints += 4;
            } else if (strikeRate >= 130) {
                totalPoints += 2;
            } else if (strikeRate >= 60 && strikeRate <= 70) {
                totalPoints -= 2;
            } else if (strikeRate >= 50 && strikeRate <= 60) {
                totalPoints -= 4;
            } else if (strikeRate < 50){
                totalPoints -= 6;
            }
        }

        return totalPoints;
    }

    private double calculateBowlingPoints(FantasyPlayer player) {
        FantasyPoints points = player.getPoints();
        double totalPoints = 0.0;

        if (points.getWickets() >= 5) {
            points.setFiveWicketsHaul(true);
        } else if (points.getWickets() >= 4) {
            points.setFourWicketsHaul(true);
        }

        totalPoints += points.getWickets() * 25;
        totalPoints += points.getBowledOrLBW() * 8;

        if (points.isFiveWicketsHaul()) {
            totalPoints += 8;
        } else if (points.isFourWicketsHaul()) {
            totalPoints += 4;
        }

        if (points.getOverBowled() > 2) {
            double economyRate = (double) points.getRunsConceded() / (double) points.getOverBowled();
            if (economyRate >= 12) {
                totalPoints -= 6;
            } else if (economyRate >= 11) {
                totalPoints -= 4;
            } else if (economyRate >= 10) {
                totalPoints -= 2;
            } else if (economyRate >= 6 && economyRate <= 7) {
                totalPoints += 2;
            } else if (economyRate >= 5 && economyRate <= 6) {
                totalPoints += 4;
            } else if (economyRate < 5) {
                totalPoints += 6;
            }
        }

        return totalPoints;
    }

    private double calculateFieldingPoints(FantasyPlayer player) {
        FantasyPoints points = player.getPoints();
        double totalPoints = 0.0;

        totalPoints += points.getCatches() * 8;
        totalPoints += points.getStumping() * 12;
        totalPoints += points.getRunOuts() * 12;

        if (points.getCatches() >= 3) {
            totalPoints += 4;
        }

        return totalPoints;
    }
}
