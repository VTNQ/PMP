package com.qnp.pmp.entity;

import lombok.Data;
import java.util.Date;

@Data
public class WorkTime {
    private String id;
    private int timeSheetId;
    private String hourStart;
    private String hourEnd;
    private String note;
}
