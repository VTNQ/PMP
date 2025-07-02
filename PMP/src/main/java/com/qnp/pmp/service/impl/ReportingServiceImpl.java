package com.qnp.pmp.service.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.qnp.pmp.config.MongoDBConnection;
import com.qnp.pmp.dto.*;
import com.qnp.pmp.service.ReportingService;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

public class ReportingServiceImpl implements ReportingService {

    private final MongoCollection<Document> officerCol = MongoDBConnection.getDatabase().getCollection("officers");
    private final MongoCollection<Document> allowanceCol = MongoDBConnection.getDatabase().getCollection("allowances");
    private final MongoCollection<Document> workTimeCol = MongoDBConnection.getDatabase().getCollection("work_times");
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
        List<ReportDTO> result = new ArrayList<>();

        Date now = new Date();
        for (Document allowance : allowanceCol.find(Filters.and(
                Filters.lte("bat_dau", now),
                Filters.or(Filters.gte("ket_thuc", now), Filters.exists("ket_thuc", false))
        ))) {
            String officerId = allowance.get("officer_id").toString();
            Document officer = officerCol.find(Filters.eq("id", Long.valueOf(officerId))).first();
            if (officer != null) {
                result.add(mapOfficerToReport(officer));
            }
        }

        // Loại bỏ trùng officer
        return result.stream()
                .collect(Collectors.toMap(ReportDTO::getOfficerId, dto -> dto, (a, b) -> a))
                .values().stream().toList();
    }

    @Override
    public List<AllowanceDTO> getExpiringAllowances(int withinDays) {
        List<AllowanceDTO> result = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, withinDays);
        Date upperBound = calendar.getTime();

        for (Document doc : allowanceCol.find(Filters.and(
                Filters.gte("ket_thuc", new Date()),
                Filters.lte("ket_thuc", upperBound)
        )).sort(Sorts.ascending("ket_thuc"))) {
            AllowanceDTO dto = new AllowanceDTO();
            dto.setOfficerId(doc.get("officer_id").toString());
            dto.setType(doc.getString("loai"));
            dto.setStartDate(doc.getDate("bat_dau"));
            dto.setEndDate(doc.getDate("ket_thuc"));
            dto.setStatus(doc.getString("trang_thai"));
            dto.setCoefficient(doc.getDouble("he_so"));
            dto.setPaid(doc.getBoolean("da_lanh"));
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<ReportDTO> getQualifiedOfficers() {
        List<ReportDTO> result = new ArrayList<>();

        Map<String, Integer> officerMonthMap = new HashMap<>();

        for (Document doc : workTimeCol.find(Filters.eq("da_tinh_thang", true))) {
            String officerId = doc.get("officer_id").toString();
            int days = doc.getInteger("ngay_tinh_cong", 0);
            int months = (days >= 15) ? 1 : 0;

            officerMonthMap.put(officerId, officerMonthMap.getOrDefault(officerId, 0) + months);
        }

        for (String officerId : officerMonthMap.keySet()) {
            int totalMonths = officerMonthMap.get(officerId);
            if (totalMonths >= 6) {
                Document officer = officerCol.find(Filters.eq("id", Long.valueOf(officerId))).first();
                if (officer != null) {
                    ReportDTO dto = mapOfficerToReport(officer);
                    dto.setTotalConvertedMonths(totalMonths);
                    dto.setTotalConvertedYears(totalMonths / 12);
                    result.add(dto);
                }
            }
        }

        return result;
    }

    @Override
    public PersonalReportDTO getPersonalReport(String officerId) {
        PersonalReportDTO report = new PersonalReportDTO();

        Document officer = officerCol.find(Filters.eq("id", Long.valueOf(officerId))).first();
        if (officer != null) {
            ReportDTO dto = mapOfficerToReport(officer);
            report.setOfficerInfo(dto);
        }

        List<WorkTimeDTO> workList = new ArrayList<>();
        for (Document doc : workTimeCol.find(Filters.eq("officer_id", Long.valueOf(officerId)))) {
            WorkTimeDTO dto = new WorkTimeDTO();
            dto.setId(doc.get("_id").toString());
            dto.setOfficerId(officerId);
            dto.setStartDate(doc.getDate("bat_dau"));
            dto.setEndDate(doc.getDate("ket_thuc"));
            dto.setWorkingDaysCount(doc.getInteger("ngay_tinh_cong", 0));
            dto.setMonthCalculated(doc.getBoolean("da_tinh_thang", false));
            dto.setManualMonthCalculation(doc.getBoolean("tinh_thang_thu_cong", false));

            dto.setNote(doc.getString("ghi_chu"));
            workList.add(dto);
        }

        List<AllowanceDTO> allowanceList = new ArrayList<>();
        for (Document doc : allowanceCol.find(Filters.eq("officer_id", Long.valueOf(officerId)))) {
            AllowanceDTO dto = new AllowanceDTO();
            dto.setOfficerId(officerId);
            dto.setType(doc.getString("loai"));
            dto.setStartDate(doc.getDate("bat_dau"));
            dto.setEndDate(doc.getDate("ket_thuc"));
            dto.setStatus(doc.getString("trang_thai"));
            dto.setCoefficient(doc.getDouble("he_so"));
            dto.setPaid(doc.getBoolean("da_lanh"));
            allowanceList.add(dto);
        }

        report.setWorkTimes(workList);
        report.setAllowances(allowanceList);
        return report;
    }
}
