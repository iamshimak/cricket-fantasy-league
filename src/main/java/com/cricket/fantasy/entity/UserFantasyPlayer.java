package com.cricket.fantasy.entity;

import com.cricket.fantasy.entity.base.BaseEntity;
import com.cricket.fantasy.entity.enums.FantasyPlayerType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_fantasy_player")
public class UserFantasyPlayer extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private UserFantasySquad match;

    @Enumerated(EnumType.STRING)
    private FantasyPlayerType type;

    private double totalPoints = 0.0;
}
