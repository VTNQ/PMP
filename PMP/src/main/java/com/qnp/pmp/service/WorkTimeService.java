package com.qnp.pmp.service;

import com.qnp.pmp.dto.WorkTimeDTO;
import com.qnp.pmp.dto.WorkTimeViewDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface WorkTimeService {
    List<WorkTimeViewDTO> getWorkTimesByOfficerId(int officerId);
    long getTotalValidWorkingDays(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays);
    long getRoundedValidMonths(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays);
    String getWorkSummary(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays);
}
