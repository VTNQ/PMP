package com.qnp.pmp.service.impl;


import com.qnp.pmp.config.MySQLConnection;
import com.qnp.pmp.dto.WorkTimeDTO;
import com.qnp.pmp.dto.WorkTimeViewDTO;
import com.qnp.pmp.service.WorkTimeService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorkTimeServiceImpl implements WorkTimeService {


    @Override
    public List<WorkTimeViewDTO> getWorkTimesByOfficerId(int officerId) {
        List<WorkTimeViewDTO> workTimes = new ArrayList<>();

        String sql = """
        SELECT id, officer_id, round, start_date, end_date
        FROM studyTimes
        WHERE officer_id = ?
        ORDER BY round ASC
    """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, officerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int round = rs.getInt("round");
                    LocalDate startDate = rs.getDate("start_date").toLocalDate();
                    LocalDate endDate = rs.getDate("end_date").toLocalDate();

                    WorkTimeViewDTO dto = new WorkTimeViewDTO(round,startDate,endDate);
                    workTimes.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Hoặc logger
        }
        return workTimes;
    }


    @Override
    public long getTotalValidWorkingDays(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays) {
        return workTimes.stream()
                .filter(WorkTimeDTO::isValid)
                .mapToLong(dto -> countWorkingDays(dto.getStartDate(), dto.getEndDate(), holidays))
                .sum();
    }

    @Override
    public long getRoundedValidMonths(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays) {
        long totalMonths = 0;

        for (WorkTimeDTO dto : workTimes) {
            if (!dto.isValid()) continue;

            LocalDate current = dto.getStartDate();
            LocalDate end = dto.getEndDate();

            while (!current.isAfter(end)) {
                YearMonth ym = YearMonth.from(current);
                LocalDate monthStart = ym.atDay(1);
                LocalDate monthEnd = ym.atEndOfMonth();

                LocalDate from = current.isAfter(monthStart) ? current : monthStart;
                LocalDate to = end.isBefore(monthEnd) ? end : monthEnd;

                long workDays = countWorkingDays(from, to, holidays);
                if (workDays >= 15) totalMonths++;

                current = monthEnd.plusDays(1);
            }
        }
        return totalMonths;
    }

    @Override
    public String getWorkSummary(List<WorkTimeDTO> workTimes, Set<LocalDate> holidays) {
        long days = getTotalValidWorkingDays(workTimes, holidays);
        long months = getRoundedValidMonths(workTimes, holidays);
        return String.format("Đã tính được %d tháng (%d ngày công tác)", months, days);
    }

    // ================= Helper ======================

    private long countWorkingDays(LocalDate from, LocalDate to, Set<LocalDate> holidays) {
        long count = 0;
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && !holidays.contains(date)) {
                count++;
            }
        }
        return count;
    }

}
