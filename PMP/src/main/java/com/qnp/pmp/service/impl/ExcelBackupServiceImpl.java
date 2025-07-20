package com.qnp.pmp.service.impl;

import com.qnp.pmp.dto.OfficerViewDTO;
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
        scheduler.scheduleAtFixedRate(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filePath = "backup/officer_backup_" + timestamp + ".xlsx";
            doBackup(filePath);
        }, 0, 24, TimeUnit.HOURS);// chạy mỗi 24h
    }

    @Override
    public void manualBackup(String filePath) {
        doBackup(filePath);
    }

    private void doBackup(String filePath) {
        List<OfficerViewDTO> officers = officeService.getOfficerAllowanceStatus();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Officers");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Họ tên");
            header.createCell(2).setCellValue("Trình độ");
            header.createCell(3).setCellValue("Đơn vị");
            header.createCell(4).setCellValue("Năm sinh");
            header.createCell(5).setCellValue("Quê quán");
            header.createCell(6).setCellValue("Ghi chú");
            header.createCell(7).setCellValue("Số tháng hưởng");

            for (int i = 0; i < officers.size(); i++) {
                OfficerViewDTO o = officers.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(o.getId().get());
                row.createCell(1).setCellValue(o.fullNameProperty().get());
                row.createCell(2).setCellValue(o.levelNameProperty().get());
                row.createCell(3).setCellValue(o.unitProperty().get());
                row.createCell(4).setCellValue(o.birthYearProperty().get());
                row.createCell(5).setCellValue(o.homeTownProperty().get());
                row.createCell(6).setCellValue(o.noteProperty().get());
                row.createCell(7).setCellValue(o.getAllowanceMonths());
            }

            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // tạo thư mục nếu chưa có
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                System.out.println("✅ Backup Excel created at: " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
