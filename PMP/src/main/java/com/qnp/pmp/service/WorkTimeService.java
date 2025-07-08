package com.qnp.pmp.service;

import com.qnp.pmp.dto.WorkTimeDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface WorkTimeService {
    List<WorkTimeDTO> getWorkTimesByOfficerId(String officerId);
    long getTotalValidWorkingDays(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays);
    long getRoundedValidMonths(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays);
    String getWorkSummary(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays);
}
