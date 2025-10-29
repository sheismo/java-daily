package dev.lpa;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class Main {
    private final static String CONN_STRING = "jdbc:mysql://localhost:3306/music";

    public static void main(String[] args) {
        String username = JOptionPane
                .showInputDialog(null, "Enter your DB username", "DB Username", JOptionPane.QUESTION_MESSAGE);
        JPasswordField passwordField = new JPasswordField();
        int okCancel = JOptionPane.showConfirmDialog(null, passwordField, "Enter DB password", JOptionPane.OK_CANCEL_OPTION);
        final char[] password = (okCancel == JOptionPane.OK_OPTION) ? passwordField.getPassword() : null;

        var dataSource = new MysqlDataSource();
//        dataSource.setUrl(CONN_STRING);
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("music");
        dataSource.setUser(username);
        dataSource.setPassword(String.valueOf(password));
//        try (Connection conn = DriverManager.getConnection(CONN_STRING,
//                username, String.valueOf(password))) {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Connected to the music database successfully");
            Arrays.fill(password, ' ');
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
