package com.cricket.fantasy.service;

import com.cricket.fantasy.common.CricksheetHelper;
import com.cricket.fantasy.entity.FantasyPlayer;
import com.cricket.fantasy.entity.FantasyPoints;
import com.cricket.fantasy.entity.Match;
import com.cricket.fantasy.entity.Player;
import com.cricket.fantasy.entity.Team;
import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.model.domain.cricsheet.Inning;
import com.cricket.fantasy.model.domain.cricsheet.Over;
import com.cricket.fantasy.repository.FantasyPlayerRepository;
import com.cricket.fantasy.repository.MatchRepository;
import com.cricket.fantasy.repository.PlayerRepository;
import com.cricket.fantasy.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MatchFantasyService {

    private static final Logger logger = LoggerFactory.getLogger(MatchFantasyService.class);

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private FantasyPlayerRepository fantasyPlayerRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchFeedService matchFeedService;

    @Autowired
    private MatchCalculationService matchCalculationService;

    /**
     * Generate and calculate points for {@link FantasyPlayer}
     * @param cricksheetMatch Match information data feed
     */
    public void updatePlayerPoints(CricksheetMatch cricksheetMatch) {
        Match match = createFantasyPlayersFor(cricksheetMatch);
        List<FantasyPlayer> players = match.getPlayers();

        if (players == null || players.isEmpty()) {
            players = fantasyPlayerRepository.findByMatch(match);
            if (players == null || players.isEmpty()) {
                throw new EntityNotFoundException("Players not found");
            }
        }

        CricSheetMatchData matchData = CricksheetHelper.getMatchData(cricksheetMatch);
        for (Inning inning : matchData.getInnings()) {
            for (Over over : inning.getOvers()) {
                matchFeedService.updatePoints(players, over);
            }
        }

        matchCalculationService.calculatePoints(players);

        fantasyPlayerRepository.saveAllAndFlush(players);

        logger.info("MATCH {}", match.getTeams());
        for (FantasyPlayer player : players) {
            logger.info("{} - {}", player.getPoints().getTotalPoints(), player.getPlayer().getName());
        }
    }

    private Match createFantasyPlayersFor(CricksheetMatch cricksheetMatch) {
        Match match = matchService.findMatch(cricksheetMatch.getTeams());
        CricSheetMatchData matchData = CricksheetHelper.getMatchData(cricksheetMatch);

        Map<String, List<String>> playerNames = matchData.getInfo().getPlayers();
        for (Map.Entry<String, List<String>> entry : playerNames.entrySet()) {
            Optional<Team> team = teamRepository.findByName(entry.getKey());
            if (team.isEmpty()) {
                throw new EntityNotFoundException(String.format("Team %s not found", entry.getKey()));
            }

            List<FantasyPlayer> fantasyPlayers = new ArrayList<>();
            for (String playerName : entry.getValue()) {
                Optional<Player> player = playerRepository.findByName(playerName);
                if (player.isEmpty()) {
                    throw new EntityNotFoundException(String.format("Player %s not found", playerName));
                }

                Optional<FantasyPlayer> optionalFantasyPlayer = fantasyPlayerRepository.findByPlayerAndMatch(
                        player.get(),
                        match
                );

                if (optionalFantasyPlayer.isEmpty()) {
                    FantasyPlayer fantasyPlayer = new FantasyPlayer(player.get(), match, new FantasyPoints());
                    fantasyPlayers.add(fantasyPlayer);
                } else {
                    optionalFantasyPlayer.get().setPoints(new FantasyPoints());
                    fantasyPlayers.add(optionalFantasyPlayer.get());
                }
            }

            fantasyPlayerRepository.saveAllAndFlush(fantasyPlayers);
        }

        fantasyPlayerRepository.flush();
        matchRepository.flush();

        return matchService.findMatch(match.getTeams());
    }
}
