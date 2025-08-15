package com.qnp.pmp.service.impl;
import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.BenefitDetailDTO;
import com.qnp.pmp.entity.Allowance;
import com.qnp.pmp.service.AllowanceService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AllowanceServiceImpl implements AllowanceService {

    @Override
    public void insert(Allowance allowance) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = MySQLConnection.getConnection();
            String sql = "INSERT INTO allowance(officer_id, start_date, end_date, decision_start,decision_end) VALUES (?, ?, ?, ?,?)";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, allowance.getOfficerId());
            stmt.setDate(2, java.sql.Date.valueOf(allowance.getStartDate())); // Chuyển LocalDate -> java.sql.Date
            stmt.setDate(3, java.sql.Date.valueOf(allowance.getEndDate()));   // Chuyển LocalDate -> java.sql.Date
            stmt.setString(4, allowance.getDecisionStart());
            stmt.setString(5, allowance.getDecisionEnd());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public List<BenefitDetailDTO> getBenefitDetails(int id) {
        List<BenefitDetailDTO> benefitDetails = new ArrayList<>();
        String sql = "SELECT id,start_date,end_date,decision_start,decision_end FROM allowance WHERE officer_id = ?";
        PreparedStatement stmt = null;
        try (Connection connection = MySQLConnection.getConnection()) {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idbenefit=rs.getInt("id");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();
                String Decision = rs.getString("decision_start");
                String DecisionEnd = rs.getString("decision_end");
                BenefitDetailDTO benefitDetailDTO = new BenefitDetailDTO(startDate, endDate, Decision, DecisionEnd,id);
                benefitDetails.add(benefitDetailDTO);
            }
            return benefitDetails;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void update(Allowance allowance,int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn=MySQLConnection.getConnection();
            String sql="UPDATE allowance SET start_date=?,end_date=?,decision_start=?,decision_end=? WHERE id=?";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, java.sql.Date.valueOf(allowance.getStartDate()));
            stmt.setDate(2, java.sql.Date.valueOf(allowance.getEndDate()));
            stmt.setString(3, allowance.getDecisionStart());
            stmt.setString(4, allowance.getDecisionEnd());
            stmt.setInt(5,id);
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
