package com.cricket.fantasy.service.fantasy;

import com.cricket.fantasy.entity.cricket.Player;
import com.cricket.fantasy.entity.cricket.Team;
import com.cricket.fantasy.entity.enums.FantasyWicketKind;
import com.cricket.fantasy.entity.enums.PlayerBattingRecordType;
import com.cricket.fantasy.entity.fantasy.FantasyPlayer;
import com.cricket.fantasy.entity.fantasy.FantasyPoints;
import com.cricket.fantasy.entity.fantasy.Match;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.model.domain.cricsheet.Delivery;
import com.cricket.fantasy.model.domain.cricsheet.Fielder;
import com.cricket.fantasy.model.domain.cricsheet.Inning;
import com.cricket.fantasy.model.domain.cricsheet.Over;
import com.cricket.fantasy.model.domain.cricsheet.Runs;
import com.cricket.fantasy.model.domain.cricsheet.Wicket;
import com.cricket.fantasy.repository.cricket.PlayerRepository;
import com.cricket.fantasy.repository.cricket.TeamRepository;
import com.cricket.fantasy.repository.fantasy.FantasyPlayerRepository;
import com.cricket.fantasy.service.cricket.MatchService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FantasyService {

    private static final Logger logger = LoggerFactory.getLogger(FantasyService.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private FantasyPlayerRepository fantasyPlayerRepository;

    public void updatePoints(CricSheetMatchData matchData) {
        String teams = String.join(",", matchData.getInfo().getTeams());
        Match match = matchService.findMatch(teams);

        Map<String, List<String>> playerNames = matchData.getInfo().getPlayers();
        for (Map.Entry<String, List<String>> entry : playerNames.entrySet()) {
            Optional<Team> team = teamRepository.findByName(entry.getKey());
            if (team.isEmpty()) {
                continue;
            }

            List<FantasyPlayer> fantasyPlayers = new ArrayList<>();
            for (String playerName : entry.getValue()) {
                Optional<Player> player = playerRepository.findByName(playerName);
                if (player.isEmpty()) {
                    throw new EntityNotFoundException(String.format("Player %s not found", playerName));
                }

                if (fantasyPlayerRepository.findByPlayerAndMatch(player.get(), match).isEmpty()) {
                    FantasyPlayer fantasyPlayer = new FantasyPlayer(player.get(), match, new FantasyPoints());
                    fantasyPlayers.add(fantasyPlayer);
                }
            }

            fantasyPlayerRepository.saveAllAndFlush(fantasyPlayers);
        }

        fantasyPlayerRepository.flush();
        Match updatedMatch = matchService.findMatch(teams);
        List<FantasyPlayer> players = updatedMatch.getPlayers();

        if (players == null) {
            throw new EntityNotFoundException("Players not found");
        }

        for (Inning inning : matchData.getInnings()) {
            for (Over over : inning.getOvers()) {
                updatePoints(players, over);

                logger.info("MATCH {} OVER {}", updatedMatch.getTeams(), over.getOver() + 1);
                for (FantasyPlayer player : players) {
                    logger.info("{}\t - {}", player.getPoints().getTotalPoints(), player.getPlayer().getName());
                }
            }
        }

        calculatePoints(players);

        logger.info("MATCH {}", updatedMatch.getTeams());
        for (FantasyPlayer player : players) {
            logger.info("{}\t - {}", player.getPoints().getTotalPoints(), player.getPlayer().getName());
        }
    }

    public void updatePoints(List<FantasyPlayer> players, Over over) {
        for (Delivery delivery : over.getDeliveries()) {
            FantasyPlayer batter = findPlayer(players, delivery.getBatter());
            FantasyPlayer bowler = findPlayer(players, delivery.getBowler());

            updateBatterPoints(players, batter, delivery);
            updateBowlerPoints(players, bowler, delivery);
            updateFielderPoints(players, delivery);
        }

        updateBowlerPoints(players, over);
    }

    private void updateBatterPoints(List<FantasyPlayer> players, FantasyPlayer batter, Delivery delivery) {
        Runs runs = delivery.getRuns();
        FantasyPoints points = batter.getPoints();
        points.setRuns(points.getRuns() + runs.getBatter());
        points.setBallsFaced(points.getBallsFaced() + 1);

        switch (runs.getBatter()) {
            case 4 -> points.setBoundaries(points.getBoundaries() + 1);
            case 6 -> points.setSixes(points.getSixes() + 1);
        }

        if (delivery.getWickets() != null) {
            for (Wicket wicket : delivery.getWickets()) {
                FantasyPlayer playerOut = findPlayer(players, wicket.getPlayerOut());
                if (playerOut.getPoints().getRuns() == 0 && playerOut.getPoints().getBallsFaced() == 1) {
                    playerOut.getPoints().setBattingRecord(PlayerBattingRecordType.DUCK);
                }
            }
        }
    }

    private void updateBowlerPoints(List<FantasyPlayer> players, FantasyPlayer bowler, Delivery delivery) {
        Runs runs = delivery.getRuns();
        FantasyPoints points = bowler.getPoints();
        points.setBallBowled(points.getBallBowled() + 1);
        points.setRunsConceded(points.getRunsConceded() + runs.getBatter());

        if (delivery.getWickets() != null) {
            for (Wicket wicket : delivery.getWickets()) {
                points.setWickets(points.getWickets() + 1);
                List<String> pointsForWicketsKinds = Stream.of(FantasyWicketKind.BOWLED, FantasyWicketKind.LBW)
                        .map(wicketKind -> wicketKind.name().toUpperCase())
                        .toList();

                if (pointsForWicketsKinds.contains(wicket.getKind())) {
                    points.setBowledOrLBW(points.getBowledOrLBW() + 1);
                }
            }
        }
    }

    private void updateBowlerPoints(List<FantasyPlayer> players, Over over) {
        int totalBatterRuns = 0;
        boolean isSameBowler = true;

        FantasyPlayer bowler = null;
        for (Delivery delivery : over.getDeliveries()) {
            totalBatterRuns += delivery.getRuns().getBatter();

            FantasyPlayer findBowler = findPlayer(players, delivery.getBowler());
            if (bowler == null) {
                bowler = findBowler;
            } else {
                if (bowler.getId() != findBowler.getId()) {
                    isSameBowler = false;
                }
            }
        }

        if (totalBatterRuns == 0 && isSameBowler && bowler != null) {
            bowler.getPoints().setMaidenOvers(bowler.getPoints().getMaidenOvers() + 1);
        }

        if (isSameBowler && bowler != null) {
            bowler.getPoints().setOverBowled(bowler.getPoints().getOverBowled() + 1);
        }
    }

    private void updateFielderPoints(List<FantasyPlayer> players, Delivery delivery) {
        if (delivery.getWickets() == null) {
            return;
        }

        for (Wicket wicket : delivery.getWickets()) {
            if (wicket.getFielders() == null) {
                continue;
            }

            FantasyWicketKind wicketKind = FantasyWicketKind.fromCricksheetValue(wicket.getKind());
            for (Fielder fielder : wicket.getFielders()) {
                FantasyPlayer fantasyPlayer = findPlayer(players, fielder.getName());
                FantasyPoints points = fantasyPlayer.getPoints();

                switch (wicketKind) {
                    case CAUGHT -> points.setCatches(points.getCatches() + 1);
                    case STUMPED -> points.setStumping(points.getStumping() + 1);
                    case RUN_OUT -> points.setRunOuts(points.getRunOuts() + 1);
                }
            }
        }
    }

    private void calculatePoints(List<FantasyPlayer> players) {
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

    private FantasyPlayer findPlayer(List<FantasyPlayer> players, String name) {
        Optional<FantasyPlayer> fantasyPlayer = players.stream()
                .filter(player -> player.getPlayer().getName().equals(name))
                .findFirst();

        if (fantasyPlayer.isEmpty()) {
            throw new EntityNotFoundException(String.format("Player %s not found", name));
        }

        return fantasyPlayer.get();
    }

    private List<FantasyPlayer> findPlayers(List<FantasyPlayer> players, List<String> names) {
        List<FantasyPlayer> fantasyPlayers = new ArrayList<>();

        for (String name : names) {
            Optional<FantasyPlayer> fantasyPlayer = players.stream()
                    .filter(player -> player.getPlayer().getName().equals(name))
                    .findFirst();

            if (fantasyPlayer.isEmpty()) {
                throw new EntityNotFoundException(String.format("Player %s not found", name));
            }

            fantasyPlayers.add(fantasyPlayer.get());
        }

        return fantasyPlayers;
    }
}
