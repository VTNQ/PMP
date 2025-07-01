package com.qnp.pmp.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.qnp.pmp.config.MongoDBConnection;
import com.qnp.pmp.dto.OfficerDTO;
import com.qnp.pmp.generic.GeneralService;
import com.qnp.pmp.service.OfficeService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OfficerServiceImpl implements OfficeService {
    private final MongoCollection<Document> officerCollection;
    private GeneralService generalService;

    public OfficerServiceImpl() {
        officerCollection = MongoDBConnection.getDatabase().getCollection("officers");
        generalService = new GeneralService();
    }

    @Override
    public void saveOfficer(File image, OfficerDTO officer) {
        String file = generalService.saveFile(image);
        Document officerDoc = new Document()
                .append("code", officer.getCode())
                .append("fullName", officer.getFullName())
                .append("birthYear", officer.getBirthYear())
                .append("rank", officer.getRank())
                .append("position", officer.getPosition())
                .append("hometown", officer.getHometown())
                .append("unit", officer.getUnit())
                .append("avatar", file)
                .append("workingStatus", officer.getWorkingStatus());
        officerCollection.insertOne(officerDoc);
    }

    @Override
    public void updateOfficer(File image, String id, OfficerDTO officer) {
        String filePath = null;

        if (image != null) {
            filePath = generalService.saveFile(image); // Lưu file mới nếu có
        } else {

            Document existing = officerCollection.find(Filters.eq("_id", new ObjectId(id))).first();
            if (existing != null) {
                filePath = existing.getString("avatar");
            }
        }
        Bson filter = Filters.eq("_id", new ObjectId(id));
        Bson update = Updates.combine(
                Updates.set("code", officer.getCode()),
                Updates.set("fullName", officer.getFullName()),
                Updates.set("birthYear", officer.getBirthYear()),
                Updates.set("rank", officer.getRank()),
                Updates.set("position", officer.getPosition()),
                Updates.set("hometown", officer.getHometown()),
                Updates.set("unit", officer.getUnit()),
                Updates.set("workingStatus", officer.getWorkingStatus()),
                Updates.set("avatar", filePath)
        );
        officerCollection.updateOne(filter, update);
    }

    @Override
    public void deleteOfficer(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        officerCollection.deleteOne(filter);
    }

    @Override
    public List<OfficerDTO> findByNameAndRankAndCodeAndUnitAndWorkingStatus(String name, String code, String rank, String unit, String workingStatus) {
        Bson filter = Filters.and(
                Filters.eq("code", code),
                Filters.eq("unit", unit),
                Filters.eq("name", name),
                Filters.eq("rank", rank),
                Filters.eq("workingStatus", workingStatus)
        );
        FindIterable<Document> documents = officerCollection.find(filter);
        List<OfficerDTO> result = new ArrayList<>();

        for (Document doc : documents) {
            OfficerDTO officer = new OfficerDTO();
            officer.setCode(doc.getString("code"));
            officer.setFullName(doc.getString("fullName"));
            officer.setBirthYear(doc.getInteger("birthYear"));
            officer.setRank(doc.getString("rank"));
            officer.setPosition(doc.getString("position"));
            officer.setHometown(doc.getString("hometown"));
            officer.setUnit(doc.getString("unit"));
            officer.setAvatar(doc.getString("avatar")); 
            result.add(officer);
        }

        return result;
    }
}
