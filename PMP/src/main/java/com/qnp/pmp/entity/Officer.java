package com.qnp.pmp.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Officer {
    private int id;
    private String fullName;
    private String phone;
    private int levelId;
    private String unit;
    private String since;
    private String identifier;
    private String homeTown;
}
