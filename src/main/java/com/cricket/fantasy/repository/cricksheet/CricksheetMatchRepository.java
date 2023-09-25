package com.cricket.fantasy.repository.cricksheet;

import com.cricket.fantasy.entity.cricsheet.CricksheetMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CricksheetMatchRepository extends JpaRepository<CricksheetMatch, Integer> {
    List<CricksheetMatch> findByEventNameAndSeasonOrderByDateAsc(String eventName, String season);

}