package com.qnp.pmp.controllers;


import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.dto.StudyRoundDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import com.qnp.pmp.excel.ExcelImportUtil;
import com.qnp.pmp.controllers.ImportPreviewController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.apache.poi.ss.usermodel.Cell;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OfficerViewController {

    @FXML
    private Label totalLabel;
    @FXML
    private TabPane officerTabPane;
    // === Bảng trên 60 tháng ===
    @FXML
    private TableView<OfficerViewDTO> officerTableAbove60;
    @FXML
    private TableColumn<OfficerViewDTO, String> fullNameColAbove;
    @FXML
    private TableColumn<OfficerViewDTO, String> identifierCodeColAbove;
    @FXML
    private TableColumn<OfficerViewDTO, Integer> birthYearColAbove;
    @FXML
    private TableColumn<OfficerViewDTO, Integer> allowanceColAbove;
    @FXML
    private TableColumn<OfficerViewDTO, String> unitColAbove;
    @FXML
    private TableColumn<OfficerViewDTO, Void> detailColAbove;
    @FXML
    private TableColumn<OfficerViewDTO, Void> workColAbove;

    // === Bảng ≤ 60 tháng ===
    @FXML
    private TableView<OfficerViewDTO> officerTableBelow60;
    @FXML
    private TableColumn<OfficerViewDTO, String> fullNameColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, String> identifierCodeColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, Integer> birthYearColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, Integer> allowanceColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, String> unitColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, Void> detailColBelow;
    @FXML
    private TableColumn<OfficerViewDTO, Void> workColBelow;

    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML
    public void initialize() {
        // Khởi tạo cấu hình cho 2 bảng
        setupTable(fullNameColAbove, identifierCodeColAbove, birthYearColAbove, allowanceColAbove, unitColAbove, detailColAbove, workColAbove);
        setupTable(fullNameColBelow, identifierCodeColBelow, birthYearColBelow, allowanceColBelow, unitColBelow, detailColBelow, workColBelow);
        officerTableBelow60.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        officerTableAbove60.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        attachRowClickHandler(officerTableAbove60);
        attachRowClickHandler(officerTableBelow60);
        // Nạp dữ liệu ban đầu
        loadData();
    }

    /**
     * Khởi tạo cấu hình cho bảng
     */
    private void setupTable(TableColumn<OfficerViewDTO, String> fullNameCol,
                            TableColumn<OfficerViewDTO, String> identifierCodeCol,
                            TableColumn<OfficerViewDTO, Integer> birthYearCol,
                            TableColumn<OfficerViewDTO, Integer> allowanceCol,
                            TableColumn<OfficerViewDTO, String> unitCol,
                            TableColumn<OfficerViewDTO, Void> detailCol,
                            TableColumn<OfficerViewDTO, Void> workCol) {

        fullNameCol.setCellValueFactory(c -> c.getValue().fullNameProperty());
        identifierCodeCol.setCellValueFactory(c -> c.getValue().identifierCodeProperty());
        birthYearCol.setCellValueFactory(c -> c.getValue().birthYearProperty().asObject());
        allowanceCol.setCellValueFactory(c -> c.getValue().allowanceMonthsProperty().asObject());
        unitCol.setCellValueFactory(c -> c.getValue().unitProperty());

        addButtonToColumn(detailCol, "Chi tiết", this::showOfficerDetail);
        addButtonToColumn(workCol, "Thời gian thôi hưởng", this::showOfficerWorkHistory);
    }

    /**
     * Load dữ liệu và chia vào 2 bảng
     */
    private void loadData() {
        List<OfficerViewDTO> allData = officeService.getOfficerAllowanceStatus();

        List<OfficerViewDTO> above60 = allData.stream()
                .filter(o -> o.getAllowanceMonths() > 60)
                .collect(Collectors.toList());

        List<OfficerViewDTO> belowOrEqual60 = allData.stream()
                .filter(o -> o.getAllowanceMonths() <= 60)
                .collect(Collectors.toList());

        officerTableAbove60.setItems(FXCollections.observableArrayList(above60));
        officerTableBelow60.setItems(FXCollections.observableArrayList(belowOrEqual60));

        totalLabel.setText("Tổng: " + allData.size() + " cán bộ");
    }

    private void attachRowClickHandler(TableView<OfficerViewDTO> table) {
        table.setRowFactory(tv -> {
            TableRow<OfficerViewDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    OfficerViewDTO item = row.getItem();
                    openEditPopup(item);
                }
            });
            return row;
        });
    }

    /**
     * Thêm nút vào cột thao tác
     */
    private void addButtonToColumn(TableColumn<OfficerViewDTO, Void> column,
                                   String buttonText,
                                   Consumer<OfficerViewDTO> action) {
        column.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button(buttonText);

            {
                btn.setOnAction(e -> {
                    OfficerViewDTO officer = getTableView().getItems().get(getIndex());
                    action.accept(officer);
                });
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(btn));
            }
        });
    }

    /**
     * Xử lý khi nhấn nút "Chi tiết"
     */
    private void showOfficerDetail(OfficerViewDTO officer) {

        openDetailPopup(officer.getId().getValue());
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

    private void openEditPopup(OfficerViewDTO officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/EditOfficer.fxml"));
            Parent root = loader.load();
            EditOfficerController controller = loader.getController();
            controller.setOfficer(officer);
            Stage stage = new Stage();
            stage.setTitle("Sửa cán bộ");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setResizable(false);
            stage.showAndWait();
            loadData();
        } catch (IOException e) {
            Dialog.displayErrorMessage("Không thể mở cửa sổ sửa cán bộ.");
        }
    }

    private void openDetailPopup(int officerId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/OfficerDetailPopup.fxml"));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xử lý khi nhấn nút "Công tác"
     */
    private void showOfficerWorkHistory(OfficerViewDTO officer) {

        // TODO: Mở popup hoặc scene mới
        showStudyTime(officer);
    }

    private void showStudyTime(OfficerViewDTO officer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/StudyTime/StudyTimeView.fxml"));
            Parent root = loader.load();

            StudyTimeController controller = loader.getController();
            controller.setOfficer(officer);

            Stage stage = new Stage();
            stage.setTitle("Thời gian học");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 1) Backup thủ công: tính max toàn cục và tạo 2 sheet với cùng cấu trúc cột
    @FXML
    private void onManualExcelBackup() {
        List<OfficerViewDTO> above60 = new ArrayList<>(officerTableAbove60.getItems());
        List<OfficerViewDTO> belowOrEqual60 = new ArrayList<>(officerTableBelow60.getItems());

        // Gom để tính max chung
        List<OfficerViewDTO> all = new ArrayList<>();
        all.addAll(above60);
        all.addAll(belowOrEqual60);

        int globalMaxStudyRounds = all.stream()
                .map(OfficerViewDTO::getStudyRounds).filter(Objects::nonNull)
                .mapToInt(m -> m.keySet().stream().mapToInt(Integer::intValue).max().orElse(0))
                .max().orElse(0);

        int globalMaxAllowanceRounds = all.stream()
                .map(OfficerViewDTO::getAllowances).filter(Objects::nonNull)
                .mapToInt(m -> m.keySet().stream().mapToInt(Integer::intValue).max().orElse(0))
                .max().orElse(0);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn nơi lưu file Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel file", "*.xlsx"));
        File file = fileChooser.showSaveDialog(officerTableAbove60.getScene().getWindow());
        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook()) {
            // Sanitize + đảm bảo không trùng tên
            String s1 = sanitizeSheetName("Trên 60 tháng");
            String s2 = sanitizeSheetName("Dưới hoặc bằng 60 tháng");
            if (s2.equalsIgnoreCase(s1)) s2 = s2 + " (2)";

            createSheet(workbook, s1, above60, globalMaxStudyRounds, globalMaxAllowanceRounds);
            createSheet(workbook, s2, belowOrEqual60, globalMaxStudyRounds, globalMaxAllowanceRounds);

            // Kiểm tra số sheet
            int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount < 2) {
                throw new IllegalStateException("Tạo chưa đủ sheet (hiện có " + sheetCount + ").");
            }

            try (OutputStream os = new FileOutputStream(file)) {
                workbook.write(os);
            }
            Dialog.displaySuccessFully("Xuất Excel thành công: " + file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Xuất Excel thất bại: " + e.getMessage());
        }
    }

    /** POI giới hạn tên sheet <=31 ký tự và không chứa: : \ / ? * [ ]  */
    private String sanitizeSheetName(String name) {
        String n = name.replace(':',' ')
                .replace('\\',' ')
                .replace('/',' ')
                .replace('?',' ')
                .replace('*',' ')
                .replace('[','(')
                .replace(']',')');
        if (n.length() > 31) n = n.substring(0, 31);
        if (n.isBlank()) n = "Sheet";
        return n.trim();
    }




    private void createSheet(Workbook wb,
                             String sheetName,
                             List<OfficerViewDTO> data,
                             int maxStudyRounds,
                             int maxAllowanceRounds) {
        Sheet sheet = wb.createSheet(sheetName);

        CreationHelper ch = wb.getCreationHelper();

        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont(); headerFont.setBold(true);
        headerStyle.setFont(headerFont); headerStyle.setWrapText(true);

        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(ch.createDataFormat().getFormat("dd/MM/yyyy"));

        int rowIdx = 0, col = 0;
        Row header = sheet.createRow(rowIdx++);
        setHeader(header, col++, "ID", headerStyle);
        setHeader(header, col++, "Họ tên", headerStyle);
        setHeader(header, col++, "Mã định danh", headerStyle);
        setHeader(header, col++, "Trình độ", headerStyle);
        setHeader(header, col++, "Đơn vị", headerStyle);
        setHeader(header, col++, "Năm sinh", headerStyle);
        setHeader(header, col++, "Quê quán", headerStyle);
        setHeader(header, col++, "Ghi chú", headerStyle);
        setHeader(header, col++, "Số tháng hưởng", headerStyle);

        for (int i = 1; i <= maxStudyRounds; i++) {
            setHeader(header, col++, "Lần " + i + " Bắt đầu (học)", headerStyle);
            setHeader(header, col++, "Lần " + i + " Kết thúc (học)", headerStyle);
        }
        for (int i = 1; i <= maxAllowanceRounds; i++) {
            setHeader(header, col++, "Được hưởng " + i + " - Từ ngày", headerStyle);
            setHeader(header, col++, "Được hưởng " + i + " - Đến ngày", headerStyle);
        }

        for (OfficerViewDTO o : data) {
            Row row = sheet.createRow(rowIdx++);
            int c = 0;

            setString(row, c++, safeStr(o.getId() != null ? o.getId().get() : null));
            setString(row, c++, safeStr(o.fullNameProperty() != null ? o.fullNameProperty().get() : null));
            setString(row, c++, safeStr(o.identifierCodeProperty() != null ? o.identifierCodeProperty().get() : null));
            setString(row, c++, safeStr(o.levelNameProperty() != null ? o.levelNameProperty().get() : null));
            setString(row, c++, safeStr(o.unitProperty() != null ? o.unitProperty().get() : null));

            Integer by = (o.birthYearProperty() != null ? o.birthYearProperty().get() : null);
            setNumericOrString(row, c++, by == null ? null : String.valueOf(by));

            setString(row, c++, safeStr(o.homeTownProperty() != null ? o.homeTownProperty().get() : null));
            setString(row, c++, safeStr(o.noteProperty() != null ? o.noteProperty().get() : null));

            row.createCell(c++).setCellValue(o.getAllowanceMonths());

            Map<Integer, StudyRoundDTO> rounds = o.getStudyRounds() == null ? Map.of() : o.getStudyRounds();
            for (int i = 1; i <= maxStudyRounds; i++) {
                StudyRoundDTO r = rounds.get(i);
                writeLocalDate(row, c++, r == null ? null : r.getStartDate(), dateStyle);
                writeLocalDate(row, c++, r == null ? null : r.getEndDate(),   dateStyle);
            }

            Map<Integer, com.qnp.pmp.dto.AllowanceDTO> als = o.getAllowances() == null ? Map.of() : o.getAllowances();
            for (int i = 1; i <= maxAllowanceRounds; i++) {
                com.qnp.pmp.dto.AllowanceDTO a = als.get(i);
                writeLocalDate(row, c++, a == null ? null : a.getStartDate(), dateStyle);
                writeLocalDate(row, c++, a == null ? null : a.getEndDate(),   dateStyle);
            }
        }

        sheet.createFreezePane(0, 1);
        for (int i = 0; i < Math.min(col, 120); i++) sheet.autoSizeColumn(i);
    }




    /* ---------------- helpers ---------------- */

    private void setHeader(Row row, int col, String text, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(text);
        cell.setCellStyle(style);
    }

    private void setString(Row row, int col, String text) {
        row.createCell(col).setCellValue(text == null ? "" : text);
    }

    private void setNumericOrString(Row row, int col, String maybeNumber) {
        Cell cell = row.createCell(col);
        if (maybeNumber != null && maybeNumber.matches("\\d+")) {
            cell.setCellValue(Integer.parseInt(maybeNumber));
        } else {
            cell.setCellValue(safeStr(maybeNumber));
        }
    }

    private void writeLocalDate(Row row, int col, LocalDate ld, CellStyle dateStyle) {
        Cell cell = row.createCell(col);
        if (ld != null) {
            // Excel expects a java.util.Date; use java.sql.Date for convenience
            cell.setCellValue(java.sql.Date.valueOf(ld));
            cell.setCellStyle(dateStyle);
        } else {
            cell.setBlank();
        }
    }

    private String safeStr(Object o) {
        return o == null ? "" : String.valueOf(o);
    }


    @FXML
    private void add() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/AddOfficerView.fxml"));
            Parent root = loader.load();
            AddOfficerController ctrl = loader.getController();            // <-- lấy controller
            ctrl.setOnSuccess(this::loadData);
            Stage stage = new Stage();
            stage.setTitle("Thêm cán bộ");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadData();
        } catch (IOException e) {
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm cán bộ.");
        }
    }

    @FXML
    private void onImport() {
        try {
            // 1) Chọn file
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Chọn file Excel để import");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx")
            );
            File file = chooser.showOpenDialog(officerTableAbove60.getScene().getWindow());
            if (file == null) return;

            // 2) Đọc dữ liệu từ Excel
            List<OfficerViewDTO> imported = ExcelImportUtil.readOfficers(file);
            if (imported == null || imported.isEmpty()) {
                Dialog.displayErrorMessage("Không tìm thấy dữ liệu hợp lệ trong file.");
                return;
            }

            // 3) Mở preview + callback khi người dùng xác nhận
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Import/ImportPreview.fxml"));
            Parent root = loader.load();
            ImportPreviewController ctrl = loader.getController();

            ctrl.setPreviewData(imported, approvedList -> {
                try {
                    // 4) Lưu vào DB qua service
                    officeService.importOfficers(approvedList);
                    Dialog.displaySuccessFully("Nhập dữ liệu thành công: " + approvedList.size() + " dòng.");
                    loadData(); // reload lại 2 bảng
                } catch (Exception ex) {
                    Dialog.displayErrorMessage("Lỗi lưu dữ liệu: " + ex.getMessage());
                }
            });

            // 5) Hiển thị cửa sổ preview
            Stage stage = new Stage();
            stage.setTitle("Xác nhận nhập dữ liệu");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Không thể import: " + e.getMessage());
        }
    }

    @FXML
    private void addSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/StudyTime/AddStudy.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm lịch công tác");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadData();
        } catch (IOException e) {
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm lịch công tác");
        }
    }

    @FXML
    private void onAllowanceTime() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/AllowanceTime/AddAllowanceTime.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm cấp bậc");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadData();
        } catch (IOException e) {
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm thời gian được hưởng.");
        }
    }

    @FXML
    private void addRank() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/Rank/AddRankView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm cấp bậc");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadData();
        } catch (IOException e) {
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm cấp bậc.");
        }
    }

    @FXML
    private void refreshTable() {
        loadData();
    }

    @FXML
    private void onDelete() {
        Tab selectedTab = officerTabPane.getSelectionModel().getSelectedItem();

        OfficerViewDTO selected = null;
        if ("Trên 60".equals(selectedTab.getText())) {
            selected = officerTableAbove60.getSelectionModel().getSelectedItem();
        } else if ("Dưới 60".equals(selectedTab.getText())) {
            selected = officerTableBelow60.getSelectionModel().getSelectedItem();
        }

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
            loadData(); // reload lại dữ liệu cho cả 2 bảng
        }
    }

    @FXML
    private void onSearch() {

    }
}
