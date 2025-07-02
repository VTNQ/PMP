package com.qnp.pmp.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private String id;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private String status;
    private String email;
    private String phoneNumber;
    private Date createdAt;

}
