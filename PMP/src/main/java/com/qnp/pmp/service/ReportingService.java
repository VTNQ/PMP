package com.qnp.pmp.service;

import com.qnp.pmp.dto.*;

import java.util.List;

public interface ReportingService {
    List<ReportDTO> getCurrentAllowanceRecipients();
    List<AllowanceDTO> getExpiringAllowances(int withinDays);
    List<ReportDTO> getQualifiedOfficers();
    PersonalReportDTO getPersonalReport(String officerId);
}
