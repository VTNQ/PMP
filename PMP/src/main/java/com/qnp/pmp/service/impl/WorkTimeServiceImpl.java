package com.qnp.pmp.service.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.qnp.pmp.config.MongoDBConnection;
import com.qnp.pmp.dto.WorkTimeDTO;
import com.qnp.pmp.service.WorkTimeService;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkTimeServiceImpl implements WorkTimeService {

    private final MongoCollection<Document> collection;

    public WorkTimeServiceImpl() {
        this.collection = MongoDBConnection.getDatabase().getCollection("work_times");
    }

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
        collection.insertOne(toDocument(dto));
    }

    @Override
    public void update(WorkTimeDTO dto) {
        Bson filter = Filters.eq("id", dto.getId());
        Bson updates = Updates.combine(
                Updates.set("officer_id", dto.getOfficerId()),
                Updates.set("start_date", dto.getStartDate()),
                Updates.set("end_date", dto.getEndDate()),
                Updates.set("working_days_count", dto.getWorkingDaysCount()),
                Updates.set("is_month_calculated", dto.isMonthCalculated()),
                Updates.set("is_manual_month_calculation", dto.isManualMonthCalculation()),
                Updates.set("note", dto.getNote())
        );
        collection.updateOne(filter, updates);
    }

    @Override
    public void delete(String id) {
        collection.deleteOne(Filters.eq("id", id));
    }

    @Override
    public WorkTimeDTO findById(String id) {
        Document doc = collection.find(Filters.eq("id", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    @Override
    public List<WorkTimeDTO> findByOfficerId(String officerId) {
        List<WorkTimeDTO> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("officer_id", officerId))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    @Override
    public List<WorkTimeDTO> findAll() {
        List<WorkTimeDTO> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }
}
