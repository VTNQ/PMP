package com.qnp.pmp.service.impl;

import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Level;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.generic.GeneralService;
import com.qnp.pmp.service.LevelService;
import com.qnp.pmp.service.OfficeService;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfficerServiceImpl implements OfficeService {
    private GeneralService generalService;
    private LevelService levelService;
    public OfficerServiceImpl() {
        this.generalService = new GeneralService();
        this.levelService=new LevelServiceImpl();
    }

    @Override
    public void saveOfficer(Officer officer) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = MySQLConnection.getConnection();

            String sql =
                    "INSERT INTO officer(full_name,level_id,unit,hometown,birth_year,note) VALUES(?,?,?,?,?,?);";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, officer.getFullName());
            stmt.setInt(2, officer.getLevelId());
            stmt.setString(3, officer.getUnit());
            stmt.setString(4,officer.getHomeTown() );
            stmt.setInt(5, officer.getBirthYear());
            stmt.setString(6, officer.getNote());
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
        String sql =
                "INSERT INTO officer(full_name,level_id,unit,hometown,birth_year,note) VALUES(?,?,?,?,?,?);";
        try (Connection conn = MySQLConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
        ){
            for (Officer officer : officers) {
                stmt.setString(1, officer.getFullName());
                Level level=levelService.getByName(officer.getLevelName());
                if(level!=null){
                    stmt.setInt(2, level.getId());
                }

                stmt.setString(3, officer.getUnit());
                stmt.setString(4, officer.getHomeTown());
                stmt.setInt(5, officer.getBirthYear());
                stmt.setString(6, officer.getNote());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }catch (SQLException |ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOfficer(int id,Officer officer) {
        String sql = "UPDATE officer SET full_name = ?, level_id = ?, unit = ?, hometown = ?,birth_year= ?,note= ? WHERE id = ?";
        try (Connection connection=MySQLConnection.getConnection()){
            PreparedStatement stmt=connection.prepareStatement(sql);
            stmt.setString(1, officer.getFullName());
            stmt.setInt(2, officer.getLevelId());
            stmt.setString(3, officer.getUnit());
            stmt.setString(4, officer.getHomeTown());
            stmt.setInt(5, officer.getBirthYear());
            stmt.setString(6, officer.getNote());
            stmt.setInt(7, id);
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
        String sql = "SELECT o.id, o.full_name,l.id as levelId, l.name AS level_name, o.unit, o.hometown,o.birth_year,o.note " +
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
                int levelId = rs.getInt("levelId");
                String levelName = rs.getString("level_name");
                String unit = rs.getString("unit");
                String homeTown = rs.getString("hometown");
                int BirthYear = rs.getInt("birth_year");
                String note = rs.getString("note");
                OfficerViewDTO dto = new OfficerViewDTO(id, fullName,levelId, levelName, unit, BirthYear, homeTown, note);
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
        String sql = "SELECT o.id, o.full_name,l.id as levelId, l.name AS level_name, o.unit, o.hometown,o.birth_year,o.note" +
                " From officer o JOIN level l ON o.level_id=l.id";
        try (Connection connection = MySQLConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                int levelId = rs.getInt("levelId");
                String levelName = rs.getString("level_name");
                String unit = rs.getString("unit");
                String homeTown = rs.getString("hometown");
                int BirthYear = rs.getInt("birth_year");
                String note = rs.getString("note");
                OfficerViewDTO dto = new OfficerViewDTO(id, fullName,levelId, levelName, unit, BirthYear, homeTown, note);
                officerViewDTOList.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return officerViewDTOList;

    }
}
