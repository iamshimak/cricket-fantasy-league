package com.cricket.fantasy.service.cricket;

import com.cricket.fantasy.common.CricksheetHelper;
import com.cricket.fantasy.entity.cricket.Player;
import com.cricket.fantasy.entity.cricket.Team;
import com.cricket.fantasy.entity.enums.FantasyWicketKind;
import com.cricket.fantasy.entity.enums.PlayerType;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.model.domain.cricsheet.Delivery;
import com.cricket.fantasy.model.domain.cricsheet.Inning;
import com.cricket.fantasy.model.domain.cricsheet.Over;
import com.cricket.fantasy.model.domain.cricsheet.Wicket;
import com.cricket.fantasy.repository.cricket.PlayerRepository;
import com.cricket.fantasy.repository.cricket.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CricketService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public void setupDatabaseFromCrickFeed(CricSheetMatchData matchData) {
        List<String> teamNames = matchData.getInfo().getTeams();
        for (String teamName : teamNames) {
            if (!teamRepository.existsByName(teamName)) {
                teamRepository.saveAndFlush(new Team(teamName));
            }
        }

        Map<String, List<String>> playerNames = matchData.getInfo().getPlayers();
        List<Player> players = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : playerNames.entrySet()) {
            Optional<Team> team = teamRepository.findByName(entry.getKey());
            if (team.isEmpty()) {
                continue;
            }

            for (String playerName : entry.getValue()) {
                if (!playerRepository.existsByName(playerName)) {
                    String externalId = matchData.getInfo().getRegistry().getPeople().get(playerName);
                    players.add(new Player(playerName, team.get(), null, externalId));
                }
            }
        }

        playerRepository.saveAllAndFlush(players);

        List<Player> setTypePlayers = new ArrayList<>();
        for (String teamName : teamNames) {
            List<Player> typePlayers = playerRepository.findByTeam_Name(teamName);
            if (typePlayers.size() == 0) {
                throw new EntityNotFoundException("Players not found");
            }

            setTypePlayers.addAll(typePlayers);
        }

        for (Inning inning : matchData.getInnings()) {
            for (Over over : inning.getOvers()) {
                for (Delivery delivery : over.getDeliveries()) {
                    Player bowler = CricksheetHelper.findPlayer(setTypePlayers, delivery.getBowler());
                    bowler.setType(PlayerType.BOWLER);

                    if (delivery.getWickets() != null) {
                        for (Wicket wicket : delivery.getWickets()) {
                            FantasyWicketKind wicketKind = FantasyWicketKind.fromCricksheetValue(wicket.getKind());
                            if (wicketKind == FantasyWicketKind.STUMPED) {
                                if (wicket.getFielders().size() == 1) {
                                    Player player = CricksheetHelper.findPlayer(
                                            setTypePlayers,
                                            wicket.getFielders().get(0).getName()
                                    );
                                    player.setType(PlayerType.WICKET_KEEPER);
                                } else {
                                    throw new EntityNotFoundException("Wicket keeper not found");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
