package com.qnp.pmp.entity;

import lombok.Data;

@Data
public class AllowanceOfficer {
    private int id;
    private int officerId;
    private int AllowanceId;
    private String firstAmount;
    private int firstStatus;
}
