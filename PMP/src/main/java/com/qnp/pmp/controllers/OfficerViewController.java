package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.dto.StudyRoundDTO;
import com.qnp.pmp.entity.Officer;
import com.qnp.pmp.service.OfficeService;
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
import com.qnp.pmp.dialog.Dialog;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
public class OfficerViewController {

    @FXML private TableView<OfficerViewDTO> officerTable;

    @FXML private TableColumn<OfficerViewDTO, String> fullNameCol;
    @FXML private TableColumn<OfficerViewDTO, String> positionCol;
    @FXML private TableColumn<OfficerViewDTO, String> unitCol;
    @FXML private TableColumn<OfficerViewDTO, Integer> birthYearCol;
    @FXML private TableColumn<OfficerViewDTO,String>noteCol;
    @FXML private TableColumn<OfficerViewDTO, String> homeTownCol;
    @FXML private TableColumn<OfficerViewDTO,Integer> totalAllowance;
    @FXML
    private TextField searchField;

    @FXML private Label totalLabel;

    private final OfficeService officeService = new OfficerServiceImpl();

    @FXML
    public void initialize() {

        // Gán dữ liệu cho các cột
        fullNameCol.setCellValueFactory(data -> data.getValue().fullNameProperty());
        positionCol.setCellValueFactory(data -> data.getValue().levelNameProperty());
        unitCol.setCellValueFactory(data -> data.getValue().unitProperty());
        homeTownCol.setCellValueFactory(data -> data.getValue().homeTownProperty());
        birthYearCol.setCellValueFactory(data -> data.getValue().birthYearProperty().asObject());
        noteCol.setCellValueFactory(data -> data.getValue().noteProperty());
        totalAllowance.setCellValueFactory(data->data.getValue().allowanceMonthsProperty().asObject());
        officerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Căn giữa dữ liệu cho tất cả cột
        centerAllColumns(fullNameCol, positionCol, unitCol, birthYearCol, homeTownCol,noteCol);

        // Tải dữ liệu ban đầu
        loadOfficerAllowance();
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
    private void showAddDialog(){
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/qnp/pmp/Officer/AddOfficerView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Officer");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
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
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setStyle("-fx-alignment: CENTER;");
            }
        });
    }

    @SafeVarargs
    private final void centerAllColumns(TableColumn<OfficerViewDTO, ?>... columns) {
        for (TableColumn<OfficerViewDTO, ?> col : columns) {
            centerCell(col);
        }
    }

    private void loadOfficerAllowance() {
        List<OfficerViewDTO>list=officeService.getOfficerAllowanceStatus();
        ObservableList<OfficerViewDTO> data =
                FXCollections.observableArrayList(officeService.getOfficerAllowanceStatus());
        officerTable.setItems(data);
        totalLabel.setText("Tổng: " + data.size() + " cán bộ");
        int maxRound = list.stream()
                .flatMap(dto -> dto.getStudyRounds().keySet().stream())
                .max(Integer::compareTo)
                .orElse(0);

        // Tạo các cột động cho từng "Lần n"
        for (int i = 1; i <= maxRound; i++) {
            final int roundNum = i;
            TableColumn<OfficerViewDTO, String> roundCol = new TableColumn<>("Lần " + roundNum + " (Từ ngày đến ngày)");

            roundCol.setCellValueFactory(cellData -> {
                StudyRoundDTO round = cellData.getValue().getStudyRounds().get(roundNum);
                return new SimpleStringProperty(round != null ? round.getFormatted() : "");
            });

            officerTable.getColumns().add(roundCol);
        }
    }
    @FXML
    private void add() {
        showAddDialog();
    }
    @FXML
    private void onSearch(KeyEvent event) {
            if(event.getCode()==KeyCode.ENTER) {
                String keyword=searchField.getText().trim();
                if (!keyword.isEmpty()) {
                    ObservableList<OfficerViewDTO> result =
                            FXCollections.observableArrayList(officeService.findByName(keyword));
                    officerTable.setItems(result);
                    totalLabel.setText("Tổng: " + result.size() + " cán bộ");
                } else {
                    loadOfficerAllowance(); // Nếu rỗng thì tải lại toàn bộ
                }
            }
    }
    @FXML
    private void refreshTable() {
        loadOfficerAllowance();
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
    @FXML
    private void onDelete() {
       try {
           ObservableList<OfficerViewDTO> selectedItems = officerTable.getSelectionModel().getSelectedItems();

           if (selectedItems == null || selectedItems.isEmpty()) {
               Dialog.displayErrorMessage("Vui lòng chọn ít nhất một cán bộ để xoá.");
               return;
           }

           // Xác nhận xoá
           Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
           alert.setTitle("Xác nhận xoá");
           alert.setHeaderText("Bạn có chắc chắn muốn xoá " + selectedItems.size() + " cán bộ?");
           alert.setContentText("Hành động này không thể hoàn tác.");
           if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
               return;
           }

           // ✅ Lấy danh sách ID cần xoá
           int idDeleted =selectedItems.get(0).getId().get();
           officeService.deleteOfficer(idDeleted);
           Dialog.displaySuccessFully("Xóa cán bộ thành công");
           loadOfficerAllowance();
       }catch (Exception e) {
           Dialog.displayErrorMessage("Lỗi xóa cán bộ");
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
                            fields[5]                  // homeTown
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
                        getCellString(row.getCell(5))                        // homeTown
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

}
