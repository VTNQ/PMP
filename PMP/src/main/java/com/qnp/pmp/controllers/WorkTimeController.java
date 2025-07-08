package com.qnp.pmp.controllers;

import com.qnp.pmp.dto.WorkTimeDTO;
import com.qnp.pmp.service.impl.WorkTimeServiceImpl;
import com.qnp.pmp.service.WorkTimeService;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class WorkTimeController {

    @FXML private TextField officerIdField;
    @FXML private TableView<WorkTimeDTO> workTimeTable;
    @FXML private TableColumn<WorkTimeDTO, LocalDate> startDateColumn;
    @FXML private TableColumn<WorkTimeDTO, LocalDate> endDateColumn;
    @FXML private TableColumn<WorkTimeDTO, Boolean> validColumn;
    @FXML private TableColumn<WorkTimeDTO, String> noteColumn;
    @FXML private Label resultLabel;

    private final WorkTimeService workTimeService = new WorkTimeServiceImpl(); // JavaFX thuần

    private final ObservableList<WorkTimeDTO> workTimeData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        startDateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getEndDate()));
        validColumn.setCellValueFactory(cell -> new ReadOnlyBooleanWrapper(cell.getValue().isValid()));
        noteColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getNote()));

        workTimeTable.setItems(workTimeData);
    }

    @FXML
    public void handleLoadWorkTimes() {
        String officerId = officerIdField.getText();
        if (officerId == null || officerId.isBlank()) {
            showAlert("Vui lòng nhập mã cán bộ");
            return;
        }

        // 🧪 Dữ liệu giả (demo). Bạn nên load từ file hoặc DB nếu có:
        List<WorkTimeDTO> mockData = createMockWorkTimeList(officerId);
        workTimeData.setAll(mockData);
        resultLabel.setText("Tải " + mockData.size() + " dòng dữ liệu.");
    }

    @FXML
    public void handleCalculateDays() {
        Set<LocalDate> holidays = getNgayNghiLe();
        long days = workTimeService.getTotalValidWorkingDays(workTimeData, holidays);
        resultLabel.setText("Tổng số ngày làm việc thực tế: " + days);
    }

    @FXML
    public void handleCalculateMonths() {
        Set<LocalDate> holidays = getNgayNghiLe();
        long months = workTimeService.getRoundedValidMonths(workTimeData, holidays);
        resultLabel.setText("Tổng số tháng hợp lệ (≥15 ngày làm việc): " + months);
    }

    private Set<LocalDate> getNgayNghiLe() {
        return new HashSet<>(Arrays.asList(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 4, 30),
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 9, 2)
        ));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 🧪 Hàm tạo dữ liệu test
    private List<WorkTimeDTO> createMockWorkTimeList(String officerId) {
        List<WorkTimeDTO> list = new ArrayList<>();

        WorkTimeDTO dto1 = new WorkTimeDTO();
        dto1.setOfficerId(officerId);
        dto1.setStartDate(LocalDate.of(2023, 1, 5));
        dto1.setEndDate(LocalDate.of(2023, 1, 25));
        dto1.setNote("Thời gian làm việc 1");
        dto1.setMonthCalculated(true);
        list.add(dto1);

        WorkTimeDTO dto2 = new WorkTimeDTO();
        dto2.setOfficerId(officerId);
        dto2.setStartDate(LocalDate.of(2023, 2, 1));
        dto2.setEndDate(LocalDate.of(2023, 2, 10));
        dto2.setNote("Đợt ngắn không đủ 15 ngày");
        dto2.setMonthCalculated(true);
        list.add(dto2);

        return list;
    }
}
