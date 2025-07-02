package com.qnp.pmp.dto;

import lombok.Data;
import java.util.Date;

@Data
public class WorkTimeDTO {
    private String id;
    private String officerId;
    private Date startDate;
    private Date endDate;
    private int workingDaysCount;
    private boolean isMonthCalculated;
    private boolean isManualMonthCalculation;
    private String note;

    // Thêm các phương thức tính toán
    public long getTotalValidDays() {
        if (startDate == null || endDate == null) return 0;
        long diff = endDate.toInstant().toEpochMilli() - startDate.toInstant().toEpochMilli();
        return (diff / (1000 * 60 * 60 * 24)) + 1;
    }

    public long getConvertedMonths() {
        return getTotalValidDays() >= 15 ? 1 : 0;
    }

    public long getConvertedYears() {
        return getTotalValidDays() >= 180 ? 1 : 0;
    }
}