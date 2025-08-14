package com.qnp.pmp.controllers;

import com.google.protobuf.compiler.PluginProtos;
import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.dto.StudyRoundDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

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

    @FXML private Label totalLabel;
    @FXML
    private TabPane officerTabPane;
    // === Bảng trên 60 tháng ===
    @FXML private TableView<OfficerViewDTO> officerTableAbove60;
    @FXML private TableColumn<OfficerViewDTO, String> fullNameColAbove;
    @FXML private TableColumn<OfficerViewDTO, String> identifierCodeColAbove;
    @FXML private TableColumn<OfficerViewDTO, Integer> birthYearColAbove;
    @FXML private TableColumn<OfficerViewDTO, Integer> allowanceColAbove;
    @FXML private TableColumn<OfficerViewDTO, String> unitColAbove;
    @FXML private TableColumn<OfficerViewDTO, Void> detailColAbove;
    @FXML private TableColumn<OfficerViewDTO, Void> workColAbove;

    // === Bảng ≤ 60 tháng ===
    @FXML private TableView<OfficerViewDTO> officerTableBelow60;
    @FXML private TableColumn<OfficerViewDTO, String> fullNameColBelow;
    @FXML private TableColumn<OfficerViewDTO, String> identifierCodeColBelow;
    @FXML private TableColumn<OfficerViewDTO, Integer> birthYearColBelow;
    @FXML private TableColumn<OfficerViewDTO, Integer> allowanceColBelow;
    @FXML private TableColumn<OfficerViewDTO, String> unitColBelow;
    @FXML private TableColumn<OfficerViewDTO, Void> detailColBelow;
    @FXML private TableColumn<OfficerViewDTO, Void> workColBelow;

    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML
    public void initialize() {
        // Khởi tạo cấu hình cho 2 bảng
        setupTable(fullNameColAbove, identifierCodeColAbove, birthYearColAbove, allowanceColAbove, unitColAbove, detailColAbove, workColAbove);
        setupTable(fullNameColBelow, identifierCodeColBelow, birthYearColBelow, allowanceColBelow, unitColBelow, detailColBelow, workColBelow);

        // Nạp dữ liệu ban đầu
        loadData();
    }

    /** Khởi tạo cấu hình cho bảng */
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
        addButtonToColumn(workCol, "Công tác", this::showOfficerWorkHistory);
    }

    /** Load dữ liệu và chia vào 2 bảng */
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

    /** Thêm nút vào cột thao tác */
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

    /** Xử lý khi nhấn nút "Chi tiết" */
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
    /** Xử lý khi nhấn nút "Công tác" */
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

    @FXML
    private void onManualExcelBackup(){
        List<OfficerViewDTO>above60=new ArrayList<>(officerTableAbove60.getItems());
        List<OfficerViewDTO>below60=new ArrayList<>(officerTableBelow60.getItems());
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Chọn nơi lưu file Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel file", "*.xlsx"));
        File file = fileChooser.showSaveDialog(officerTableAbove60.getScene().getWindow());
        if (file == null) return;
        try (Workbook workbook = new XSSFWorkbook()) {
            createSheet(workbook, "Trên 60",  above60);
            createSheet(workbook, "Dưới 60", below60);
            try (OutputStream os = new FileOutputStream(file)) {
                workbook.write(os);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Lỗi khi xuất Excel: " + e.getMessage());
        }
    }
    private void createSheet(Workbook wb, String sheetName, List<OfficerViewDTO> data) {
        Sheet sheet = wb.createSheet(sheetName);

        // Max study round index (keys may be sparse)
        int maxStudyRounds = data.stream()
                .map(OfficerViewDTO::getStudyRounds)
                .filter(Objects::nonNull)
                .mapToInt(m -> m.keySet().stream().mapToInt(Integer::intValue).max().orElse(0))
                .max()
                .orElse(0);

        // Styles
        CreationHelper ch = wb.getCreationHelper();

        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setWrapText(true);

        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(ch.createDataFormat().getFormat("dd/MM/yyyy"));

        // Header
        int rowIdx = 0;
        Row header = sheet.createRow(rowIdx++);
        int col = 0;
        setHeader(header, col++, "ID", headerStyle);
        setHeader(header, col++, "Họ tên", headerStyle);
        setHeader(header, col++, "Mã định danh", headerStyle);
        setHeader(header, col++, "Trình độ", headerStyle);
        setHeader(header, col++, "Đơn vị", headerStyle);
        setHeader(header, col++, "Năm sinh", headerStyle);
        setHeader(header, col++, "Quê quán", headerStyle);
        setHeader(header, col++, "Ghi chú", headerStyle);
        setHeader(header, col++, "Ngày bắt đầu hưởng", headerStyle);
        setHeader(header, col++, "Ngày kết thúc hưởng", headerStyle);
        setHeader(header, col++, "Số tháng hưởng", headerStyle);

        for (int i = 1; i <= maxStudyRounds; i++) {
            setHeader(header, col++, "Lần " + i + " Bắt đầu", headerStyle);
            setHeader(header, col++, "Lần " + i + " Kết thúc", headerStyle);
        }

        // Data
        for (OfficerViewDTO o : data) {
            Row row = sheet.createRow(rowIdx++);
            col = 0;

            setString(row, col++, safeStr(o.getId() != null ? o.getId().get() : null));
            setString(row, col++, safeStr(o.fullNameProperty() != null ? o.fullNameProperty().get() : null));
            setString(row, col++, safeStr(o.identifierCodeProperty() != null ? o.identifierCodeProperty().get() : null));
            setString(row, col++, safeStr(o.levelNameProperty() != null ? o.levelNameProperty().get() : null));
            setString(row, col++, safeStr(o.unitProperty() != null ? o.unitProperty().get() : null));

            // Năm sinh: write as number if possible
            Integer birthYearInt = (o.birthYearProperty() != null ? o.birthYearProperty().get() : null);
            String birthYear = (birthYearInt != null ? String.valueOf(birthYearInt) : null);
            setNumericOrString(row, col++, birthYear);
            setNumericOrString(row, col++, birthYear);

            setString(row, col++, safeStr(o.homeTownProperty() != null ? o.homeTownProperty().get() : null));
            setString(row, col++, safeStr(o.noteProperty() != null ? o.noteProperty().get() : null));

            // Ngày bắt đầu/ kết thúc hưởng (as real dates if present)

            // Số tháng hưởng
            row.createCell(col++).setCellValue(o.getAllowanceMonths());

            // Study rounds (dates)
            // rounds không null
            Map<Integer, StudyRoundDTO> rounds =
                    o.getStudyRounds() != null ? o.getStudyRounds() : Collections.emptyMap();

            for (int j = 1; j <= maxStudyRounds; j++) {
                StudyRoundDTO r = rounds.get(j);

                // writeLocalDate đã tự set blank nếu giá trị null
                java.time.LocalDate start = (r != null) ? r.getStartDate() : null;
                java.time.LocalDate end   = (r != null) ? r.getEndDate()   : null;

                writeLocalDate(row, col++, start, dateStyle);
                writeLocalDate(row, col++, end,   dateStyle);
            }

        }

        // Optional niceties
        sheet.createFreezePane(0, 1); // freeze header
        for (int c = 0; c < col; c++) sheet.autoSizeColumn(c);
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

    private String safeStr(Object o) { return o == null ? "" : String.valueOf(o); }


    @FXML
    private void add(){
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
            loadData();
        } catch (IOException e) {
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm cán bộ.");
        }
    }
    @FXML
    private void onImport(){

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
            loadData();
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
            stage.setTitle("Thêm cấp bậc");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            enableWindowDragging(stage, root);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadData();
        }catch (IOException e){
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm thời gian được hưởng.");
        }
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
            loadData();
        }catch (IOException e){
            Dialog.displayErrorMessage("Không thể mở cửa sổ thêm cấp bậc.");
        }
    }
    @FXML
    private void refreshTable(){

    }
    @FXML
    private void onDelete(){
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
    private void onSearch(){

    }
}
