package com.qnp.pmp.service.impl;

import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.generic.GeneralService;
import com.qnp.pmp.service.OfficeService;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OfficerServiceImpl implements OfficeService {
    private GeneralService generalService;

    public OfficerServiceImpl() {
        this.generalService = new GeneralService();
    }

    @Override
    public void saveOfficer(Officer officer) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = MySQLConnection.getConnection();

            String sql =
                    "INSERT INTO officer(full_name,phone,level_id,unit,since,identifier,hometown,dob) VALUES(?,?,?,?,?,?,?,?);";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, officer.getFullName());
            stmt.setString(2, officer.getPhone());
            stmt.setInt(3, officer.getLevelId());
            stmt.setString(4, officer.getUnit());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            stmt.setString(5, officer.getSince().format(formatter));
            stmt.setString(6, officer.getIdentifier());
            stmt.setString(7, officer.getHomeTown());
            stmt.setDate(8, java.sql.Date.valueOf(officer.getDob()));
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
            }
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void updateOfficer(File image, String id, Officer officer) {

    }

    @Override
    public void deleteOfficer(String id) {

    }

    @Override
    public List<OfficerViewDTO> findByName(String name) {
        List<OfficerViewDTO> officerViewDTOList = new ArrayList<>();
        String sql = "SELECT o.id, o.full_name, o.phone, l.name AS level_name, o.unit, o.identifier, o.hometown, o.dob, o.since " +
                "FROM officer o " +
                "JOIN level l ON o.level_id = l.id " +
                "WHERE o.full_name LIKE ?";

        try (Connection connection = MySQLConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "%" + name + "%"); // tìm gần đúng
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String phone = rs.getString("phone");
                String levelName = rs.getString("level_name");
                String unit = rs.getString("unit");
                String identifier = rs.getString("identifier");
                String homeTown = rs.getString("hometown");
                String dob = rs.getDate("dob") != null ? rs.getDate("dob").toString() : "";
                String since = rs.getString("since");

                OfficerViewDTO dto = new OfficerViewDTO(id, fullName, phone, levelName, unit, identifier, homeTown, dob, since);
                officerViewDTOList.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return officerViewDTOList;
    }

    @Override
    public List<OfficerViewDTO> getOfficerAllowanceStatus() {
        List<OfficerViewDTO> officerViewDTOList = new ArrayList<>();
        String sql = "SELECT o.id, o.full_name, o.phone, l.name AS level_name, o.unit, o.identifier, o.hometown, o.dob, o.since " +
                "FROM officer o JOIN level l ON o.level_id = l.id";

        try (Connection connection = MySQLConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String phone = rs.getString("phone");
                String levelName = rs.getString("level_name");
                String unit = rs.getString("unit");
                String identifier = rs.getString("identifier");
                String homeTown = rs.getString("hometown");
                String dob = rs.getDate("dob") != null ? rs.getDate("dob").toString() : "";
                String since = rs.getString("since");

                OfficerViewDTO dto = new OfficerViewDTO(id, fullName, phone, levelName, unit, identifier, homeTown, dob, since);
                officerViewDTOList.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return officerViewDTOList;
    }
}
