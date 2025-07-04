package com.qnp.pmp.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserViewModel {
    private final StringProperty username;
    private final StringProperty status;


    public UserViewModel(String username, String status) {
        this.username = new SimpleStringProperty(username);
        this.status = new SimpleStringProperty(status);

    }

    public StringProperty usernameProperty() { return username; }
    public StringProperty statusProperty() { return status; }


    public String getUsername() { return username.get(); }
    public String getStatus() { return status.get(); }


}
