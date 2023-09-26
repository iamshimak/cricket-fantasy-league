package com.cricket.fantasy.service;

import com.cricket.fantasy.common.CricksheetHelper;
import com.cricket.fantasy.entity.Match;
import com.cricket.fantasy.entity.Player;
import com.cricket.fantasy.entity.User;
import com.cricket.fantasy.entity.UserFantasyPlayer;
import com.cricket.fantasy.entity.UserFantasySquad;
import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import com.cricket.fantasy.entity.enums.FantasyPlayerType;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.repository.PlayerRepository;
import com.cricket.fantasy.repository.UserFantasyMatchRepository;
import com.cricket.fantasy.repository.UserFantasyPlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserFantasyFeedService {

    private static final Logger logger = LoggerFactory.getLogger(UserFantasyFeedService.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserFantasyMatchRepository userFantasyMatchRepository;
    @Autowired
    private UserFantasyPlayerRepository userFantasyPlayerRepository;

    /**
     * Generate random {@link UserFantasySquad} for given {@link CricksheetMatch} and users
     * @param users User collection
     * @param cricksheetMatch Match information data feed
     * @return Random squads
     */
    public List<UserFantasySquad> generateUserFantasyMatches(List<User> users, CricksheetMatch cricksheetMatch) {
        Match match = matchService.findMatch(cricksheetMatch.getTeams());
        CricSheetMatchData matchData = CricksheetHelper.getMatchData(cricksheetMatch);

        List<Player> playerList = new ArrayList<>();
        for (String teamName : matchData.getInfo().getTeams()) {
            List<Player> typePlayers = playerRepository.findByTeam_Name(teamName);
            if (typePlayers.size() == 0) {
                throw new EntityNotFoundException("Players not found");
            }

            playerList.addAll(typePlayers);
        }

        userFantasyMatchRepository.flush();

        List<UserFantasySquad> userFantasyMatchList = new ArrayList<>();
        for (User user : users) {
            UserFantasySquad userFantasyMatch = new UserFantasySquad();
            userFantasyMatch.setUser(user);
            userFantasyMatch.setMatch(match);

            UserFantasySquad updatedUserFantasyMatch = userFantasyMatchRepository.saveAndFlush(userFantasyMatch);

            List<Player> randomPlayerList = pickRandomPlayers(playerList, 11);
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
