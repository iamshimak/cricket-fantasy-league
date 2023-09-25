package com.cricket.fantasy.entity.cricsheet;

import com.cricket.fantasy.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Length;

@Getter
@Setter
@Entity
@Table(name = "cricksheet_match")
public class CricksheetMatch extends BaseEntity {

    private int ballPerOver;
    private String date;
    private String eventName;
    private int eventMatchNumber;
    private String gender;
    private String matchType;
    private int overs;
    private String season;
    private String teamType;
    private String teams;

    @Column(length = Length.LONG32)
    private String json;
}