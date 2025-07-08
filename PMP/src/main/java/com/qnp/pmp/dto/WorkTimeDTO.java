package com.qnp.pmp.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class WorkTimeDTO {
    private String id;
    private String officerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isMonthCalculated;         // true nếu đợt này được tính
    private boolean isManualMonthCalculation;  // nếu tính thủ công
    private String note;

    // Số ngày thực tế không tính T7, CN, lễ sẽ tính từ service
    public long getCalendarDays() {
        if (startDate == null || endDate == null) return 0;
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public boolean isValid() {
        return startDate != null && endDate != null && isMonthCalculated;
    }
}
