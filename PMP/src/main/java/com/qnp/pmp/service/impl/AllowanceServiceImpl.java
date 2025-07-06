package com.qnp.pmp.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.qnp.pmp.dto.AllowanceDTO;
import com.qnp.pmp.service.AllowanceService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class AllowanceServiceImpl implements AllowanceService {

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

    }

    @Override
    public List<AllowanceDTO> findByOfficerId(int OfficerId) {
     return List.of();
    }

    @Override
    public void update(String id, AllowanceDTO allowance) {

    }

    @Override
    public void delete(String id) {

    }
}
