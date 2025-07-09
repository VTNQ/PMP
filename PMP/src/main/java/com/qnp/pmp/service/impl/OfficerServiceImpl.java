package com.qnp.pmp.service.impl;

import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.generic.GeneralService;
import com.qnp.pmp.service.OfficeService;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
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
            stmt.setDate(5, java.sql.Date.valueOf(officer.getSince()));
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
    public void saveOfficerAll(List<Officer> officers) {
        String sql = "INSERT INTO officer(full_name, phone, level_id, unit, since, identifier, hometown, dob) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            for (Officer officer : officers) {
                stmt.setString(1, officer.getFullName());
                stmt.setString(2, officer.getPhone());
                stmt.setInt(3, officer.getLevelId());
                stmt.setString(4, officer.getUnit());
                stmt.setDate(5, java.sql.Date.valueOf(officer.getSince()));
                stmt.setString(6, officer.getIdentifier());
                stmt.setString(7, officer.getHomeTown());
                stmt.setDate(8, java.sql.Date.valueOf(officer.getDob()));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOfficer(int id,Officer officer) {
        String sql = "UPDATE officer SET full_name = ?, phone = ?, level_id = ?, unit = ?, identifier = ?, hometown = ?, dob = ?, since = ? WHERE id = ?";
        try (Connection connection=MySQLConnection.getConnection()){
            PreparedStatement stmt=connection.prepareStatement(sql);
            stmt.setString(1, officer.getFullName());
            stmt.setString(2, officer.getPhone());
            stmt.setInt(3, officer.getLevelId());
            stmt.setString(4, officer.getUnit());
            stmt.setString(5, officer.getIdentifier());
            stmt.setString(6, officer.getHomeTown());
            stmt.setDate(7, Date.valueOf(officer.getDob()));     // LocalDate to SQL Date
            stmt.setDate(8, Date.valueOf(officer.getSince()));
            stmt.setInt(9, id);
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOfficer(int id) {
        String sql="DELETE FROM officer WHERE id = ?";
        try (Connection connection=MySQLConnection.getConnection()){
            PreparedStatement stmt=connection.prepareStatement(sql);
            stmt.setInt(1,id);
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<OfficerViewDTO> findByName(String name) {
        List<OfficerViewDTO> officerViewDTOList = new ArrayList<>();
        String sql = "SELECT o.id, o.full_name, o.phone,l.id as levelId, l.name AS level_name, o.unit, o.identifier, o.hometown, o.dob,o.since " +
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
                int levelId = rs.getInt("levelId");
                String levelName = rs.getString("level_name");
                String unit = rs.getString("unit");
                String identifier = rs.getString("identifier");
                String homeTown = rs.getString("hometown");
                Date dobDate = rs.getDate("dob");
                Date since = rs.getDate("since");
                String dob = (dobDate != null) ? dobDate.toString() : "";
                String sinceString=(since != null) ? since.toString() : "";
                OfficerViewDTO dto = new OfficerViewDTO(id, fullName, phone,levelId, levelName, unit, identifier, homeTown, dob,sinceString);
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
        String sql = "SELECT o.id,o.full_name,o.phone,l.id as levelId,l.name as level_name,o.unit, o.identifier, o.hometown,o.dob,o.since" +
                " From officer o JOIN level l ON o.level_id=l.id";
        try (Connection connection = MySQLConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet re = stmt.executeQuery();
            while (re.next()) {
                int id = re.getInt("id");
                String fullName = re.getString("full_name");
                String phone = re.getString("phone");
                String levelName = re.getString("level_name");
                String unit = re.getString("unit");
                Integer levelId = re.getInt("levelId");
                String identifier = re.getString("identifier");
                String homeTown = re.getString("hometown");
                Date dobDate = re.getDate("dob");
                Date since = re.getDate("since");
                String dob = (dobDate != null) ? dobDate.toString() : "";
                String sinceString=(since != null) ? since.toString() : "";
                OfficerViewDTO dto = new OfficerViewDTO(id, fullName, phone,levelId, levelName, unit, identifier, homeTown, dob,sinceString);
                officerViewDTOList.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return officerViewDTOList;

    }
}
