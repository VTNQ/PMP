package com.qnp.pmp.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.util.Locale;

public class WorkTimeViewDTO {
    private final IntegerProperty round;
    private final ObjectProperty<LocalDate>startDate;
    private final ObjectProperty<LocalDate>endDate;
    public WorkTimeViewDTO(int round,LocalDate startDate,LocalDate endDate){
        this.round=new SimpleIntegerProperty(round);
        this.startDate=new SimpleObjectProperty<LocalDate>(startDate);
        this.endDate=new SimpleObjectProperty<>(endDate);
    }
    public IntegerProperty roundProperty(){
        return round;
    }
    public ObjectProperty<LocalDate> startDateProperty(){
        return startDate;
    }
    public ObjectProperty<LocalDate> endDateProperty(){
        return endDate;
    }
}
