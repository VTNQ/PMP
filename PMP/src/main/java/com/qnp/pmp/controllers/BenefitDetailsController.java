package com.qnp.pmp.controllers;

import com.qnp.pmp.service.AllowanceService;
import com.qnp.pmp.service.impl.AllowanceServiceImpl;
import javafx.beans.binding.Bindings;
import com.qnp.pmp.dto.BenefitDetailDTO;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

public class BenefitDetailsController {
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TextField searchField;
    @FXML private TableView<BenefitDetailDTO>benefitTable;
    @FXML private TableColumn<BenefitDetailDTO,Integer>colIndex;
    @FXML private TableColumn<BenefitDetailDTO, LocalDate>colStartDate;
    @FXML private TableColumn<BenefitDetailDTO, LocalDate>colEndDate;
    @FXML private TableColumn<BenefitDetailDTO,String>colDecisionStart;
    @FXML private TableColumn<BenefitDetailDTO,String>colDecisionEnd;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private FilteredList<BenefitDetailDTO> filtered;
    private final AllowanceService allowanceService=new AllowanceServiceImpl();
    @FXML
    private void initialize() {
        colIndex.setCellValueFactory(c ->
                Bindings.createIntegerBinding(
                        () -> benefitTable.getItems().indexOf(c.getValue()) + 1
                ).asObject()
        );
        benefitTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colStartDate.setCellValueFactory(c -> c.getValue().startDateActualProperty());
        colEndDate.setCellValueFactory(c -> c.getValue().endDateActualProperty());
        colDecisionStart.setCellValueFactory(c->c.getValue().decisionStartProperty());
        colDecisionEnd.setCellValueFactory(c->c.getValue().decisionEndProperty());
        benefitTable.setRowFactory(tv -> {
            TableRow<BenefitDetailDTO> row = new TableRow<>();

            // Double-click
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                 openEditDialog(row.getItem());
                }
            });

            // Enter để mở
            row.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });

            // Context menu (chuột phải)
            MenuItem miEdit = new MenuItem("Cập nhật…");
            miEdit.setOnAction(ae -> {
                if (!row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });
            MenuItem miDelete = new MenuItem("Xóa");

            ContextMenu cm = new ContextMenu(miEdit, miDelete);
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(cm)
            );

            return row;
        });
        setupDateColumn(colStartDate);
        setupDateColumn(colEndDate);


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
    private void openEditDialog(BenefitDetailDTO original) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qnp/pmp/AllowanceTime/benefit-edit.fxml"));
            Parent root = loader.load();

            BenefitEditController editController = loader.getController();
            editController.setData(original);


            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            Stage stage = new Stage(StageStyle.TRANSPARENT); // bo góc đẹp nếu CSS có radius
            stage.setTitle("Cập nhật quyền lợi");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Nếu bạn có node cha hiện tại, gán owner để đúng modal (vd: someNode.getScene().getWindow())
            // stage.initOwner(someNode.getScene().getWindow());

            stage.setScene(scene);

            // Kéo thả cửa sổ nếu bạn muốn:
            enableWindowDragging(stage, root);

            // ESC đóng
            scene.getAccelerators().put(
                    new javafx.scene.input.KeyCodeCombination(javafx.scene.input.KeyCode.ESCAPE),
                    stage::close
            );

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể mở cửa sổ cập nhật:\n" + e.getMessage()).showAndWait();
        }
    }
    private void setupDateColumn(TableColumn<BenefitDetailDTO, LocalDate> col) {
        col.setCellFactory(tc -> new TableCell<BenefitDetailDTO, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatter.format(item));
            }
        });
    }
    public void loadData(int officerId) {
        List<BenefitDetailDTO> raw = allowanceService.getBenefitDetails(officerId); // lấy từ DB
        setData(raw); // dùng chung pipeline filter/sort
    }
    public void setData(List<BenefitDetailDTO> benefitDetailDTOs) {
        filtered = new FilteredList<>(FXCollections.observableArrayList(benefitDetailDTOs));
        SortedList<BenefitDetailDTO> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(benefitTable.comparatorProperty());
        benefitTable.setItems(sorted);
        applyFilter();
    }
    private void applyFilter() {
        if (filtered == null) return;

        LocalDate from = fromDatePicker.getValue();
        LocalDate to   = toDatePicker.getValue();
        String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        Predicate<BenefitDetailDTO> p = d -> {
            // text match theo 2 cột quyết định
            boolean textOk = q.isEmpty()
                    || (safe(d.getDecisionStart()).toLowerCase().contains(q))
                    || (safe(d.getDecisionEnd()).toLowerCase().contains(q));

            // day range: bắt đầu thực >= from, kết thúc thực <= to (nếu có)
            LocalDate s = d.getStartDateActual();
            LocalDate e = d.getEndDateActual();
            boolean fromOk = (from == null) || (s != null && !s.isBefore(from));
            boolean toOk   = (to == null)   || (e != null && !e.isAfter(to));

            return textOk && fromOk && toOk;
        };

        filtered.setPredicate(p);
        // refresh để STT cập nhật đúng vị trí
        benefitTable.refresh();
    }

    private static String safe(String s) { return s == null ? "" : s; }
    @FXML
    private void onFilter() {
        applyFilter();
    }

    @FXML
    private void onClearFilter() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        searchField.clear();
        applyFilter();
    }

    @FXML
    private void onClose() {
        // Đóng cửa sổ hiện tại
        ((Stage) benefitTable.getScene().getWindow()).close();
    }

}
