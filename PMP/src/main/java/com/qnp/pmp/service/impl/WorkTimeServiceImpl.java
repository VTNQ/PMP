package com.qnp.pmp.service.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.qnp.pmp.dto.WorkTimeDTO;
import com.qnp.pmp.service.WorkTimeService;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class WorkTimeServiceImpl implements WorkTimeService {


    private WorkTimeDTO fromDocument(Document doc) {
        WorkTimeDTO dto = new WorkTimeDTO();
        dto.setId(doc.getString("id"));
        dto.setOfficerId(doc.getString("officer_id"));
        dto.setStartDate(doc.getDate("start_date"));
        dto.setEndDate(doc.getDate("end_date"));
        dto.setWorkingDaysCount(doc.getInteger("working_days_count", 0));
        dto.setMonthCalculated(doc.getBoolean("is_month_calculated", false));
        dto.setManualMonthCalculation(doc.getBoolean("is_manual_month_calculation", false));
        dto.setNote(doc.getString("note"));
        return dto;
    }

    private Document toDocument(WorkTimeDTO dto) {
        return new Document("id", dto.getId())
                .append("officer_id", dto.getOfficerId())
                .append("start_date", dto.getStartDate())
                .append("end_date", dto.getEndDate())
                .append("working_days_count", dto.getWorkingDaysCount())
                .append("is_month_calculated", dto.isMonthCalculated())
                .append("is_manual_month_calculation", dto.isManualMonthCalculation())
                .append("note", dto.getNote());
    }

    @Override
    public void insert(WorkTimeDTO dto) {

    }

    @Override
    public void update(WorkTimeDTO dto) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public WorkTimeDTO findById(String id) {
       return null;
    }

    @Override
    public List<WorkTimeDTO> findByOfficerId(String officerId) {
        return null;
    }

    @Override
    public List<WorkTimeDTO> findAll() {
       return null;
    }
}
