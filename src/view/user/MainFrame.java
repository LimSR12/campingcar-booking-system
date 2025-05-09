package view.user;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Camping"); // 상단 제목
        setSize(700, 500);  // 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 정렬

        // 간단한 라벨 추가
        JLabel welcomeLabel = new JLabel("Camping", SwingConstants.CENTER);
        welcomeLabel.setFont(new java.awt.Font("맑은 고딕", java.awt.Font.BOLD, 20));
        add(welcomeLabel);

        setVisible(true);
    }
}
