module com.qnp.pmp {
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.apache.poi.ooxml;      // XSSFWorkbook, .xlsx
    requires org.apache.poi.poi;
    requires static lombok;
    requires com.jfoenix;
    requires java.desktop;
    requires javafx.controls;
    requires jbcrypt;
    requires java.sql;
    requires mysql.connector.j;
    opens com.qnp.pmp to javafx.fxml;
    exports com.qnp.pmp;

    opens com.qnp.pmp.controllers to javafx.fxml;
    exports com.qnp.pmp.controllers;
}
