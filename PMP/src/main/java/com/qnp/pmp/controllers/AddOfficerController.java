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
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddOfficerController {
    private final OfficeService officeService;
    private final LevelService levelService;
    @FXML
    private Button closeButton;
    public AddOfficerController() {
        this.officeService = new OfficerServiceImpl();
        this.levelService = new LevelServiceImpl();
    }

    @FXML
    private TextField identifierField;
    @FXML
    private TextField fullNameField;

    @FXML
    private ComboBox<Level> levelComboBox;
    @FXML
    private TextField unitField;
    @FXML
    private TextField birthYearField;
    @FXML
    private TextArea noteField;
    @FXML
    private TextField homeTownField;
    @FXML
    private Label fileLabel; // Label hiển thị tên file
    private File selectedFile;

    @FXML
    public void initialize() {
        List<Level>levelList=levelService.getAll();
        levelComboBox.setItems(FXCollections.observableList(levelList));
        levelComboBox.setConverter(new StringConverter<Level>() {
            @Override
            public String toString(Level level) {
                return level != null ? level.getName() : "";
            }

            @Override
            public Level fromString(String s) {
                return levelComboBox.getItems().stream()
                        .filter(level -> level.getName().equals(s))
                        .findFirst()
                        .orElse(null);

            }
        });
    }
    @FXML
    private void onClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void onChooseFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Officer");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV file", "*.csv"),
                new FileChooser.ExtensionFilter("Excel file", "*.xlsx")
        );

        // Mở dialog chọn file
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
        } else {
            fileLabel.setText("Chưa chọn file");
        }
    }
    private int tryParseInt(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (Exception e) {
            return 0;
        }
    }
    private LocalDate getCellLocalDateString(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            CellType cellType = cell.getCellType();

            // Trường hợp ô là ngày dạng số
            if (cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            // Trường hợp ô là chuỗi (String) - ví dụ "12/07/2025"
            else if (cellType == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();

                if (dateStr.isEmpty()) {
                    return null;
                }

                // Cố gắng parse định dạng dd/MM/yyyy
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dateStr, formatter);
            }

            // Có thể xử lý thêm trường hợp công thức (formula) nếu cần
            else if (cellType == CellType.FORMULA) {
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue evaluated = evaluator.evaluate(cell);

                if (evaluated.getCellType() == CellType.NUMERIC) {
                    return LocalDate.ofInstant(
                            DateUtil.getJavaDate(evaluated.getNumberValue()).toInstant(),
                            ZoneId.systemDefault()
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️ Lỗi khi đọc ngày từ ô: " + e.getMessage());
        }

        return null;
    }

    private void importExcelFile(File file) {
        List<Officer> officerList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean skipHeader = true;

            for (Row row : sheet) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                try {
                    int col = 0;
                    int id = tryParseInt(getCellString(row.getCell(col++)));              // ID (không dùng)
                    String fullName = getCellString(row.getCell(col++));                  // Họ tên
                    String identifierCode = getCellString(row.getCell(col++));            // Mã định danh (⚠️ String)
                    String level = getCellString(row.getCell(col++));                     // Trình độ
                    String unit = getCellString(row.getCell(col++));                      // Đơn vị
                    int birthYear = tryParseInt(getCellString(row.getCell(col++)));       // Năm sinh
                    String homeTown = getCellString(row.getCell(col++));                  // Quê quán
                    String note = getCellString(row.getCell(col++));                      // Ghi chú
                    LocalDate since = getCellLocalDateString(row.getCell(col++));         // Ngày bắt đầu hưởng
                    LocalDate util = getCellLocalDateString(row.getCell(col++));          // Ngày kết thúc hưởng
                    col++; // Bỏ qua cột "Số tháng hưởng" vì không cần nhập

                    Officer officer = new Officer(
                            fullName,
                            identifierCode,
                            birthYear,
                            since,
                            util,
                            level,
                            unit,
                            homeTown,
                            note
                    );

                    // Xử lý các lần công tác
                    Map<Integer, Pair<LocalDate, LocalDate>> studyTimes = new LinkedHashMap<>();
                    int roundIndex = 1;

                    for (; col + 1 < row.getLastCellNum(); col += 2) {
                        LocalDate startDate = getCellLocalDateString(row.getCell(col));
                        LocalDate endDate = getCellLocalDateString(row.getCell(col + 1));

                        if (startDate != null && endDate != null) {
                            studyTimes.put(roundIndex++, Pair.of(startDate, endDate));
                        }
                    }

                    officer.setStudyTimes(studyTimes);
                    officerList.add(officer);

                } catch (Exception e) {
                    System.err.println("⚠️ Lỗi tại dòng " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            officeService.saveOfficerAll(officerList);
            Dialog.displaySuccessFully("✅ Đã lưu " + officerList.size() + " cán bộ");


        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("❌ Không thể đọc file Excel");
        }
    }

    private void importCsvFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            List<Officer> officerList = new ArrayList<>();
            boolean skipHeader = true;

            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                String[] fields = line.split(",", -1); // giữ ô trống

                if (fields.length >= 7) {
                    String fullName = fields[0].trim();
                    String identifierCode = fields[1].trim();
                    int birthYear = Integer.parseInt(fields[1].trim());
                    LocalDate since = parseDate(fields[2].trim());
                    LocalDate util = parseDate(fields[3].trim());
                    String levelName = fields[4].trim();
                    String unit = fields[5].trim();
                    String homeTown = fields[6].trim();
                    String note = fields[7].trim();

                    Officer officer = new Officer(fullName,identifierCode, birthYear, since,util, levelName, unit, homeTown, note);

                    // Bắt đầu từ cột 7 → Lần 1 BĐ, Lần 1 KT, ...
                    Map<Integer, Pair<LocalDate, LocalDate>> studyTimes = new LinkedHashMap<>();
                    int roundIndex = 1;
                    for (int i = 8; i + 1 < fields.length; i += 2) {
                        LocalDate start = parseDate(fields[i].trim());
                        LocalDate end = parseDate(fields[i + 1].trim());

                        if (start != null && end != null) {
                            studyTimes.put(roundIndex++, Pair.of(start, end));
                        }
                    }

                    officer.setStudyTimes(studyTimes);
                    officerList.add(officer);
                }
            }

            officeService.saveOfficerAll(officerList);
            Dialog.displaySuccessFully("✅ Đã lưu " + officerList.size() + " cán bộ");
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("❌ Không thể đọc file CSV");
        }
    }
    private LocalDate parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.isBlank()) return null;
            return LocalDate.parse(dateStr); // mặc định theo định dạng ISO: yyyy-MM-dd
        } catch (Exception e) {
            System.err.println("⚠️ Không thể parse ngày: " + dateStr);
            return null;
        }
    }

    private String getCellString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
    private LocalDate getCellLocalDate(Cell cell) {
        if (cell == null ) {
            return null; // hoặc LocalDate.now() tùy ý bạn
        }

        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        return null;
    }
    @FXML
    private void onImport(){
        if (selectedFile == null) {
            Dialog.displayErrorMessage("Vui lòng chọn file trước khi import!");
            return;
        }

        String fileName = selectedFile.getName().toLowerCase();
        try {
            if (fileName.endsWith(".csv")) {
                importCsvFile(selectedFile);
            } else if (fileName.endsWith(".xlsx")) {
                importExcelFile(selectedFile);
            } else {
                Dialog.displayErrorMessage("Định dạng file không hỗ trợ.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Lỗi khi import file!");
        }
    }
    @FXML
    private void onSave() {
        try {

            Officer officer = new Officer();
            officer.setUnit(unitField.getText());
            officer.setFullName(fullNameField.getText());
            officer.setLevelId(levelComboBox.getValue().getId());
            officer.setHomeTown(homeTownField.getText());
            officer.setBirthYear(Integer.valueOf(birthYearField.getText()));
            officer.setIdentifierCode(identifierField.getText());
            officer.setNote(noteField.getText());
            officeService.saveOfficer(officer);
            fullNameField.clear();
            unitField.clear();
            birthYearField.clear();
            homeTownField.clear();
            levelComboBox.getSelectionModel().clearSelection();
            noteField.clear();

            Dialog.displaySuccessFully("Luu cán bộ thành công");
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Luu cán bộ thất bại");
        }
    }

}
