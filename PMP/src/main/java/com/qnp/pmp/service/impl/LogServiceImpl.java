package com.qnp.pmp.service.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.qnp.pmp.dto.LogDTO;
import com.qnp.pmp.service.LogService;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogServiceImpl implements LogService {


    private LogDTO fromDocument(Document doc) {
        return null;
    }

    private Document toDocument(LogDTO dto) {
        return null;
    }

    @Override
    public void insert(LogDTO dto) {

    }

    @Override
    public List<LogDTO> findByOfficerId(String officerId) {
        return null;
    }

    @Override
    public List<LogDTO> findAll() {
        return null;
    }

    @Override
    public List<LogDTO> findRecent(int limit) {
        return null;
    }
}
