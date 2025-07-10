package com.qnp.pmp.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Officer {
    private int id;
    private String fullName;
    private String phone;
    private int levelId;
    private String levelName;
    private String unit;
    private LocalDate since;

    public Officer() {
    }

    private String identifier;
    private String homeTown;
    private LocalDate dob;

    public Officer(String fullName, String phone, String levelName, String unit, LocalDate since, String identifier, String homeTown, LocalDate dob) {
        this.fullName = fullName;
        this.phone = phone;
        this.levelName = levelName;
        this.unit = unit;
        this.since = since;
        this.identifier = identifier;
        this.homeTown = homeTown;
        this.dob = dob;
    }
}
