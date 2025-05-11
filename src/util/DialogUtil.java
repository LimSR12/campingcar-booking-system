package util;

import javax.swing.JOptionPane;
import java.awt.Component;

public class DialogUtil {

    // 오류 메시지 표시용
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "오류",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // 경고창
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "경고",
            JOptionPane.WARNING_MESSAGE
        );
    }

    // 정보창 (예: 저장 완료 등)
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "알림",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
