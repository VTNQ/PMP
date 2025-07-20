package com.qnp.pmp.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserViewDTO {
    private final IntegerProperty index;
    private final StringProperty fullName;
    private final StringProperty username;
    public UserViewDTO(int index,String fullName,String username) {
        this.index = new SimpleIntegerProperty(index);
        this.fullName = new SimpleStringProperty(fullName);
        this.username = new SimpleStringProperty(username);
    }
    public IntegerProperty indexProperty(){
        return index;
    }
    public StringProperty fullNameProperty(){
        return fullName;
    }
    public StringProperty usernameProperty(){
        return username;
    }
}
