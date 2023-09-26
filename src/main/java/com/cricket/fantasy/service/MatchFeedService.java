package com.cricket.fantasy.service;

import com.cricket.fantasy.entity.enums.FantasyWicketKind;
import com.cricket.fantasy.entity.enums.PlayerBattingRecordType;
import com.cricket.fantasy.entity.FantasyPlayer;
import com.cricket.fantasy.entity.FantasyPoints;
import com.cricket.fantasy.model.domain.cricsheet.Delivery;
import com.cricket.fantasy.model.domain.cricsheet.Fielder;
import com.cricket.fantasy.model.domain.cricsheet.Over;
import com.cricket.fantasy.model.domain.cricsheet.Runs;
import com.cricket.fantasy.model.domain.cricsheet.Wicket;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MatchFeedService {

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
