package com.qnp.pmp.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Allowance {
    private String id;
    private String name;
    private String percent;
    private String yearStart;
    private String yearEnd;
}
