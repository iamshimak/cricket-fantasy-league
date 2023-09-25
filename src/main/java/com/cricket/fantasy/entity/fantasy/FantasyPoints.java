package com.cricket.fantasy.entity.fantasy;

import com.cricket.fantasy.entity.base.BaseEntity;
import com.cricket.fantasy.entity.enums.PlayerBattingRecordType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fantasy_points")
public class FantasyPoints extends BaseEntity {

    @OneToOne(mappedBy = "points")
    public FantasyPlayer player;

    private double totalPoints = 0.0;

    private int runs = 0;
    private int boundaries = 0;
    private int sixes = 0;
    private int ballsFaced = 0;

    @Enumerated(EnumType.STRING)
    private PlayerBattingRecordType battingRecord = PlayerBattingRecordType.NONE;

    private int wickets = 0;
    private int bowledOrLBW = 0;
    private int maidenOvers = 0;
    private int runsConceded = 0;
    private int ballBowled = 0;
    private int overBowled = 0;
    private boolean isFourWicketsHaul = false;
    private boolean isFiveWicketsHaul = false;

    private int catches = 0;
    private int stumping = 0;
    private int runOuts = 0;
    private boolean isCaughtThreeCatches = false;

    private double strikeRate = 0.0;
    private double economyRate = 0.0;
}