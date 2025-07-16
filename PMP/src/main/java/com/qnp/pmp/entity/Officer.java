package com.qnp.pmp.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Officer {
        private int id;
        private String fullName;
        private int levelId;
        private String levelName;
        private String unit;
        private int birthYear;
        private String note;
    public Officer() {
    }


    private String homeTown;



    public Officer(String fullName, String levelName, String unit, String homeTown, int birthYear, String note) {
        this.fullName = fullName;
        this.levelName = levelName;
        this.unit = unit;
        this.homeTown = homeTown;
        this.birthYear = birthYear;
        this.note = note;
    }
    @Override
    public String toString() {
        return fullName;
    }
}
