package com.cricket.fantasy.service.cricket;

import com.cricket.fantasy.common.CricksheetHelper;
import com.cricket.fantasy.entity.cricket.Player;
import com.cricket.fantasy.entity.cricket.Team;
import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import com.cricket.fantasy.entity.enums.FantasyWicketKind;
import com.cricket.fantasy.entity.enums.PlayerType;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.model.domain.cricsheet.Delivery;
import com.cricket.fantasy.model.domain.cricsheet.Inning;
import com.cricket.fantasy.model.domain.cricsheet.Over;
import com.cricket.fantasy.model.domain.cricsheet.Wicket;
import com.cricket.fantasy.repository.MatchRepository;
import com.cricket.fantasy.repository.cricket.PlayerRepository;
import com.cricket.fantasy.repository.cricket.TeamRepository;
import com.cricket.fantasy.repository.cricksheet.CricksheetMatchRepository;
import com.cricket.fantasy.repository.fantasy.UserFantasyMatchRepository;
import com.cricket.fantasy.repository.user.UserRepository;
import com.cricket.fantasy.service.fantasy.FantasyService;
import com.cricket.fantasy.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CricketFeedService {

    private static final Logger logger = LoggerFactory.getLogger(CricketFeedService.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserService userService;

    @Autowired
    private FantasyService fantasyService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CricksheetMatchRepository cricksheetMatchRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFantasyMatchRepository userFantasyMatchRepository;

    /**
     * Generate and persist {@link Team}, {@link Player}
     * @param matchData Match information data feed
     */
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

    public void generateCricsheetDataFromJson() {
        try {
            File file = ResourceUtils.getFile("classpath:jsons/ipl_json");
            if (file.listFiles() == null) {
                logger.info("FILES NOT FOUND");
                return;
            }

            List<CricksheetMatch> matches = new ArrayList<>();
            for (File jsonFile : file.listFiles()) {
                if (!jsonFile.getName().split("\\.")[1].equals("json")) {
                    continue;
                }

                logger.info("LOADING FILE {}", jsonFile.getName());

                //read json file data to String
                byte[] jsonData = Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath()));

                //create ObjectMapper instance
                ObjectMapper objectMapper = new ObjectMapper();

                //convert json string to object
                CricSheetMatchData matchData = objectMapper.readValue(jsonData, CricSheetMatchData.class);

                CricksheetMatch match = new CricksheetMatch();
                match.setBallPerOver(matchData.getInfo().getBalls_per_over());
                match.setDate(String.join(",", matchData.getInfo().getDates()));
                match.setEventName(matchData.getInfo().getEvent().getName());
                match.setEventMatchNumber(matchData.getInfo().getEvent().getMatchNumber());
                match.setGender(matchData.getInfo().getGender());
                match.setMatchType(matchData.getInfo().getMatch_type());
                match.setOvers(matchData.getInfo().getOvers());
                match.setSeason(matchData.getInfo().getSeason());
                match.setTeamType(matchData.getInfo().getTeam_type());
                match.setTeams(String.join(",",matchData.getInfo().getTeams()));

                String json = objectMapper.writeValueAsString(matchData);
                match.setJson(json);

                matches.add(match);
            }

            cricksheetMatchRepository.saveAllAndFlush(matches);

        } catch (Exception exception) {
            logger.error("FILE EXCEPTION", exception);
        }
    }
}