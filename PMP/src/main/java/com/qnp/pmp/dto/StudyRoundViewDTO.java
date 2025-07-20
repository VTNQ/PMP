package com.qnp.pmp.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudyRoundViewDTO {
    private final IntegerProperty id;
    private final IntegerProperty round;
    private final StringProperty startDate;
    private final StringProperty endDate;
    public StudyRoundViewDTO(int id, int round, String startDate, String endDate) {
        this.id = new SimpleIntegerProperty(id);
        this.round = new SimpleIntegerProperty(round);
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
    }
    public IntegerProperty getId(){
        return id;
    }
    public IntegerProperty roundProperty(){
        return round;
    }
    public StringProperty getStartDate(){
        return startDate;
    }
    public StringProperty getEndDate(){
        return endDate;
    }

}
