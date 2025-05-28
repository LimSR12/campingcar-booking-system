package main;

import javax.swing.*;

import common.view.RoleSelectFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RoleSelectFrame(); // 로그인 화면으로 시작
        });
    }
}
