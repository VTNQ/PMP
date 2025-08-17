package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.entity.Level;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.service.LevelService;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.LevelServiceImpl;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddOfficerController {

    private final OfficeService officeService;
    private final LevelService levelService;

    @FXML private Button closeButton;

    @FXML private TextField identifierField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<Level> levelComboBox;
    @FXML private TextField unitField;
    @FXML private TextField birthYearField;
    @FXML private TextArea  noteField;
    @FXML private TextField homeTownField;
    @FXML private Label     fileLabel;

    private File selectedFile;

    public AddOfficerController() {
        this.officeService = new OfficerServiceImpl();
        this.levelService  = new LevelServiceImpl();
    }

    @FXML
    public void initialize() {
        List<Level> levels = levelService.getAll();
        levelComboBox.setItems(FXCollections.observableList(levels));
        levelComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Level l) { return l != null ? l.getName() : ""; }
            @Override public Level fromString(String s) {
                return levelComboBox.getItems().stream()
                        .filter(l -> Objects.equals(l.getName(), s))
                        .findFirst().orElse(null);
            }
        });
    }
    private Runnable onSuccess;              // <-- thêm
    public void setOnSuccess(Runnable r) {   // <-- thêm
        this.onSuccess = r;
    }

    @FXML
    private void onClose() {
        Window w = closeButton.getScene() != null ? closeButton.getScene().getWindow() : null;
        if (w instanceof Stage) ((Stage) w).close();
    }

    @FXML
    private void onChooseFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Officer");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel (*.xlsx, *.xls)", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv")
        );
        Window owner = closeButton.getScene() != null ? closeButton.getScene().getWindow() : null;
        selectedFile = fc.showOpenDialog(owner);
        fileLabel.setText(selectedFile != null ? selectedFile.getName() : "Chưa chọn file");
    }

    /* ================= Helpers ================= */

    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ISO_LOCAL_DATE
    };

    private int tryParseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private LocalDate parseDateFlexible(String s) {
        if (s == null || s.isBlank()) return null;
        for (DateTimeFormatter f : DATE_FORMATS) {
            try { return LocalDate.parse(s.trim(), f); } catch (Exception ignored) {}
        }
        return null;
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue()
                            .toLocalDate()
                            .format(DATE_FORMATS[0]); // dd/MM/yyyy
                }
                double d = cell.getNumericCellValue();
                long l = Math.round(d);
                return (Math.abs(d - l) < 1e-9) ? String.valueOf(l) : String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    try {
                        double dv = cell.getNumericCellValue();
                        long lv = Math.round(dv);
                        return (Math.abs(dv - lv) < 1e-9) ? String.valueOf(lv) : String.valueOf(dv);
                    } catch (Exception ignored) { return ""; }
                }
            default:
                return "";
        }
    }

    private LocalDate getCellLocalDate(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (cell.getCellType() == CellType.STRING) {
            return parseDateFlexible(cell.getStringCellValue());
        }
        if (cell.getCellType() == CellType.FORMULA) {
            try { return cell.getLocalDateTimeCellValue().toLocalDate(); } catch (Exception ignored) {}
        }
        return null;
    }

    /* ================= Import Excel/CSV ================= */

    private void importExcelFile(File file) {
        List<Officer> officerList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean skipHeader = true;

            for (Row row : sheet) {
                if (skipHeader) { skipHeader = false; continue; }

                try {
                    int col = 0;
                    /* 0 */        String _idIgnore   = getCellString(row.getCell(col++)); // ID (bỏ)
                    /* 1 */        String fullName    = getCellString(row.getCell(col++));
                    /* 2 */        String identifier  = getCellString(row.getCell(col++));
                    /* 3 */        String levelName   = getCellString(row.getCell(col++));
                    /* 4 */        String unit        = getCellString(row.getCell(col++));
                    /* 5 */        int birthYear      = tryParseInt(getCellString(row.getCell(col++)));
                    /* 6 */        String homeTown    = getCellString(row.getCell(col++));
                    /* 7 */        String note        = getCellString(row.getCell(col++));
                    /* 8 */        LocalDate since    = getCellLocalDate(row.getCell(col++));
                    /* 9 */        LocalDate until    = getCellLocalDate(row.getCell(col++));
                    /* 10 */       col++; // Số tháng hưởng (bỏ)

                    Officer officer = new Officer(
                            fullName, identifier, birthYear, since, until,
                            levelName, unit, homeTown, note
                    );

                    // Lần 1 BĐ, Lần 1 KT, Lần 2 BĐ, Lần 2 KT, ...
                    Map<Integer, Pair<LocalDate, LocalDate>> rounds = new LinkedHashMap<>();
                    int round = 1;
                    for (; col + 1 < row.getLastCellNum(); col += 2) {
                        LocalDate s = getCellLocalDate(row.getCell(col));
                        LocalDate e = getCellLocalDate(row.getCell(col + 1));
                        if (s != null && e != null) rounds.put(round++, Pair.of(s, e));
                    }
                    officer.setStudyTimes(rounds);

                    officerList.add(officer);
                } catch (Exception ex) {
                    System.err.println("⚠ Dòng " + (row.getRowNum() + 1) + " lỗi: " + ex.getMessage());
                }
            }

            officeService.saveOfficerAll(officerList);
            Dialog.displaySuccessFully("✅ Đã lưu " + officerList.size() + " cán bộ");

        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("❌ Không thể đọc file Excel: " + e.getMessage());
        }
    }

    private void importCsvFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            List<Officer> officerList = new ArrayList<>();
            boolean skipHeader = true;

            while ((line = br.readLine()) != null) {
                if (skipHeader) { skipHeader = false; continue; }

                String[] f = line.split(",", -1); // giữ ô trống
                if (f.length < 10) continue;

                // 0:ID(bỏ), 1:Họ tên, 2:Mã định danh, 3:Trình độ, 4:Đơn vị, 5:Năm sinh,
                // 6:Quê quán, 7:Ghi chú, 8:Since, 9:Until, 10:Số tháng(bỏ), 11..: vòng học
                String fullName   = f[1].trim();
                String identifier = f[2].trim();
                String levelName  = f[3].trim();
                String unit       = f[4].trim();
                int    birthYear  = tryParseInt(f[5].trim());
                String homeTown   = f[6].trim();
                String note       = f[7].trim();
                LocalDate since   = parseDateFlexible(f[8].trim());
                LocalDate until   = parseDateFlexible(f[9].trim());

                Officer officer = new Officer(fullName, identifier, birthYear, since, until,
                        levelName, unit, homeTown, note);

                Map<Integer, Pair<LocalDate, LocalDate>> rounds = new LinkedHashMap<>();
                int round = 1;
                for (int i = 11; i + 1 < f.length; i += 2) {
                    LocalDate s = parseDateFlexible(f[i].trim());
                    LocalDate e = parseDateFlexible(f[i + 1].trim());
                    if (s != null && e != null) rounds.put(round++, Pair.of(s, e));
                }
                officer.setStudyTimes(rounds);

                officerList.add(officer);
            }

            officeService.saveOfficerAll(officerList);
            Dialog.displaySuccessFully("✅ Đã lưu " + officerList.size() + " cán bộ");

        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("❌ Không thể đọc file CSV: " + e.getMessage());
        }
    }

    @FXML
    private void onImport() {
        if (selectedFile == null) {
            Dialog.displayErrorMessage("Vui lòng chọn file trước khi import!");
            return;
        }
        String name = selectedFile.getName().toLowerCase();
        try {
            if (name.endsWith(".csv")) importCsvFile(selectedFile);
            else if (name.endsWith(".xlsx") || name.endsWith(".xls")) importExcelFile(selectedFile);
            else Dialog.displayErrorMessage("Định dạng file không hỗ trợ.");
            afterSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Lỗi khi import file: " + e.getMessage());
        }
    }

    @FXML
    private void onSave() {
        try {
            String fullName = fullNameField.getText();
            if (fullName == null || fullName.isBlank()) {
                Dialog.displayErrorMessage("Họ tên không được để trống.");
                return;
            }
            Level selectedLevel = levelComboBox.getValue();
            if (selectedLevel == null) {
                Dialog.displayErrorMessage("Vui lòng chọn cấp bậc.");
                return;
            }

            int birthYear = tryParseInt(birthYearField.getText());

            Officer officer = new Officer();
            officer.setFullName(fullName.trim());
            officer.setIdentifierCode(identifierField.getText() != null ? identifierField.getText().trim() : "");
            officer.setLevelId(selectedLevel.getId());
            officer.setLevelName(selectedLevel.getName());
            officer.setUnit(unitField.getText() != null ? unitField.getText().trim() : "");
            officer.setHomeTown(homeTownField.getText() != null ? homeTownField.getText().trim() : "");
            officer.setBirthYear(birthYear);
            officer.setNote(noteField.getText() != null ? noteField.getText().trim() : "");

            officeService.saveOfficer(officer);

            // clear form
            identifierField.clear();
            fullNameField.clear();
            unitField.clear();
            birthYearField.clear();
            homeTownField.clear();
            noteField.clear();
            levelComboBox.getSelectionModel().clearSelection();

            Dialog.displaySuccessFully("Lưu cán bộ thành công");
            afterSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Lưu cán bộ thất bại");
        }
    }
    private void afterSuccess() {
        try {
            if (onSuccess != null) onSuccess.run();   // báo về OfficerViewController để loadData()
        } finally {
            onClose();                                // đóng popup AddOfficer
        }
    }
}
