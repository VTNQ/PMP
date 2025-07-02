package com.qnp.pmp.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.qnp.pmp.config.MongoDBConnection;
import com.qnp.pmp.dto.AllowanceDTO;
import com.qnp.pmp.entity.Allowance;
import com.qnp.pmp.service.AllowanceService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class AllowanceServiceImpl implements AllowanceService {
    private final MongoCollection<Document> allowanceMongoCollection;

    public AllowanceServiceImpl() {
        allowanceMongoCollection = MongoDBConnection.getDatabase().getCollection("allowances");
    }

    @Override
    public void insert(AllowanceDTO allowance) {
        Document document = new Document()
                .append("officerId", allowance.getOfficerId())
                .append("type", allowance.getType())
                .append("coefficient", allowance.getCoefficient())
                .append("startDate", allowance.getStartDate())
                .append("endDate", allowance.getEndDate())
                .append("isPad", allowance.isPaid())
                .append("status", allowance.getStatus())
                .append("note", allowance.getNote())
                .append("decisionCode", allowance.getDecisionCode())
                .append("decisionDate", allowance.getDecisionDate())
                .append("salaryType", allowance.getSalaryType());
        allowanceMongoCollection.insertOne(document);
    }

    @Override
    public List<AllowanceDTO> findByOfficerId(int OfficerId) {
        Bson query = Filters.eq("officerId", OfficerId);
        FindIterable<Document>documents = allowanceMongoCollection.find(query);
        List<AllowanceDTO> allowances = new ArrayList<>();
        for (Document document : documents) {
            AllowanceDTO allowance = new AllowanceDTO();
            allowance.setOfficerId(document.getString("officerId"));
            allowance.setType(document.getString("type"));
            allowance.setCoefficient(document.getDouble("coefficient"));
            allowance.setStartDate(document.getDate("startDate"));
            allowance.setEndDate(document.getDate("endDate"));
            allowance.setPaid(document.getBoolean("isPad"));
            allowance.setStatus(document.getString("status"));
            allowance.setNote(document.getString("note"));
            allowance.setDecisionCode(document.getString("decisionCode"));
            allowance.setDecisionDate(document.getDate("decisionDate"));
            allowance.setSalaryType(document.getString("salaryType"));
            allowances.add(allowance);
        }
        return allowances;
    }

    @Override
    public void update(String id, AllowanceDTO allowance) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        Bson update= Updates.combine(
                Updates.set("officerId", allowance.getOfficerId()),
                Updates.set("type",allowance.getType()),
                Updates.set("coefficient",allowance.getCoefficient()),
                Updates.set("startDate",allowance.getStartDate()),
                Updates.set("endDate",allowance.getEndDate()),
                Updates.set("isPad",allowance.isPaid()),
                Updates.set("status",allowance.getStatus()),
                Updates.set("note",allowance.getNote()),
                Updates.set("decisionCode",allowance.getDecisionCode()),
                Updates.set("decisionDate",allowance.getDecisionDate()),
                Updates.set("salaryType",allowance.getSalaryType())
        );
        allowanceMongoCollection.updateOne(filter, update);
    }

    @Override
    public void delete(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        allowanceMongoCollection.deleteOne(filter);
    }
}
