package com.qnp.pmp.dto;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class BenefitDetailDTO {
    private final ObjectProperty<Integer>id=new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate>startDateActual = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> endDateActual = new SimpleObjectProperty<>();
    private final StringProperty decisionStart= new SimpleStringProperty();
    private final StringProperty decisionEnd= new SimpleStringProperty();
    public BenefitDetailDTO(LocalDate startDate,LocalDate endDate,String decisionStart,String decisionEnd,int id) {
        setStartDateActual(startDate);
        setEndDateActual(endDate);
        setDecisionStart(decisionStart);
        setDecisionEnd(decisionEnd);
        setId(id);
    }
    public int getId() {
        return id.get();
    }
    public void setId(int id) {
        this.id.set(id);
    }
    public  ObjectProperty<Integer> idProperty() {
        return id;
    }
    public LocalDate getStartDateActual() {
        return startDateActual.get();
    }
    public void setStartDateActual(LocalDate startDateActual) {
        this.startDateActual.set(startDateActual);
    }
    public ObjectProperty<LocalDate> startDateActualProperty() {
        return startDateActual;
    }
    public LocalDate getEndDateActual() {
        return endDateActual.get();
    }
    public void setEndDateActual(LocalDate endDateActual) {
        this.endDateActual.set(endDateActual);
    }
    public ObjectProperty<LocalDate>endDateActualProperty() {
        return endDateActual;
    }
    public String getDecisionStart() {
        return decisionStart.get();
    }
    public void setDecisionStart(String decisionStart) {
        this.decisionStart.set(decisionStart);
    }
    public StringProperty decisionStartProperty() {
        return decisionStart;
    }
    public String getDecisionEnd() {
        return decisionEnd.get();
    }
    public void setDecisionEnd(String decisionEnd) {
        this.decisionEnd.set(decisionEnd);
    }
    public StringProperty decisionEndProperty() {
        return decisionEnd;
    }
}
