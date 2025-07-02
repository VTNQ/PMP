package com.qnp.pmp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class LogDTO {
    private String id;
    private String officerId;
    private String performedBy;
    private String action;
    private String content;
    private Date timestamp;
    private String device;
    private String ipAddress;
}
