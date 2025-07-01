package com.qnp.pmp.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Officer {
    private String id;
    private String code;
    private String fullName;
    private int birthYear;
    private String rank;
    private String position;
    private String hometown;
    private String unit;
    private Date hardshipStartDate;
    private String allowanceStatus;
    private int totalConvertedMonths;
    private int totalConvertedYears;
    private boolean oneTimeSupport;
    private String note;
    private String gender;
    private String workingStatus;
    private String avatar;
}
