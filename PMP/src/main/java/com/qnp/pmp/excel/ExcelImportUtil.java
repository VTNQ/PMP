package com.qnp.pmp.excel;

import com.qnp.pmp.dto.OfficerViewDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Đọc 1 file Excel và trả về danh sách OfficerViewDTO đã map đủ trường,
 * gồm cả các “Lần i Bắt đầu/Kết thúc”.
 */
public class ExcelImportUtil {

    private static final DataFormatter DF = new DataFormatter(true); // giữ nguyên hiển thị Excel

    public static List<OfficerViewDTO> readOfficers(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook wb = new XSSFWorkbook(fis)) {

            List<OfficerViewDTO> all = new ArrayList<>();
            for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                Sheet sheet = wb.getSheetAt(s);
                if (sheet != null) all.addAll(readSheet(sheet));
            }
            return all;
        }
    }

    private static List<OfficerViewDTO> readSheet(Sheet sheet) {
        List<OfficerViewDTO> out = new ArrayList<>();
        if (sheet.getPhysicalNumberOfRows() == 0) return out;

        Row header = sheet.getRow(0);
        if (header == null) return out;

        Map<String, Integer> idx = headerIndex(header);
        int maxRound = detectMaxRound(idx);

        // hỗ trợ nhiều nhãn header khác nhau
        Integer colId            = idx.getOrDefault("ID", null);
        Integer colFullName      = firstOf(idx, "Họ tên", "Ho ten", "Full name");
        Integer colIdentifier    = firstOf(idx, "Mã định danh", "Số hiệu CAND", "Identifier");
        Integer colLevelId       = firstOf(idx, "Trình độ ID", "Cấp bậc ID", "Level Id");
        Integer colLevelName     = firstOf(idx, "Trình độ", "Cấp bậc", "Level");
        Integer colUnit          = firstOf(idx, "Đơn vị", "Unit");
        Integer colBirthYear     = firstOf(idx, "Năm sinh", "Birth year");
        Integer colHomeTown      = firstOf(idx, "Quê quán", "Home town");
        Integer colNote          = firstOf(idx, "Ghi chú", "Note");
        Integer colAllowance     = firstOf(idx, "Số tháng hưởng", "Tổng tháng phụ cấp", "Allowance months");

        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            String fullName = getString(row, colFullName);
            if (isBlank(fullName)) continue; // bỏ dòng trống

            // Thu thập giá trị
            Integer id          = getInt(row, colId);        if (id == null) id = 0;
            Integer levelId     = getInt(row, colLevelId);
            String  levelName   = getString(row, colLevelName);
            String  unit        = getString(row, colUnit);
            Integer birthYear   = getInt(row, colBirthYear);
            String  homeTown    = getString(row, colHomeTown);
            String  note        = getString(row, colNote);
            String  identifier  = getString(row, colIdentifier);

            // ❗ Khởi tạo đúng constructor 9 tham số
            OfficerViewDTO dto = new OfficerViewDTO(
                    id,
                    fullName,
                    levelId,
                    levelName,
                    unit,
                    birthYear,
                    homeTown,
                    note,
                    identifier
            );

            // số tháng phụ cấp (nếu có)
            Integer months = getInt(row, colAllowance);
            if (months != null) dto.setAllowanceMonths(months);

            // Lần i Bắt đầu/Kết thúc
            for (int i = 1; i <= maxRound; i++) {
                String sKey = "Lần " + i + " Bắt đầu";
                String eKey = "Lần " + i + " Kết thúc";
                Integer sCol = idx.get(sKey);
                Integer eCol = idx.get(eKey);

                LocalDate sDate = (sCol != null) ? getDate(row, sCol) : null;
                LocalDate eDate = (eCol != null) ? getDate(row, eCol) : null;

                if (sDate != null || eDate != null) {
                    dto.addStudyRound(i, sDate, eDate); // không dùng setter cho StudyRoundDTO
                }
            }

            out.add(dto);
        }
        return out;
    }

    /* ---------------- helpers ---------------- */

    private static Map<String, Integer> headerIndex(Row header) {
        Map<String, Integer> map = new HashMap<>();
        for (int c = 0; c < header.getLastCellNum(); c++) {
            Cell cell = header.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) continue;
            String key = DF.formatCellValue(cell);
            if (!isBlank(key)) map.put(key.trim(), c);
        }
        return map;
    }

    private static int detectMaxRound(Map<String, Integer> idx) {
        int max = 0;
        for (String k : idx.keySet()) {
            if (k.startsWith("Lần ") && k.endsWith(" Bắt đầu")) {
                try {
                    String mid = k.substring(4, k.length() - " Bắt đầu".length()).trim();
                    int n = Integer.parseInt(mid);
                    max = Math.max(max, n);
                } catch (Exception ignored) {}
            }
        }
        return max;
    }

    private static Integer firstOf(Map<String, Integer> idx, String... keys) {
        for (String k : keys) {
            Integer c = idx.get(k);
            if (c != null) return c;
        }
        return null;
    }

    private static String getString(Row row, Integer col) {
        if (col == null) return "";
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return DF.formatCellValue(cell).trim(); // giữ 0 đầu cho mã định danh
    }

    private static Integer getInt(Row row, Integer col) {
        String s = getString(row, col);
        if (isBlank(s)) return null;
        try {
            if (s.endsWith(".0")) s = s.substring(0, s.length() - 2);
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static LocalDate getDate(Row row, Integer col) {
        if (col == null) return null;
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        try {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        } catch (Exception ignored) {}

        String s = DF.formatCellValue(cell).trim();
        if (s.isEmpty()) return null;

        String[] patterns = {"dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd"};
        for (String p : patterns) {
            try { return LocalDate.parse(s, DateTimeFormatter.ofPattern(p)); }
            catch (Exception ignored) {}
        }
        return null;
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
