package com.qnp.pmp.entity;

import lombok.Data;

@Data
public class Level {
    private int id;
    private String name;
    private String salary;

    @Override
    public String toString() {
      return  name;
    }
}
