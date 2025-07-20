package com.qnp.pmp.service.impl;

import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.StudyRoundViewDTO;
import com.qnp.pmp.entity.StudyTime;
import com.qnp.pmp.service.StudyTimeService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudyTimeServiceImpl implements StudyTimeService {
    @Override
    public void saveStudyTime(StudyTime studyTime) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = MySQLConnection.getConnection();
            String sql = "INSERT INTO studyTimes(officer_id,round,start_date,end_date) VALUES(?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studyTime.getOfficerId());
            stmt.setInt(2, studyTime.getRound());
            stmt.setDate(3, Date.valueOf(studyTime.getStartDate()));
            stmt.setDate(4, Date.valueOf(studyTime.getEndDate()));
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
    public List<StudyRoundViewDTO> getByOfficeId(int officerId) {
        String sql = "SELECT id, officer_id, round, start_date, end_date " +
                "FROM studyTimes WHERE officer_id = ? ORDER BY round ASC";

        List<StudyRoundViewDTO> list = new ArrayList<>();

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, officerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int round = rs.getInt("round");
                    String startDateStr = "";
                    String endDateStr = "";
                    Date startDate = rs.getDate("start_date");
                    Date endDate = rs.getDate("end_date");
                    if (startDate != null) {
                        startDateStr = startDate.toString();
                    }
                    if (endDate != null) {
                        endDateStr = endDate.toString();
                    }
                    StudyRoundViewDTO dto = new StudyRoundViewDTO(id, round, startDateStr, endDateStr);
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // hoặc log lỗi nếu có logger
        }

        return list;
    }

}
