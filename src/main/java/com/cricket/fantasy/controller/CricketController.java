package com.cricket.fantasy.controller;

import com.cricket.fantasy.entity.User;
import com.cricket.fantasy.entity.UserFantasySquad;
import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.service.CricketFeedService;
import com.cricket.fantasy.service.MatchFantasyService;
import com.cricket.fantasy.service.UserFantasyFeedService;
import com.cricket.fantasy.service.UserFantasyService;
import com.cricket.fantasy.service.UserService;
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
    private MatchFantasyService matchFantasyService;

    @Autowired
    private UserFantasyFeedService userFantasyFeedService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFantasyService userFantasyService;

    @PostMapping("/cricsheet-feed")
    public ResponseEntity<Void> setupDataFromCricFeed(
            @RequestParam(name = "usernames") List<String> usernames,
            @RequestBody CricSheetMatchData matchData
    ) {
        CricksheetMatch cricksheetMatch = cricketFeedService.setupDatabaseFromCrickFeed(matchData);
        matchFantasyService.updatePlayerPoints(cricksheetMatch);
        List<User> users = userService.saveUsers(usernames);
        List<UserFantasySquad> squadList = userFantasyFeedService.generateUserFantasyMatches(users, cricksheetMatch);
        userFantasyService.calculatePoints(cricksheetMatch, squadList);

        return ResponseEntity.ok().build();
    }

//    @PostMapping("/generate-cricsheet-feed")
//    public ResponseEntity<Void> generateCricksheetData() {
//        cricketFeedService.generateCricsheetDataFromJson();
//        return ResponseEntity.ok().build();
//    }

//    @PostMapping("/generate-squad-for-users")
//    private ResponseEntity<Void> generateSquadsForUsers(
//            @RequestParam(name = "usernames") List<String> usernames,
//            @RequestParam(name = "event") String event,
//            @RequestParam(name = "season") String season
//    ) {
//        userFantasyService.generateRandomTeamsForUser(usernames, event, season);
//        return ResponseEntity.ok().build();
//    }
}
