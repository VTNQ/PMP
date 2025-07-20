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
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
                    "INSERT INTO officer(full_name,level_id,unit,hometown,birth_year,note,since) VALUES(?,?,?,?,?,?,?);";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, officer.getFullName());
            stmt.setInt(2, officer.getLevelId());
            stmt.setString(3, officer.getUnit());
            stmt.setString(4,officer.getHomeTown() );
            stmt.setInt(5, officer.getBirthYear());
            stmt.setString(6, officer.getNote());
            stmt.setDate(7, java.sql.Date.valueOf(officer.getSince()));
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
        String sql = "UPDATE officer SET full_name = ?, level_id = ?, unit = ?, hometown = ?,birth_year= ?,note= ?,since=? WHERE id = ?";
        try (Connection connection=MySQLConnection.getConnection()){
            PreparedStatement stmt=connection.prepareStatement(sql);
            stmt.setString(1, officer.getFullName());
            stmt.setInt(2, officer.getLevelId());
            stmt.setString(3, officer.getUnit());
            stmt.setString(4, officer.getHomeTown());
            stmt.setInt(5, officer.getBirthYear());
            stmt.setString(6, officer.getNote());
            stmt.setDate(7, java.sql.Date.valueOf(officer.getSince()));
            stmt.setInt(8, id);
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
        Map<Integer, OfficerViewDTO> officerMap = new HashMap<>();
        String sql = "SELECT o.id, o.full_name, l.id as levelId, l.name AS level_name, o.unit, o.hometown, o.birth_year, o.note, o.since " +
                "FROM officer o " +
                "JOIN level l ON o.level_id = l.id " +
                "WHERE o.full_name LIKE ?";

        try (Connection connection = MySQLConnection.getConnection()) {

            // Bước 1: Lấy danh sách cán bộ
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "%" + name + "%");
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

                Date sqlSince = rs.getDate("since");
                LocalDate since = (sqlSince != null) ? sqlSince.toLocalDate() : null;

                OfficerViewDTO dto = new OfficerViewDTO(id, fullName, levelId, levelName,
                        unit, birthYear, homeTown, note, since);
                officerMap.put(id, dto);
            }

            if (officerMap.isEmpty()) {
                return new ArrayList<>();
            }

            // Bước 2: Lấy thông tin thời gian đi học
            String idPlaceholders = officerMap.keySet().stream()
                    .map(id -> "?")
                    .collect(Collectors.joining(", "));

            String studySql = "SELECT a.officer_id, a.round, a.start_date, a.end_date " +
                    "FROM studyTimes a " +
                    "WHERE a.officer_id IN (" + idPlaceholders + ") " +
                    "ORDER BY a.officer_id, a.round";

            stmt = connection.prepareStatement(studySql);

            int index = 1;
            for (Integer id : officerMap.keySet()) {
                stmt.setInt(index++, id);
            }

            rs = stmt.executeQuery();

            Map<Integer, Map<YearMonth, Integer>> studyDaysByOfficer = new HashMap<>();

            while (rs.next()) {
                int officerId = rs.getInt("officer_id");
                int round = rs.getInt("round");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();

                OfficerViewDTO dto = officerMap.get(officerId);
                if (dto == null) continue;

                dto.addStudyRound(round, startDate, endDate);

                Map<YearMonth, Integer> monthlyDays = studyDaysByOfficer
                        .computeIfAbsent(officerId, k -> new HashMap<>());

                LocalDate date = startDate;
                while (!date.isAfter(endDate)) {
                    YearMonth ym = YearMonth.from(date);
                    monthlyDays.put(ym, monthlyDays.getOrDefault(ym, 0) + 1);
                    date = date.plusDays(1);
                }
            }

            // Bước 3: Tính số tháng được hưởng phụ cấp
            for (Map.Entry<Integer, OfficerViewDTO> entry : officerMap.entrySet()) {
                int officerId = entry.getKey();
                OfficerViewDTO dto = entry.getValue();
                Map<YearMonth, Integer> daysPerMonth = studyDaysByOfficer.getOrDefault(officerId, Map.of());

                LocalDate sinceDate = dto.getSince();
                YearMonth sinceMonth = (sinceDate != null) ? YearMonth.from(sinceDate) : YearMonth.of(1900, 1);

                int count = 0;
                for (Map.Entry<YearMonth, Integer> monthEntry : daysPerMonth.entrySet()) {
                    YearMonth month = monthEntry.getKey();
                    int studyDays = monthEntry.getValue();
                    if (!month.isBefore(sinceMonth) && studyDays <= 15) {
                        count++;
                    }
                }

                dto.setAllowanceMonths(count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(officerMap.values());
    }


    @Override
    public List<OfficerViewDTO> getOfficerAllowanceStatus() {
        Map<Integer, OfficerViewDTO> officerMap = new HashMap<>();

        String officerSql = """
        SELECT o.id, o.full_name, l.id AS levelId, l.name AS level_name,
               o.unit, o.hometown, o.birth_year, o.note, o.since
        FROM officer o
        JOIN level l ON o.level_id = l.id
    """;

        try (Connection connection = MySQLConnection.getConnection()) {

            // Bước 1: Lấy danh sách cán bộ
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

                Date sqlSince = rs.getDate("since");
                LocalDate since = (sqlSince != null) ? sqlSince.toLocalDate() : null;

                OfficerViewDTO dto = new OfficerViewDTO(id, fullName, levelId, levelName,
                        unit, birthYear, homeTown, note, since);
                officerMap.put(id, dto);
            }

            if (officerMap.isEmpty()) {
                return new ArrayList<>();
            }

            // Bước 2: Lấy thông tin thời gian đi học cho officer_id nằm trong danh sách officerMap
            String idPlaceholders = officerMap.keySet().stream()
                    .map(id -> "?")
                    .collect(Collectors.joining(", "));

            String studySql = "SELECT officer_id, round, start_date, end_date " +
                    "FROM studyTimes " +
                    "WHERE officer_id IN (" + idPlaceholders + ") " +
                    "ORDER BY officer_id, round";

            stmt = connection.prepareStatement(studySql);
            int index = 1;
            for (Integer id : officerMap.keySet()) {
                stmt.setInt(index++, id);
            }

            rs = stmt.executeQuery();

            Map<Integer, Map<YearMonth, Integer>> studyDaysByOfficer = new HashMap<>();

            while (rs.next()) {
                int officerId = rs.getInt("officer_id");
                int round = rs.getInt("round");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();

                OfficerViewDTO dto = officerMap.get(officerId);
                if (dto == null) continue;

                dto.addStudyRound(round, startDate, endDate);

                Map<YearMonth, Integer> monthlyDays = studyDaysByOfficer
                        .computeIfAbsent(officerId, k -> new HashMap<>());

                LocalDate date = startDate;
                while (!date.isAfter(endDate)) {
                    YearMonth ym = YearMonth.from(date);
                    monthlyDays.put(ym, monthlyDays.getOrDefault(ym, 0) + 1);
                    date = date.plusDays(1);
                }
            }

            // Bước 3: Tính số tháng được hưởng phụ cấp
            for (Map.Entry<Integer, OfficerViewDTO> entry : officerMap.entrySet()) {
                int officerId = entry.getKey();
                OfficerViewDTO dto = entry.getValue();
                Map<YearMonth, Integer> daysPerMonth = studyDaysByOfficer.getOrDefault(officerId, Map.of());

                LocalDate sinceDate = dto.getSince();
                YearMonth sinceMonth = (sinceDate != null) ? YearMonth.from(sinceDate) : YearMonth.of(1900, 1);

                int count = 0;
                for (Map.Entry<YearMonth, Integer> monthEntry : daysPerMonth.entrySet()) {
                    YearMonth month = monthEntry.getKey();
                    int studyDays = monthEntry.getValue();
                    if (!month.isBefore(sinceMonth) && studyDays <= 15) {
                        count++;
                    }
                }

                dto.setAllowanceMonths(count);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Gợi ý: dùng logger thay vì in ra console
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
