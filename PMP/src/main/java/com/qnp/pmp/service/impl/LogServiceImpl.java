package com.qnp.pmp.service.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.qnp.pmp.config.MongoDBConnection;
import com.qnp.pmp.dto.LogDTO;
import com.qnp.pmp.service.LogService;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogServiceImpl implements LogService {

    private final MongoCollection<Document> collection;

    public LogServiceImpl() {
        this.collection = MongoDBConnection.getDatabase().getCollection("logs");
    }

    private LogDTO fromDocument(Document doc) {
        LogDTO dto = new LogDTO();
        dto.setId(doc.getString("id"));
        dto.setOfficerId(doc.getString("officer_id"));
        dto.setPerformedBy(doc.getString("performed_by"));
        dto.setAction(doc.getString("action"));
        dto.setContent(doc.getString("content"));
        dto.setTimestamp(doc.getDate("timestamp"));
        dto.setDevice(doc.getString("device"));
        dto.setIpAddress(doc.getString("ip_address"));
        return dto;
    }

    private Document toDocument(LogDTO dto) {
        return new Document("id", dto.getId())
                .append("officer_id", dto.getOfficerId())
                .append("performed_by", dto.getPerformedBy())
                .append("action", dto.getAction())
                .append("content", dto.getContent())
                .append("timestamp", dto.getTimestamp() != null ? dto.getTimestamp() : new Date())
                .append("device", dto.getDevice())
                .append("ip_address", dto.getIpAddress());
    }

    @Override
    public void insert(LogDTO dto) {
        collection.insertOne(toDocument(dto));
    }

    @Override
    public List<LogDTO> findByOfficerId(String officerId) {
        List<LogDTO> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("officer_id", officerId))) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    @Override
    public List<LogDTO> findAll() {
        List<LogDTO> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    @Override
    public List<LogDTO> findRecent(int limit) {
        List<LogDTO> list = new ArrayList<>();
        for (Document doc : collection.find().sort(Sorts.descending("timestamp")).limit(limit)) {
            list.add(fromDocument(doc));
        }
        return list;
    }
}
