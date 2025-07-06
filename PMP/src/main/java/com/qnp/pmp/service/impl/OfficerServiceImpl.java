package com.qnp.pmp.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.qnp.pmp.dto.OfficerDTO;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.generic.GeneralService;
import com.qnp.pmp.service.OfficeService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OfficerServiceImpl implements OfficeService {


    @Override
    public void saveOfficer(File image, OfficerDTO officer) {

    }

    @Override
    public void updateOfficer(File image, String id, OfficerDTO officer) {

    }

    @Override
    public void deleteOfficer(String id) {

    }

    @Override
    public List<OfficerDTO> findByNameAndRankAndCodeAndUnitAndWorkingStatus(String name, String code, String rank, String unit, String workingStatus) {
       return List.of();
    }

    @Override
    public List<OfficerViewDTO> getOfficerAllowanceStatus() {
        return List.of();
    }
}
