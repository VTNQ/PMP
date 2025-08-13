package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OfficerViewController {

    @FXML private Label totalLabel;
    @FXML
    private TabPane tabPane;
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
    private void onManualExcelBackup() {
        List<OfficerViewDTO> officers = officeService.getOfficerAllowanceStatus();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn nơi lưu file Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        // Mở dialog lưu file
        File file = fileChooser.showSaveDialog(officerTableAbove60.getScene().getWindow());
        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Officers");

            // Định dạng ngày
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // 1️⃣ Tìm số lần công tác lớn nhất
            int maxStudyRounds = officers.stream()
                    .mapToInt(o -> o.getStudyRounds().size())
                    .max()
                    .orElse(0);

            // 2️⃣ Tạo header
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
            header.createCell(col++).setCellValue("Số tháng hưởng");

            // Header các lần công tác
            for (int i = 1; i <= maxStudyRounds; i++) {
                header.createCell(col++).setCellValue("Lần " + i + " Bắt đầu");
                header.createCell(col++).setCellValue("Lần " + i + " Kết thúc");
            }

            // 3️⃣ Ghi dữ liệu từng cán bộ
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

                // Ghi các lần công tác
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
        loadData();
    }
    @FXML
    private void onDelete(){
        OfficerViewDTO selected = officerTableAbove60.getSelectionModel().getSelectedItem();

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
            loadData();
        }
    }
    @FXML
    private void onSearch(){

    }
}
