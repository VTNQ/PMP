package com.qnp.pmp.dto;

import lombok.Data;

@Data
public class ReportDTO {
    private String officerId;
    private String fullName;
    private String rank;
    private String position;
    private String unit;
    private int totalConvertedMonths;
    private int totalConvertedYears;
}
