package com.cricket.fantasy.service.fantasy;

import com.cricket.fantasy.entity.cricket.Player;
import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import com.cricket.fantasy.entity.enums.FantasyPlayerType;
import com.cricket.fantasy.entity.fantasy.Match;
import com.cricket.fantasy.entity.fantasy.user.UserFantasySquad;
import com.cricket.fantasy.entity.fantasy.user.UserFantasyPlayer;
import com.cricket.fantasy.entity.user.User;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.repository.cricket.PlayerRepository;
import com.cricket.fantasy.repository.cricksheet.CricksheetMatchRepository;
import com.cricket.fantasy.repository.fantasy.UserFantasyMatchRepository;
import com.cricket.fantasy.repository.fantasy.UserFantasyPlayerRepository;
import com.cricket.fantasy.service.cricket.CricketFeedService;
import com.cricket.fantasy.service.cricket.MatchService;
import com.cricket.fantasy.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class FantasyFeedService {

    private static final Logger logger = LoggerFactory.getLogger(FantasyFeedService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CricketFeedService cricketFeedService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private CricksheetMatchRepository cricksheetMatchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserFantasyMatchRepository userFantasyMatchRepository;
    @Autowired
    private UserFantasyPlayerRepository userFantasyPlayerRepository;

    public void generateRandomTeamsForUser(List<String> usernames, String eventName, String season) {
        List<User> users = userService.saveUsers(usernames);
        List<CricksheetMatch> matches = cricksheetMatchRepository.findByEventNameAndSeasonOrderByDateAsc(
                eventName,
                season
        );

        for (CricksheetMatch match : matches) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                CricSheetMatchData matchData = objectMapper.readValue(match.getJson(), CricSheetMatchData.class);
                cricketFeedService.setupDatabaseFromCrickFeed(matchData);
                List<UserFantasySquad> userFantasyMatches = generateUserFantasyMatches(users, matchData);

            } catch (Exception exception) {
                logger.error("CONVERSION ERROR", exception);
            }
        }
    }

    /**
     * Generate random {@link UserFantasySquad} for given {@link CricSheetMatchData} and users
     * @param users User collection
     * @param matchData Match information data feed
     * @return Random squads
     */
    public List<UserFantasySquad> generateUserFantasyMatches(List<User> users, CricSheetMatchData matchData) {
        Match match = matchService.findMatch(String.join(",", matchData.getInfo().getTeams()));
        List<Player> players = new ArrayList<>();
        for (String teamName : matchData.getInfo().getTeams()) {
            List<Player> typePlayers = playerRepository.findByTeam_Name(teamName);
            if (typePlayers.size() == 0) {
                throw new EntityNotFoundException("Players not found");
            }

            players.addAll(typePlayers);
        }

        userFantasyMatchRepository.flush();

        List<UserFantasySquad> userFantasyMatchList = new ArrayList<>();
        for (User user : users) {
            UserFantasySquad userFantasyMatch = new UserFantasySquad();
            userFantasyMatch.setUser(user);
            userFantasyMatch.setMatch(match);

            UserFantasySquad updatedUserFantasyMatch = userFantasyMatchRepository.saveAndFlush(userFantasyMatch);

            List<Player> randomPlayerList = pickRandomPlayers(players, 11);
            List<UserFantasyPlayer> userFantasyPlayers = randomPlayerList.stream().map(randomPlayer -> {
                UserFantasyPlayer userFantasyPlayer = new UserFantasyPlayer();
                userFantasyPlayer.setPlayer(randomPlayer);
                userFantasyPlayer.setMatch(userFantasyMatch);
                userFantasyPlayer.setType(FantasyPlayerType.REGULAR);
                return userFantasyPlayer;
            }).toList();

            pickRandomFantasyPlayerType(userFantasyPlayers, FantasyPlayerType.CAPTAIN);
            pickRandomFantasyPlayerType(userFantasyPlayers, FantasyPlayerType.VICE_CAPTAIN);

            userFantasyPlayers = userFantasyPlayerRepository.saveAllAndFlush(userFantasyPlayers);
            updatedUserFantasyMatch.setPlayers(userFantasyPlayers);

            userFantasyMatchList.add(userFantasyMatch);
        }

        return userFantasyMatchRepository.saveAllAndFlush(userFantasyMatchList);
    }

    private List<Player> pickRandomPlayers(List<Player> players, int limit) {
        List<Player> randomPlayers = new ArrayList<>();
        while (randomPlayers.size() < limit) {
            Random random = new Random();
            int index = random.nextInt(players.size());
            Player pickedPlayer = players.get(index);

            if (randomPlayers.stream().noneMatch(randomPlayer -> randomPlayer.getId() == pickedPlayer.getId())) {
                randomPlayers.add(pickedPlayer);
            }
        }

        return randomPlayers;
    }

    private void pickRandomFantasyPlayerType(
            List<UserFantasyPlayer> userFantasyPlayers,
            FantasyPlayerType fantasyPlayerType
    ) {
        while (!isPlayerTypeSelected(userFantasyPlayers, fantasyPlayerType)) {
            Random random = new Random();
            int index = random.nextInt(userFantasyPlayers.size());
            UserFantasyPlayer pickedPlayer = userFantasyPlayers.get(index);

            if (pickedPlayer.getType() == FantasyPlayerType.REGULAR) {
                pickedPlayer.setType(fantasyPlayerType);
            }
        }
    }

    private boolean isPlayerTypeSelected(List<UserFantasyPlayer> userFantasyPlayers, FantasyPlayerType playerType) {
        return userFantasyPlayers.stream().anyMatch(player -> player.getType() == playerType);
    }
}
