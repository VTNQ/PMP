module com.qnp.pmp {
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires static lombok;
    requires com.jfoenix;
    requires java.desktop;
    requires javafx.controls;
    requires jbcrypt;
    opens com.qnp.pmp to javafx.fxml;
    exports com.qnp.pmp;

    opens com.qnp.pmp.controllers to javafx.fxml;
    exports com.qnp.pmp.controllers;
}
