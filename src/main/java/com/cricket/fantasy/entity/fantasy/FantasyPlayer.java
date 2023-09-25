package com.cricket.fantasy.entity.fantasy;

import com.cricket.fantasy.entity.base.BaseEntity;
import com.cricket.fantasy.entity.cricket.Player;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fantasy_player")
public class FantasyPlayer extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "points_id", referencedColumnName = "id", nullable = false)
    private FantasyPoints points;
}
