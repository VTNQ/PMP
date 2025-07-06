package com.qnp.pmp.service.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.qnp.pmp.dto.*;
import com.qnp.pmp.service.ReportingService;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

public class ReportingServiceImpl implements ReportingService {


    private ReportDTO mapOfficerToReport(Document doc) {
        ReportDTO dto = new ReportDTO();
        dto.setOfficerId(doc.get("id").toString());
        dto.setFullName(doc.getString("ho_ten"));
        dto.setRank(doc.getString("cap_bac"));
        dto.setPosition(doc.getString("chuc_vu"));
        dto.setUnit(doc.getString("don_vi"));
        return dto;
    }

    @Override
    public List<ReportDTO> getCurrentAllowanceRecipients() {
      return List.of();
    }

    @Override
    public List<AllowanceDTO> getExpiringAllowances(int withinDays) {
      return List.of();
    }

    @Override
    public List<ReportDTO> getQualifiedOfficers() {
      return List.of();
    }

    @Override
    public PersonalReportDTO getPersonalReport(String officerId) {
      return null;
    }
}
