package com.qnp.pmp.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Log {
    private String id;

    private String officerId;
    private String performedBy;
    private String action;
    private String content;
    private Date timestamp;
    private String device;
    private String ipAddress;
}
