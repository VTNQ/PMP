package com.qnp.pmp.service;

public interface ExcelBackupService {
    void startAutoBackup();
    void manualBackup(String filePath);
}
