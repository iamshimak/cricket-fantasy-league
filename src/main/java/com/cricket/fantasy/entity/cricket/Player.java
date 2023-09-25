package com.cricket.fantasy.entity.cricket;

import com.cricket.fantasy.entity.base.BaseEntity;
import com.cricket.fantasy.entity.enums.PlayerType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "player")
public class Player extends BaseEntity {

    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private PlayerType type;

    private String externalId;
}
