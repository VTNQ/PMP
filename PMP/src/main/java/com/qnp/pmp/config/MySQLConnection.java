package com.qnp.pmp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static String host="mysql-227a4303-votannamquoc-130504.j.aivencloud.com";

    private static String port="22125";
    private static String username="avnadmin";
    private static String password="";
    private static String database="PersonalManagement";
    private static String url="jdbc:mysql://" + host + ":" + port + "/" + database + "?sslmode=require";
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to connect to the database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ClassNotFoundException("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }
}
