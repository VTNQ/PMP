package com.qnp.pmp.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class OfficerViewDTO {
    private final IntegerProperty id;
    private final StringProperty fullName;
    private final StringProperty phone;
    private final IntegerProperty levelId;
    private final StringProperty levelName;
    private final StringProperty unit;
    private final StringProperty identifier;
    private final StringProperty homeTown;
    private final StringProperty dob;
    private final StringProperty since;


    public OfficerViewDTO(int id,String fullName, String phone,Integer levelId,String levelName, String unit, String identifier, String homeTown, String dob,String since) {
        this.id = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.phone = new SimpleStringProperty(phone);
        this.levelId = new SimpleIntegerProperty(levelId);
       this.levelName = new SimpleStringProperty(levelName);
        this.unit = new SimpleStringProperty(unit);
        this.identifier = new SimpleStringProperty(identifier);
        this.homeTown = new SimpleStringProperty(homeTown);
        this.dob = new SimpleStringProperty(dob);
        this.since = new SimpleStringProperty(since);
    }
    public IntegerProperty getId() {
        return id;
    }
    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public IntegerProperty levelIdProperty() {
        return levelId;
    }
    public StringProperty levelNameProperty() {
        return levelName;
    }
    public StringProperty sinceProperty() {
        return since;
    }

    public StringProperty unitProperty() {
        return unit;
    }

    public StringProperty identifierProperty() {
        return identifier;
    }

    public StringProperty homeTownProperty() {
        return homeTown;
    }

    public StringProperty dobProperty() {
        return dob;
    }

    public String getSince() {
        return since.get();
    }

    public void setSince(String value) {
        since.set(value);
    }


    public long getThoiGianHuongThuHut() {
        if (since.get() == null || since.get().isBlank()) return 0;
        try {
            LocalDate sinceDate = LocalDate.parse(since.get(), DateTimeFormatter.ISO_DATE);
            return ChronoUnit.MONTHS.between(sinceDate, LocalDate.now());
        } catch (Exception e) {
            return 0;
        }
    }

}
