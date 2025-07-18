package com.qnp.pmp.dto;

import lombok.Data;

import java.util.Date;
@Data
public class AllowanceDTO {
    private String officerId;
    private String type;
    private double coefficient;
    private Date startDate;
    private Date endDate;
    private boolean isPaid;
    private String status;
    private String note;
    private String decisionCode;
    private Date decisionDate;
    private String salaryType;
}
