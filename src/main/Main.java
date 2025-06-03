package main;

import java.sql.*;

import javax.swing.*;

import common.view.RoleSelectFrame;

public class Main {
    public static void main(String[] args) throws SQLException {
    	createSchema();
        SwingUtilities.invokeLater(() -> {
            new RoleSelectFrame(); // 로그인 화면으로 시작
        });
    }
    
    // 
    public static void createSchema() throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "1234")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS DBTEST");
                System.out.println("DBTEST 스키마가 생성되었거나 이미 존재합니다.");
            }
        }
    }

}
