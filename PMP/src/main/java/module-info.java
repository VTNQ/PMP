module com.qnp.pmp {
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires static lombok;
    requires com.jfoenix;
    requires java.desktop;
    requires javafx.controls;
    requires jbcrypt;
    requires java.sql;
    requires tess4j;
    requires mysql.connector.j;
    requires org.apache.commons.lang3;
    opens com.qnp.pmp to javafx.fxml;
    exports com.qnp.pmp;

    opens com.qnp.pmp.controllers to javafx.fxml;
    exports com.qnp.pmp.controllers;
}
