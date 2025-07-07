package com.qnp.pmp.entity;

import lombok.Data;

@Data
public class TimeSheet {
    private int id;
    private int officerId;
    private String month;
    private String year;
}
