package com.qnp.pmp.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.AllowanceDTO;
import com.qnp.pmp.entity.Allowance;
import com.qnp.pmp.service.AllowanceService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class AllowanceServiceImpl implements AllowanceService {

    @Override
    public void insert(Allowance allowance) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = MySQLConnection.getConnection();
            String sql = "INSERT INTO allowance(officer_id, start_date, end_date, decision) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, allowance.getOfficerId());
            stmt.setDate(2, java.sql.Date.valueOf(allowance.getStartDate())); // Chuyển LocalDate -> java.sql.Date
            stmt.setDate(3, java.sql.Date.valueOf(allowance.getEndDate()));   // Chuyển LocalDate -> java.sql.Date
            stmt.setString(4, allowance.getDecision());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }


}
