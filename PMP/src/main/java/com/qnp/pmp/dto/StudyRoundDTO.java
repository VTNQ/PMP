package com.qnp.pmp.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Getter
public class StudyRoundDTO {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int round;
    public StudyRoundDTO(int round,LocalDate startDate, LocalDate endDate) {
        this.round = round;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public String getFormatted(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return startDate.format(formatter) + " → " + endDate.format(formatter);
    }

}
