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
    @FXML private TableColumn<OfficerViewDTO,Void>detailCol;
    @FXML private TableColumn<OfficerViewDTO, String> unitCol;
    @FXML private TableColumn<OfficerViewDTO, Integer> birthYearCol;
    @FXML private TableColumn<OfficerViewDTO, String> identifierCodeCol;
    @FXML
    private TableColumn<OfficerViewDTO, Integer> allowanceCol;
    @FXML
    private TableColumn<OfficerViewDTO,Void>workCol;
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

        unitCol.setCellValueFactory(data -> data.getValue().unitProperty());
        identifierCodeCol.setCellValueFactory(data->data.getValue().identifierCodeProperty());
        birthYearCol.setCellValueFactory(data -> data.getValue().birthYearProperty().asObject());
        allowanceCol.setCellValueFactory(cellData -> cellData.getValue().allowanceMonthsProperty().asObject());
        officerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Căn giữa dữ liệu cho tất cả cột
        centerAllColumns(fullNameCol ,unitCol, birthYearCol,identifierCodeCol,allowanceCol);

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
                        int months = item.getAllowanceMonths();
                        if (months >= 120) {
                            setStyle("-fx-background-color: #d8bfd8;"); // tím nhạt
                        } else if (months > 60) {
                            setStyle("-fx-background-color: #f8d7da;"); // đỏ nhạt
                        } else if (months >= 57) {
                            setStyle("-fx-background-color: #fff3cd;"); // vàng nhạt
                        } else {
                            setStyle(""); // không tô màu
                        }
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
        addDetailButtonToTable();
        addWorkButtonToTable();
    }
    private void addWorkButtonToTable() {
        workCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnWork = new Button("📋 Công tác");

            {
                btnWork.setStyle(
                        "-fx-background-color: linear-gradient(to right, #10b981, #059669);" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 20;" +
                                "-fx-padding: 4 12;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: 600;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-graphic-text-gap: 6;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 2, 0.3, 0, 1);"
                );

                btnWork.setOnMouseEntered(e -> btnWork.setStyle(
                        "-fx-background-color: linear-gradient(to right, #059669, #047857);" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 20;" +
                                "-fx-padding: 4 12;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: 600;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-graphic-text-gap: 6;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 3, 0.4, 0, 2);"
                ));

                btnWork.setOnMouseExited(e -> btnWork.setStyle(
                        "-fx-background-color: linear-gradient(to right, #10b981, #059669);" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 20;" +
                                "-fx-padding: 4 12;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: 600;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-graphic-text-gap: 6;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 2, 0.3, 0, 1);"
                ));

                btnWork.setOnAction(event -> {
                    OfficerViewDTO data = getTableView().getItems().get(getIndex());
                    if (data != null) {
                        openWorkHistoryPopup(data.getId().getValue());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnWork);
            }
        });
    }
    private void openWorkHistoryPopup(int id) {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/qnp/pmp/WorkTime/WorkHistoryPopup.fxml"));
            Parent root = loader.load();

            WorkHistoryController controller = loader.getController();
            controller.setOfficeId(id);

            Stage stage = new Stage();
            stage.setTitle("Lịch sử công tác");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addDetailButtonToTable(){
        detailCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnView = new Button("👁 Xem chi tiết");
            {
                btnView.setStyle(
                        "-fx-background-color: linear-gradient(to right, #3b82f6, #2563eb);" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 20;" +
                                "-fx-padding: 4 12;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: 600;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-graphic-text-gap: 6;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 2, 0.3, 0, 1);"
                );

                btnView.setOnMouseEntered(e -> btnView.setStyle(
                        "-fx-background-color: linear-gradient(to right, #2563eb, #1d4ed8);" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 20;" +
                                "-fx-padding: 4 12;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: 600;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-graphic-text-gap: 6;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 3, 0.4, 0, 2);"
                ));

                btnView.setOnMouseExited(e -> btnView.setStyle(
                        "-fx-background-color: linear-gradient(to right, #3b82f6, #2563eb);" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 20;" +
                                "-fx-padding: 4 12;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: 600;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-graphic-text-gap: 6;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 2, 0.3, 0, 1);"
                ));
                btnView.setOnAction(event -> {
                    OfficerViewDTO data = getTableView().getItems().get(getIndex());
                    if (data != null) {
                        openDetailPopup(data.getId().getValue());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnView);
                }
            }
        });
    }
    private void openDetailPopup(int officerId){
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/OfficerDetailPopup.fxml"));
            Parent root = loader.load();

            OfficerDetailViewController controller = loader.getController();
            controller.setOfficerId(officerId);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết cán bộ");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
        }
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
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
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

    private void enableWindowDragging(Stage stage, Parent root) {
        final double[] xOffset = {0};
        final double[] yOffset = {0};

        root.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }
    @FXML
    private void addRank(){
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/qnp/pmp/Rank/AddRankView.fxml"));
            Parent root=loader.load();
            Stage stage=new Stage();
            stage.setTitle("Thêm cấp bậc");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadOfficerAllowance();
        }catch (IOException e){
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm cấp bậc.");
        }
    }

    @FXML
    private void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/AddOfficerView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm cán bộ");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadOfficerAllowance();
        } catch (IOException e) {
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm cán bộ.");
        }
    }
    @FXML
    private void addSchedule(){
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/qnp/pmp/StudyTime/AddStudy.fxml"));
            Parent root=loader.load();
            Stage stage=new Stage();
            stage.setTitle("Thêm lịch công tác");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadOfficerAllowance();
        }catch (IOException e){
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm lịch công tác");
        }
    }
    @FXML
    private void onAllowanceTime(){
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/qnp/pmp/AllowanceTime/AddAllowanceTime.fxml"));
            Parent root=loader.load();
            Stage stage=new Stage();
            stage.setTitle("Thêm thời gian được hưởng");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }catch (IOException e){
            Dialog.displayErrorMessage("không thể mở  Thơi gian dc hưởng");
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

                // Cột công tác
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
                    row.createCell(col++).setCellValue(o.getAllowanceMonths());

                    // Ghi từng lần công tác
                    for (int j = 1; j <= maxStudyRounds; j++) {
                        if (o.getStudyRounds().containsKey(j)) {
                            var round = o.getStudyRounds().get(j);
                            row.createCell(col++).setCellValue(round.getStartDate().format(formatter));
                            row.createCell(col++).setCellValue(round.getEndDate().format(formatter));
                        } else {
                            row.createCell(col++).setCellValue("");
                            row.createCell(col++).setCellValue("");
                        }
                    }
                }

                // 4️⃣ Ghi file
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }

                Dialog.displaySuccessFully("✅ Xuất file Excel thành công!");
            } catch (IOException e) {
                e.printStackTrace();
                Dialog.displayErrorMessage("❌ Lỗi khi ghi file Excel.");
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
                    String identifierCode = matcher.group("identifierCode");
                    OfficerViewDTO dto = new OfficerViewDTO(
                            0, name, 0, level, unit, birthYear, homeTown, note, identifierCode
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
