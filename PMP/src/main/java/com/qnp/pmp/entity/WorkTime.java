package com.qnp.pmp.entity;

import lombok.Data;
import java.util.Date;

@Data
public class WorkTime {
    private String id;

    private String officerId;
    private Date startDate;
    private Date endDate;
    private int workingDaysCount;
    private boolean isMonthCalculated;
    private boolean isManualMonthCalculation;
    private String note;
}
