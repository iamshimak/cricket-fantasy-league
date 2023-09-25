package com.cricket.fantasy.entity.fantasy.user;

import com.cricket.fantasy.entity.base.BaseEntity;
import com.cricket.fantasy.entity.enums.FantasyPlayerType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "user_fantasy_player")
public class UserFantasyPlayer extends BaseEntity {


    @Enumerated(EnumType.STRING)
    private FantasyPlayerType type;
}
