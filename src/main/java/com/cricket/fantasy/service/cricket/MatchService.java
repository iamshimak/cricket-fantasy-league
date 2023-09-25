package com.cricket.fantasy.service.cricket;

import com.cricket.fantasy.entity.cricket.Player;
import com.cricket.fantasy.entity.cricket.Team;
import com.cricket.fantasy.entity.fantasy.FantasyPlayer;
import com.cricket.fantasy.entity.fantasy.FantasyPoints;
import com.cricket.fantasy.entity.fantasy.Match;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.model.domain.cricsheet.Inning;
import com.cricket.fantasy.model.domain.cricsheet.Over;
import com.cricket.fantasy.repository.MatchRepository;
import com.cricket.fantasy.repository.cricket.PlayerRepository;
import com.cricket.fantasy.repository.cricket.TeamRepository;
import com.cricket.fantasy.repository.fantasy.FantasyPlayerRepository;
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
public class MatchService {

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private FantasyPlayerRepository fantasyPlayerRepository;

    @Autowired
    private MatchFeedService matchFeedService;

    @Autowired
    private MatchCalculationService matchCalculationService;

    /**
     * Generate and calculate points for {@link FantasyPlayer}
     * @param matchData matchData Match information data feed
     */
    public void updatePoints(CricSheetMatchData matchData) {
        Match match = createFantasyPlayersFor(matchData);
        List<FantasyPlayer> players = match.getPlayers();

        if (players == null || players.isEmpty()) {
            throw new EntityNotFoundException("Players not found");
        }

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

    Match createFantasyPlayersFor(CricSheetMatchData matchData) {
        String teams = String.join(",", matchData.getInfo().getTeams());
        Match match = findMatch(teams);

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
        return findMatch(teams);
    }
}
