package com.qnp.pmp.entity;

import com.qnp.pmp.Enum.RoleUser;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private RoleUser role;
    private LocalDateTime createdAt;
    private LocalDateTime lastSign;
}
