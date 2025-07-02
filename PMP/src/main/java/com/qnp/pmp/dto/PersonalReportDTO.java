package com.qnp.pmp.dto;

import lombok.Data;

import java.util.List;

@Data
public class PersonalReportDTO {
    private ReportDTO officerInfo;
    private List<WorkTimeDTO> workTimes;
    private List<AllowanceDTO> allowances;
}
