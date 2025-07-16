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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        Map<Integer,OfficerViewDTO> officerMap = new HashMap<>();
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
                officerMap.put(id,dto);
            }
            String studySql="SELECT officer_id,round,start_date,end_date From studyTimes ORDER BY officer_id, round";
            stmt = connection.prepareStatement(studySql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int officerId = rs.getInt("officer_id");
                int round = rs.getInt("round");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();
                OfficerViewDTO dto=officerMap.get(officerId);
                if(dto!=null){
                    dto.addStudyRound(round,startDate,endDate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(officerMap.values());
    }

    @Override
    public List<OfficerViewDTO> getOfficerAllowanceStatus() {
        Map<Integer, OfficerViewDTO> officerMap = new HashMap<>();

        String officerSql = "SELECT o.id, o.full_name, l.id AS levelId, l.name AS level_name, o.unit, o.hometown, o.birth_year, o.note " +
                "FROM officer o JOIN level l ON o.level_id = l.id";

        try (Connection connection = MySQLConnection.getConnection()) {

            // Lấy danh sách cán bộ
            PreparedStatement stmt = connection.prepareStatement(officerSql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                int levelId = rs.getInt("levelId");
                String levelName = rs.getString("level_name");
                String unit = rs.getString("unit");
                String homeTown = rs.getString("hometown");
                int birthYear = rs.getInt("birth_year");
                String note = rs.getString("note");

                OfficerViewDTO dto = new OfficerViewDTO(id, fullName, levelId, levelName, unit, birthYear, homeTown, note);
                officerMap.put(id, dto);
            }

            // Lấy dữ liệu thời gian đi học
            String studySql = "SELECT officer_id, round, start_date, end_date FROM studyTimes ORDER BY officer_id, round";
            stmt = connection.prepareStatement(studySql);
            rs = stmt.executeQuery();

            // Lưu tổng số ngày đi học theo từng tháng
            Map<Integer, Map<YearMonth, Integer>> studyDaysByOfficer = new HashMap<>();

            while (rs.next()) {
                int officerId = rs.getInt("officer_id");
                int round = rs.getInt("round");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();

                OfficerViewDTO dto = officerMap.get(officerId);
                if (dto == null) continue;

                dto.addStudyRound(round, startDate, endDate);

                // Đếm số ngày theo từng tháng
                Map<YearMonth, Integer> monthlyDays = studyDaysByOfficer.computeIfAbsent(officerId, k -> new HashMap<>());
                LocalDate date = startDate;
                while (!date.isAfter(endDate)) {
                    YearMonth ym = YearMonth.from(date);
                    monthlyDays.put(ym, monthlyDays.getOrDefault(ym, 0) + 1);
                    date = date.plusDays(1);
                }
            }

            // Tính số tháng được hưởng phụ cấp
            for (Map.Entry<Integer, OfficerViewDTO> entry : officerMap.entrySet()) {
                int officerId = entry.getKey();
                OfficerViewDTO dto = entry.getValue();
                Map<YearMonth, Integer> daysPerMonth = studyDaysByOfficer.getOrDefault(officerId, new HashMap<>());

                Set<YearMonth> allMonths = daysPerMonth.keySet();
                Map<YearMonth, Boolean> allowanceMap = new HashMap<>();

                // Giả sử danh sách các tháng đi học là toàn bộ thời gian hoạt động
                for (YearMonth month : allMonths) {
                    int days = daysPerMonth.getOrDefault(month, 0);
                    // Nếu trong tháng đó đi học <= 15 ngày → được hưởng phụ cấp
                    if (days <= 15) {
                        allowanceMap.put(month, true);
                    } else {
                        allowanceMap.put(month, false);
                    }
                }

                int allowanceMonths = (int) allowanceMap.values().stream().filter(v -> v).count();
                dto.setAllowanceMonths(allowanceMonths);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(officerMap.values());
    }


    @Override
    public List<Officer> getOfficers() {
        List<Officer> officers = new ArrayList<>();
        String sql = "SELECT * FROM officer";
        try(Connection connection=MySQLConnection.getConnection()) {
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Officer officer = new Officer();
                    officer.setId(rs.getInt("id"));
                    officer.setFullName(rs.getString("full_name"));
                    officer.setLevelId(rs.getInt("level_id"));
                    officer.setUnit(rs.getString("unit"));
                    officer.setHomeTown(rs.getString("hometown"));
                    officer.setBirthYear(rs.getInt("birth_year"));
                    officer.setNote(rs.getString("note"));
                    officers.add(officer);
                }
        }catch (Exception e){
            e.printStackTrace();
        }
        return officers;
    }
}
