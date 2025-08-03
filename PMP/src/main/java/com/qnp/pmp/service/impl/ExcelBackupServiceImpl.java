package com.qnp.pmp.service.impl;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.dto.StudyRoundDTO;
import com.qnp.pmp.service.ExcelBackupService;
import com.qnp.pmp.service.OfficeService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExcelBackupServiceImpl implements ExcelBackupService {
    private final OfficeService officeService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ExcelBackupServiceImpl(OfficeService officeService) {
        this.officeService = officeService;
    }

    @Override
    public void startAutoBackup() {
        // 🔁 Backup ngay 1 lần đầu tiên khi khởi động
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = "backup/officer_backup_" + timestamp + ".xlsx";
        doBackup(filePath);

        // ⏳ Sau đó lên lịch backup mỗi 24h
        scheduler.scheduleAtFixedRate(() -> {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String path = "backup/officer_backup_" + ts + ".xlsx";
            doBackup(path);
        }, 24, 24, TimeUnit.HOURS);
    }



    private void doBackup(String filePath) {
        List<OfficerViewDTO> officers = officeService.getOfficerAllowanceStatus();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Officers");

            // 1️⃣ Tìm số lần công tác lớn nhất
            int maxStudyRounds = officers.stream()
                    .mapToInt(o -> o.getStudyRounds().size())
                    .max()
                    .orElse(0);

            // 2️⃣ Tiêu đề cột
            Row header = sheet.createRow(0);
            int col = 0;
            header.createCell(col++).setCellValue("ID");
            header.createCell(col++).setCellValue("Họ tên");
            header.createCell(col++).setCellValue("Mã định danh");
            header.createCell(col++).setCellValue("Trình độ");
            header.createCell(col++).setCellValue("Đơn vị");
            header.createCell(col++).setCellValue("Năm sinh");
            header.createCell(col++).setCellValue("Quê quán");
            header.createCell(col++).setCellValue("Ghi chú");
            header.createCell(col++).setCellValue("Ngày bắt đầu hưởng");
            header.createCell(col++).setCellValue("Ngày kết thúc hưởng");
            header.createCell(col++).setCellValue("Số tháng hưởng");

            for (int i = 1; i <= maxStudyRounds; i++) {
                header.createCell(col++).setCellValue("Lần " + i + " Bắt đầu");
                header.createCell(col++).setCellValue("Lần " + i + " Kết thúc");
            }

            // 3️⃣ Ghi dữ liệu từng cán bộ
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (int i = 0; i < officers.size(); i++) {
                OfficerViewDTO o = officers.get(i);
                Row row = sheet.createRow(i + 1);
                col = 0;

                row.createCell(col++).setCellValue(o.getId().get());
                row.createCell(col++).setCellValue(o.fullNameProperty().get());
                row.createCell(col++).setCellValue(o.identifierCodeProperty().get());
                row.createCell(col++).setCellValue(o.levelNameProperty().get());
                row.createCell(col++).setCellValue(o.unitProperty().get());
                row.createCell(col++).setCellValue(o.birthYearProperty().get());
                row.createCell(col++).setCellValue(o.homeTownProperty().get());
                row.createCell(col++).setCellValue(o.noteProperty().get());
                row.createCell(col++).setCellValue(o.getSince() != null ? o.getSince().format(formatter) : "");
                row.createCell(col++).setCellValue(o.getUtil() != null ? o.getUtil().format(formatter) : "");
                row.createCell(col++).setCellValue(o.getAllowanceMonths());

                for (int j = 1; j <= maxStudyRounds; j++) {
                    StudyRoundDTO round = o.getStudyRounds().get(j);
                    if (round != null) {
                        row.createCell(col++).setCellValue(round.getStartDate().format(formatter));
                        row.createCell(col++).setCellValue(round.getEndDate().format(formatter));
                    } else {
                        row.createCell(col++).setCellValue("");
                        row.createCell(col++).setCellValue("");
                    }
                }
            }

            // 4️⃣ Ghi file
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                System.out.println("✅ Đã tạo file backup: " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
