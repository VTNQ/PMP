package com.qnp.pmp.service.impl;

import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.generic.GeneralService;
import com.qnp.pmp.service.OfficeService;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
                    "INSERT INTO officer(full_name,phone,level_id,unit,since,identifier,hometown) VALUES(?,?,?,?,?,?,?);";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, officer.getFullName());
            stmt.setString(2, officer.getPhone());
            stmt.setInt(3, officer.getLevelId());
            stmt.setString(4, officer.getUnit());
            stmt.setString(5, officer.getSince());
            stmt.setString(6, officer.getIdentifier());
            stmt.setString(7, officer.getHomeTown());
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
    public List<Officer> findByNameAndRankAndCodeAndUnitAndWorkingStatus(String name, String code, String rank, String unit, String workingStatus) {
        return List.of();
    }

    @Override
    public List<OfficerViewDTO> getOfficerAllowanceStatus() {
        return List.of();
    }
}
