package com.qnp.pmp.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Allowance {
    private String id;
    private int officerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String decisionStart;
    private String decisionEnd;
}
