package com.qnp.pmp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyTime {
    private int officerId;
    private int round;
    private LocalDate startDate;
    private LocalDate endDate;
}
