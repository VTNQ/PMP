package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.service.ExcelBackupService;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.ExcelBackupServiceImpl;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javax.imageio.ImageIO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;

public class OfficerViewController {

    @FXML private TableView<OfficerViewDTO> officerTable;
    @FXML private TableColumn<OfficerViewDTO, String> fullNameCol;
    @FXML private TableColumn<OfficerViewDTO, String> positionCol;
    @FXML private TableColumn<OfficerViewDTO, String> unitCol;
    @FXML private TableColumn<OfficerViewDTO, Integer> birthYearCol;
    @FXML private TableColumn<OfficerViewDTO, String> noteCol;
    @FXML private TableColumn<OfficerViewDTO, String> homeTownCol;
    @FXML private TableColumn<OfficerViewDTO, Integer> totalAllowance;
    @FXML private TableColumn<OfficerViewDTO, Void> studyTimeButtonCol;
    @FXML
    private TableColumn<OfficerViewDTO, LocalDate> sinceCol;

    @FXML
    private TextField searchField;

    @FXML private Label totalLabel;

    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML
    public void initialize() {

        ExcelBackupService autoBackup = new ExcelBackupServiceImpl(officeService);
        autoBackup.startAutoBackup();


        // Gán dữ liệu cho các cột
        fullNameCol.setCellValueFactory(data -> data.getValue().fullNameProperty());
        positionCol.setCellValueFactory(data -> data.getValue().levelNameProperty());
        unitCol.setCellValueFactory(data -> data.getValue().unitProperty());
        homeTownCol.setCellValueFactory(data -> data.getValue().homeTownProperty());
        birthYearCol.setCellValueFactory(data -> data.getValue().birthYearProperty().asObject());
        noteCol.setCellValueFactory(data -> data.getValue().noteProperty());
        totalAllowance.setCellValueFactory(data->data.getValue().allowanceMonthsProperty().asObject());
        sinceCol.setCellValueFactory(cellData -> cellData.getValue().sinceProperty());
        officerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Căn giữa dữ liệu cho tất cả cột
        centerAllColumns(fullNameCol, positionCol, unitCol, birthYearCol, homeTownCol,noteCol);

        // Tải dữ liệu ban đầu
        loadOfficerAllowance();
        addStudyTimeButtonToTable();
        fullNameCol.getStyleClass().add("col-yellow");
        officerTable.setRowFactory(tv -> {
            TableRow<OfficerViewDTO> row = new TableRow<>() {
                @Override
                protected void updateItem(OfficerViewDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else {
//                        long months = item.getThoiGianHuongThuHut(); // tính số tháng từ since đến hiện tại
//                        if (months >= 57 && months < 60) {
//                            setStyle("-fx-background-color: yellow;");
//                        } else {
//                            setStyle(""); // không bôi nếu không khớp
//                        }
                    }
                }
            };

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    OfficerViewDTO officerViewDTO = row.getItem();
                    showEditDialog(officerViewDTO);
                    loadOfficerAllowance();
                }
            });

            return row;
        });
    }


    private void showEditDialog(OfficerViewDTO officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/EditOfficer.fxml"));
            Parent root = loader.load();

            EditOfficerController controller = loader.getController();
            controller.setOfficer(officer);

            Stage stage = new Stage();
            stage.setTitle("Chỉnh sửa cán bộ");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> void centerCell(TableColumn<OfficerViewDTO, T> column) {
        column.setCellFactory(new Callback<TableColumn<OfficerViewDTO, T>, TableCell<OfficerViewDTO, T>>() {
            @Override
            public TableCell<OfficerViewDTO, T> call(TableColumn<OfficerViewDTO, T> param) {
                return new TableCell<OfficerViewDTO, T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.toString());
                        setStyle("-fx-alignment: CENTER;");
                    }
                };
            }
        });
    }

    @SafeVarargs
    private final void centerAllColumns(TableColumn<OfficerViewDTO, ?>... columns) {
        for (TableColumn<OfficerViewDTO, ?> col : columns) {
            centerCell(col);
        }
    }

    private void addStudyTimeButtonToTable() {
        Callback<TableColumn<OfficerViewDTO, Void>, TableCell<OfficerViewDTO, Void>> cellFactory = new Callback<TableColumn<OfficerViewDTO, Void>, TableCell<OfficerViewDTO, Void>>() {
            @Override
            public TableCell<OfficerViewDTO, Void> call(final TableColumn<OfficerViewDTO, Void> param) {
                return new TableCell<OfficerViewDTO, Void>() {
                    private final Button btn = new Button("⏱ Xem");

                    {
                        btn.setOnAction(event -> {
                            OfficerViewDTO officer = getTableView().getItems().get(getIndex());
                            showStudyTime(officer);
                        });
                        btn.setStyle(
                                "-fx-background-color: #007bff;" +   // màu nền xanh
                                        "-fx-text-fill: white;" +           // chữ trắng
                                        "-fx-font-weight: bold;" +          // chữ đậm
                                        "-fx-background-radius: 8;" +       // bo góc
                                        "-fx-cursor: hand;"                 // hiển thị con trỏ bàn tay khi hover
                        );

                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        };

        studyTimeButtonCol.setCellFactory(cellFactory);
    }

    private void showStudyTime(OfficerViewDTO officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/StudyTime/StudyTimeView.fxml"));
            Parent root = loader.load();

            StudyTimeController controller = loader.getController();
            controller.setOfficer(officer);

            Stage stage = new Stage();
            stage.setTitle("Thời gian học");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadOfficerAllowance() {
        List<OfficerViewDTO> data = officeService.getOfficerAllowanceStatus();
        ObservableList<OfficerViewDTO> observableData = FXCollections.observableArrayList(data);
        officerTable.setItems(observableData);
        totalLabel.setText("Tổng: " + observableData.size() + " cán bộ");
    }

    @FXML
    private void onSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                ObservableList<OfficerViewDTO> result = FXCollections.observableArrayList(officeService.findByName(keyword));
                officerTable.setItems(result);
                totalLabel.setText("Tổng: " + result.size() + " cán bộ");
            } else {
                loadOfficerAllowance();
            }
        }
    }

    @FXML
    private void refreshTable() {
        searchField.clear();
        loadOfficerAllowance();
    }



    @FXML
    private void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/AddOfficerView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm cán bộ");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadOfficerAllowance();
        } catch (IOException e) {
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm cán bộ.");
        }
    }

    @FXML
    private void onDelete() {
        OfficerViewDTO selected = officerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialog.displayErrorMessage("Vui lòng chọn một cán bộ để xoá.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xoá");
        alert.setHeaderText("Xoá cán bộ: ");
        alert.setContentText("Bạn có chắc chắn muốn xoá?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            officeService.deleteOfficer(selected.getId().get());
            Dialog.displaySuccessFully("Đã xoá thành công.");
            loadOfficerAllowance();
        }
    }
    @FXML
    private void onImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Officer");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV file", "*.csv"),
                new FileChooser.ExtensionFilter("Excel file", "*.xlsx")
        );

        File file = fileChooser.showOpenDialog(officerTable.getScene().getWindow());
        if (file != null) {
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".csv")) {
                importCsvFile(file);
            } else if (fileName.endsWith(".xlsx")) {
                importExcelFile(file);
            } else {
                Dialog.displayErrorMessage("Định dạng file không hỗ trợ.");
            }
        }
        loadOfficerAllowance();

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
                    int birthYear = Integer.parseInt(fields[1].trim());
                    LocalDate since = parseDate(fields[2].trim());
                    String levelName = fields[3].trim();
                    String unit = fields[4].trim();
                    String homeTown = fields[5].trim();
                    String note = fields[6].trim();

                    Officer officer = new Officer(fullName, birthYear, since, levelName, unit, homeTown, note);

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
    private void showAddStudyTime(){
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/qnp/pmp/StudyTime/AddStudy.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Study Time");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
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
                    String fullName = getCellString(row.getCell(0));              // Cột A
                    int birthYear = Integer.parseInt(getCellString(row.getCell(1))); // Cột B
                    LocalDate since = getCellLocalDate(row.getCell(2));           // Cột C
                    String level = getCellString(row.getCell(3));                 // Cột D
                    String unit = getCellString(row.getCell(4));                  // Cột E
                    String homeTown = getCellString(row.getCell(5));              // Cột F
                    String note = getCellString(row.getCell(6));                  // Cột G
                    // Ghi chú

                    Officer officer = new Officer(
                            fullName,   // tên
                            birthYear,  // năm sinh
                            since,      // ngày bắt đầu hưởng
                            level,      // trình độ
                            unit,       // đơn vị
                            homeTown,   // quê quán
                            note        // ghi chú
                    );

                    Map<Integer, Pair<LocalDate, LocalDate>> studyTimes = new LinkedHashMap<>();
                    int roundIndex = 1;

                    for (int i = 8; i + 1 < row.getLastCellNum(); i += 2) {
                        LocalDate startDate = getCellLocalDateString(row.getCell(i));
                        LocalDate endDate = getCellLocalDateString(row.getCell(i + 1));

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
            loadOfficerAllowance();

        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("❌ Không thể đọc file Excel");
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

    @FXML
    private void onAddStudyTime(){
        showAddStudyTime();

    }
    @FXML
    private void onManualExcelBackup(javafx.event.ActionEvent event) {
        List<OfficerViewDTO> officers = officeService.getOfficerAllowanceStatus();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn nơi lưu file Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(officerTable.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Officers");

                // Tiêu đề cột
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("ID");
                header.createCell(1).setCellValue("Họ tên");
                header.createCell(2).setCellValue("Trình độ");
                header.createCell(3).setCellValue("Đơn vị");
                header.createCell(4).setCellValue("Năm sinh");
                header.createCell(5).setCellValue("Quê quán");
                header.createCell(6).setCellValue("Ghi chú");
                header.createCell(7).setCellValue("Số tháng hưởng");

                // Dữ liệu
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

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }

                Dialog.displaySuccessFully("Xuất file Excel thành công!");

            } catch (IOException e) {
                e.printStackTrace();
                Dialog.displayErrorMessage("Lỗi khi ghi file Excel.");
            }
        }
    }

    private void showPreviewDialog(List<OfficerViewDTO> extractedData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/ImportPreview.fxml"));
            Parent root = loader.load();

            ImportPreviewController controller = loader.getController();
            controller.setPreviewData(extractedData, confirmedList -> {
                // gọi officeService.saveOfficerAll(...) sau xác nhận
                officeService.saveOfficerAll(
                        confirmedList.stream().map(dto -> dto.toEntity()).collect(Collectors.toList())
                );
                Dialog.displaySuccessFully("Đã nhập " + confirmedList.size() + " cán bộ");
                loadOfficerAllowance();
            });

            Stage stage = new Stage();
            stage.setTitle("Xác nhận dữ liệu nhập từ ảnh");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            Dialog.displayErrorMessage("Lỗi khi hiển thị bảng xem trước.");
            e.printStackTrace();
        }
    }
    @FXML
    private void onImportFromImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh chứa bảng dữ liệu");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(officerTable.getScene().getWindow());
        if (file != null) {
            // Gọi hàm OCR tách dữ liệu từ ảnh (ví dụ: extractOfficerFromImage)
            List<OfficerViewDTO> extractedData = extractOfficerFromImage(file);

            if (extractedData != null && !extractedData.isEmpty()) {
                showPreviewDialog(extractedData);
            } else {
                Dialog.displayErrorMessage("Không nhận diện được dữ liệu từ ảnh.");
            }
        }
    }

    private List<OfficerViewDTO> extractOfficerFromImage(File file) {
        List<OfficerViewDTO> officerList = new ArrayList<>();
        try {
            ITesseract instance = new Tesseract();
            String tessPath = new File(getClass().getClassLoader().getResource("tessdata").getFile()).getAbsolutePath();
            System.out.println("TESSDATA PATH = " + tessPath);
            instance.setDatapath(tessPath);
            instance.setLanguage("vie");

            BufferedImage original = ImageIO.read(file);
            if (original == null) {
                Dialog.displayErrorMessage("Không thể đọc ảnh: định dạng ảnh không hợp lệ.");
                return new ArrayList<>();
            }

            BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = gray.createGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();

            BufferedImage binarized = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g2 = binarized.createGraphics();
            g2.drawImage(gray, 0, 0, null);
            g2.dispose();

            String result = instance.doOCR(binarized);
            System.out.println("OCR OUTPUT:\n" + result);
            String[] lines = result.split("\\r?\\n");

            // Gom dòng theo cụm (mỗi cán bộ là một group)
            List<String> groupLines = new ArrayList<>();
            StringBuilder currentGroup = new StringBuilder();
            for (String line : lines) {
                if (line.matches(".*\\b(19|20)\\d{2}\\b.*")) {
                    if (currentGroup.length() > 0) {
                        groupLines.add(currentGroup.toString());
                        currentGroup = new StringBuilder();
                    }
                }
                currentGroup.append(line.trim()).append(" ");
            }
            if (currentGroup.length() > 0) {
                groupLines.add(currentGroup.toString());
            }

            // Regex để phân tích cụm
            String provinceRegex = "(?i)(Hà Nội|TP\\. HCM|Hồ Chí Minh|Bắc Quang|Quảng Ninh|Lào Cai|Yên Bái|Nam Định|Nghệ An|Thanh Hóa|Đà Nẵng|Bình Dương|Bình Định|Thừa Thiên Huế|Hải Phòng|Vĩnh Phúc|Hà Giang|Điện Biên|Sơn La|Phú Thọ|Lạng Sơn|Cao Bằng|Bắc Kạn|Thái Nguyên|Bắc Giang|Hòa Bình|Tuyên Quang|Lâm Đồng|Kon Tum|Gia Lai|Đắk Lắk|Đắk Nông|Cần Thơ|Hậu Giang|Sóc Trăng|Trà Vinh|Vĩnh Long|Long An|An Giang|Tiền Giang|Bến Tre|Bạc Liêu|Cà Mau|Kiên Giang|Tây Ninh|Bình Phước|Ninh Thuận|Bình Thuận|Quảng Ngãi|Quảng Nam|Quảng Bình|Quảng Trị|Hà Tĩnh|Hưng Yên|Hải Dương|Thái Bình|Hà Nam|Ninh Bình|Tuyên Quang)";
            Pattern pattern = Pattern.compile("(?<name>[A-ZÀ-Ỹa-zà-ỹ\\s\\.]+)\\s+(?<birthYear>19\\d{2}|20\\d{2})\\s+(?<level>\\S+(?:\\s+\\S+)*)\\s+(?<unit>CAX\\s+[^\\d]+)\\s+(?<province>" + provinceRegex + ")\\s+(?<note>.+)");

            for (String group : groupLines) {
                Matcher matcher = pattern.matcher(group);
                if (matcher.find()) {
                    String name = matcher.group("name").trim();
                    int birthYear = Integer.parseInt(matcher.group("birthYear"));
                    String level = matcher.group("level").trim();
                    String unit = matcher.group("unit").trim();
                    String homeTown = matcher.group("province").trim();
                    String note = matcher.group("note").trim();

                    OfficerViewDTO dto = new OfficerViewDTO(
                            0, name, 0, level, unit, birthYear, homeTown, note,
                            LocalDate.of(2025, 4, 1)
                    );
                    officerList.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Lỗi khi xử lý ảnh OCR.");
        }
        return officerList;
    }

    private int tryParseInt(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (Exception e) {
            return 0;
        }
    }



}
