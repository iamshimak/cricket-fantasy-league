package com.cricket.fantasy.controller.cricket;

import com.cricket.fantasy.model.domain.cricsheet.CricSheetMatchData;
import com.cricket.fantasy.service.cricket.CricketService;
import com.cricket.fantasy.service.fantasy.FantasyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
