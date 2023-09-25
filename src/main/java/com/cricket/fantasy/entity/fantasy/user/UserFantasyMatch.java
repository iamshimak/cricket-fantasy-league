package com.cricket.fantasy.entity.fantasy.user;

import com.cricket.fantasy.entity.base.BaseEntity;
import com.cricket.fantasy.entity.fantasy.Match;
import com.cricket.fantasy.entity.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user_fantasy_match")
public class UserFantasyMatch extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany
    private List<UserFantasyPlayer> players;
}