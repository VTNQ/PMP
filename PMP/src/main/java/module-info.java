module com.qnp.pmp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires static lombok;
    opens com.qnp.pmp to javafx.fxml;
    exports com.qnp.pmp;

    opens com.qnp.pmp.controllers to javafx.fxml;
    exports com.qnp.pmp.controllers;
}
