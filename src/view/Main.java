package view;

import javax.swing.*;
import view.common.*;

import view.common.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RoleSelectFrame(); // 로그인 화면으로 시작
        });
    }
}
