package com.qnp.pmp.dto;

import lombok.Data;

@Data
public class OfficerDTO {
    private int id;
    private String code;
    private String fullName;
    private int birthYear;
    private String rank;
    private String position;
    private String hometown;
    private String unit;
    private String avatar;
    private String workingStatus;
}
