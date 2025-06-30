module com.qnp.pmp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.qnp.pmp to javafx.fxml;
    exports com.qnp.pmp;
    exports com.qnp.pmp.controllers;
    opens com.qnp.pmp.controllers to javafx.fxml;
}