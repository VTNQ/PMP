package com.qnp.pmp.controllers;

import com.qnp.pmp.dialog.Dialog;
import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.service.ExcelBackupService;
import com.qnp.pmp.service.OfficeService;
import com.qnp.pmp.service.impl.ExcelBackupServiceImpl;
import com.qnp.pmp.service.impl.OfficerServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
                    continue; // bỏ qua dòng tiêu đề
                }

                String[] fields = line.split(",", -1);
                if (fields.length >= 8) {
                    Officer officer = new Officer(
                            fields[0],                      // fullName
                            fields[1],    // levelId
                            fields[2],                      // unit
                            fields[3],     // since
                            Integer.valueOf(fields[4]),                      // identifier
                            fields[5]         ,
                            LocalDate.parse(fields[6])// homeTown
                    );
                    officerList.add(officer);
                }
            }
            officeService.saveOfficerAll(officerList);
            Dialog.displaySuccessFully("Đã lưu " + officerList.size() + " cán bộ");
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Không thể đọc file CSV");
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
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
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
                int birthYear = 0;
                try {
                    birthYear= Integer.parseInt(getCellString(row.getCell(4)));
                } catch (NumberFormatException e) {
                    // có thể log cảnh báo nếu cần
                }
                Officer officer = new Officer(
                        getCellString(row.getCell(0)),                        // fullName
                        getCellString(row.getCell(1)),                        // phone
                        getCellString(row.getCell(2)),        // level
                        getCellString(row.getCell(3)),                        // unit
                        birthYear,                        // identifier
                        getCellString(row.getCell(5)),
                        getCellLocalDate( row.getCell(6))// homeTown
                );
                officerList.add(officer);
            }

            officeService.saveOfficerAll(officerList);
            Dialog.displaySuccessFully("Đã lưu " + officerList.size() + " cán bộ");

        } catch (Exception e) {
            e.printStackTrace();
            Dialog.displayErrorMessage("Không thể đọc file Excel");
        }
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


}
