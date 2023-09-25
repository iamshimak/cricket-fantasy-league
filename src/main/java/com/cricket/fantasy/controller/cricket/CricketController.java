package com.cricket.fantasy.controller.cricket;

import com.cricket.fantasy.entity.fantasy.user.UserFantasySquad;
import com.cricket.fantasy.entity.user.User;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.service.cricket.CricketFeedService;
import com.cricket.fantasy.service.cricket.MatchService;
import com.cricket.fantasy.service.fantasy.FantasyFeedService;
import com.cricket.fantasy.service.fantasy.FantasyService;
import com.cricket.fantasy.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/cricket")
public class CricketController {

    @Autowired
    private CricketFeedService cricketFeedService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private FantasyFeedService userFantasyService;

    @Autowired
    private UserService userService;

    @Autowired
    private FantasyService fantasyService;

    @PostMapping("/cricsheet-feed")
    public ResponseEntity<Void> setupDataFromCricFeed(
            @RequestParam(name = "usernames") List<String> usernames,
            @RequestBody CricSheetMatchData matchData
    ) {
        cricketFeedService.setupDatabaseFromCrickFeed(matchData);
        matchService.updatePoints(matchData);
        List<User> users = userService.saveUsers(usernames);
        List<UserFantasySquad> squadList = userFantasyService.generateUserFantasyMatches(users, matchData);
        fantasyService.calculatePoints(matchData, squadList);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate-cricsheet-feed")
    public ResponseEntity<Void> generateCricksheetData() {
        cricketFeedService.generateCricsheetDataFromJson();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate-squad-for-users")
    private ResponseEntity<Void> generateSquadsForUsers(
            @RequestParam(name = "usernames") List<String> usernames,
            @RequestParam(name = "event") String event,
            @RequestParam(name = "season") String season
    ) {
        userFantasyService.generateRandomTeamsForUser(usernames, event, season);
        return ResponseEntity.ok().build();
    }
}
