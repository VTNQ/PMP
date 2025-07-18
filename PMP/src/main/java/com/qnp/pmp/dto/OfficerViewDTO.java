package com.qnp.pmp.dto;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OfficerViewDTO {
    private final IntegerProperty id;
    private final StringProperty fullName;
    private final IntegerProperty levelId;
    private final StringProperty levelName;
    private final StringProperty unit;
    private final IntegerProperty birthYear;
    private final StringProperty homeTown;
    private final StringProperty note;
    private final Map<Integer, StudyRoundDTO> studyRounds = new TreeMap<>();
    private final IntegerProperty allowanceMonths = new SimpleIntegerProperty(0);

    private final ObjectProperty<LocalDate> since = new SimpleObjectProperty<>();
    public OfficerViewDTO(int id, String fullName, Integer levelId, String levelName,
                          String unit, Integer birthYear, String homeTown,
                          String note, LocalDate since) {
        this.id = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.levelId = new SimpleIntegerProperty(levelId);
        this.levelName = new SimpleStringProperty(levelName);
        this.unit = new SimpleStringProperty(unit);
        this.homeTown = new SimpleStringProperty(homeTown);
        this.note = new SimpleStringProperty(note);
        this.birthYear = new SimpleIntegerProperty(birthYear);
        this.since.set(since);
    }

    public IntegerProperty getId() {
        return id;
    }
    public StringProperty fullNameProperty() {
        return fullName;
    }
    public IntegerProperty allowanceMonthsProperty() {
        return allowanceMonths;
    }
    public LocalDate getSince() { return since.get(); }

    public int getAllowanceMonths() {
        return allowanceMonths.get();
    }

    public void setAllowanceMonths(int value) {
        allowanceMonths.set(value);
    }

    public IntegerProperty levelIdProperty() {
        return levelId;
    }
    public StringProperty levelNameProperty() {
        return levelName;
    }
    public StringProperty unitProperty() {
        return unit;
    }

    public StringProperty homeTownProperty() {
        return homeTown;
    }
    public StringProperty noteProperty() {
        return note;
    }
    public IntegerProperty birthYearProperty() {
        return birthYear;
    }

    public void addStudyRound(int round, LocalDate start, LocalDate end) {
        studyRounds.put(round, new StudyRoundDTO(round, start, end));
    }

    public Map<Integer, StudyRoundDTO> getStudyRounds() {
        return studyRounds;
    }

//    public long getThoiGianHuongThuHut() {
//        if (since.get() == null || since.get().isBlank()) return 0;
//        try {
//            LocalDate sinceDate = LocalDate.parse(since.get(), DateTimeFormatter.ISO_DATE);
//            return ChronoUnit.MONTHS.between(sinceDate, LocalDate.now());
//        } catch (Exception e) {
//            return 0;
//        }
//    }

}
