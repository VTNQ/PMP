package com.qnp.pmp.entity;


import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Data
public class Officer {
        private int id;
        private String fullName;
        private int levelId;
        private String levelName;
        private String unit;
        private int birthYear;
        private String note;
        private LocalDate since;
        private LocalDate until;
        private String identifierCode;
    public Officer() {
    }

    private Map<Integer, Pair<LocalDate, LocalDate>> studyTimes;
    private String homeTown;



    public Officer(String fullName,String identifierCode,int birthYear, LocalDate since,LocalDate util, String levelName, String unit, String homeTown, String note) {
        this.fullName = fullName;
        this.identifierCode = identifierCode;
        this.birthYear = birthYear;
        this.since = since;
        this.until = util;
        this.levelName = levelName;
        this.unit = unit;
        this.homeTown = homeTown;
        this.note = note;
    }


    @Override
    public String toString() {
        return fullName;
    }
}
