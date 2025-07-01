package com.qnp.pmp.generic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GeneralService {
    public String saveFile(File sourceFile) {
        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs(); // Tạo thư mục nếu chưa có
        }

        // Tạo tên file đích giữ nguyên tên gốc
        File destFile = new File(uploadDir + sourceFile.getName());

        try {
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Trả về đường dẫn tương đối để lưu vào MongoDB
        return "static/" + sourceFile.getName();
    }
}
