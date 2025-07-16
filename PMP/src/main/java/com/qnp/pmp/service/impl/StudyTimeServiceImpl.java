package com.qnp.pmp.service.impl;

import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.entity.StudyTime;
import com.qnp.pmp.service.StudyTimeService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

public class StudyTimeServiceImpl implements StudyTimeService {
    @Override
    public void saveStudyTime(StudyTime studyTime) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn= MySQLConnection.getConnection();
            String sql="INSERT INTO studyTimes(officer_id,round,start_date,end_date) VALUES(?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studyTime.getOfficerId());
            stmt.setInt(2, studyTime.getRound());
            stmt.setDate(3, Date.valueOf(studyTime.getStartDate()));
            stmt.setDate(4, Date.valueOf(studyTime.getEndDate()));
            stmt.executeUpdate();
        }catch (Exception e) {
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
}
