package com.cricket.fantasy.controller.cricket;

import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.service.cricket.CricketService;
import com.cricket.fantasy.service.fantasy.FantasyService;
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
    private CricketService cricketService;

    @Autowired
    private FantasyService fantasyService;

    @PostMapping("/cricsheet-feed")
    public ResponseEntity<Void> setupDataFromCricFeed(@RequestBody CricSheetMatchData matchData) {
        cricketService.setupDatabaseFromCrickFeed(matchData);
        fantasyService.updatePoints(matchData);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate-cricsheet-feed")
    public ResponseEntity<Void> generateCricksheetData() {
        cricketService.generateCricsheetDataFromJson();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate-squad-for-users")
    private ResponseEntity<Void> generateSquadsForUsers(
            @RequestParam(name = "usernames") List<String> usernames,
            @RequestParam(name = "event") String event,
            @RequestParam(name = "season") String season
    ) {
        cricketService.generateRandomTeamsForUser(usernames, event, season);
        return ResponseEntity.ok().build();
    }
}
