package com.qnp.pmp.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OfficerViewDTO {
    private final StringProperty id;
    private final StringProperty code;
    private final StringProperty fullName;
    private final IntegerProperty birthYear;
    private final StringProperty rank;
    private final StringProperty position;
    private final StringProperty homeTown;
    private final StringProperty unit;
    private final StringProperty avatar;
    private final StringProperty workingStatus;

    public OfficerViewDTO(String id, String code, String fullName, int birthYear, String rank, String position, String homeTown, String unit, String avatar, String workingStatus) {
        this.id = new SimpleStringProperty(id);
        this.code = new SimpleStringProperty(code);
        this.fullName = new SimpleStringProperty(fullName);
        this.birthYear = new SimpleIntegerProperty(birthYear);
        this.rank = new SimpleStringProperty(rank);
        this.position = new SimpleStringProperty(position);
        this.homeTown = new SimpleStringProperty(homeTown);
        this.unit = new SimpleStringProperty(unit);
        this.avatar = new SimpleStringProperty(avatar);
        this.workingStatus = new SimpleStringProperty(workingStatus);
    }
    public StringProperty idProperty() {
        return id;
    }
    public StringProperty codeProperty() {
        return code;
    }
    public StringProperty fullNameProperty() {
        return fullName;
    }
    public IntegerProperty birthYearProperty() {
        return birthYear;
    }
    public StringProperty rankProperty() {
        return rank;
    }
    public StringProperty positionProperty() {
        return position;
    }
    public StringProperty homeTownProperty() {
        return homeTown;
    }
    public StringProperty unitProperty() {
        return unit;
    }
    public StringProperty avatarProperty() {
        return avatar;
    }
    public StringProperty workingStatusProperty() {
        return workingStatus;
    }
}

