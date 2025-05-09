package view.common;

import javax.swing.*;
import java.awt.*;
import view.admin.AdminLoginFrame;
import view.user.UserLoginFrame;

public class RoleSelectFrame extends JFrame {

    public RoleSelectFrame() {
        setTitle("Campin 로그인 선택");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙
        setResizable(false);

        // 메인 패널 (세로 정렬)
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("로그인 유형을 선택하세요", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JButton adminBtn = new JButton("관리자 로그인");
        JButton userBtn = new JButton("회원 로그인");

        adminBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        userBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        adminBtn.addActionListener(e -> {
            dispose(); // 현재 창 닫기
            new AdminLoginFrame(); // 관리자 로그인 창 열기
        });

        userBtn.addActionListener(e -> {
            dispose(); // 현재 창 닫기
            new UserLoginFrame(); // 회원 로그인 창 열기
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(adminBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(userBtn);

        add(panel);
        setVisible(true);
    }
}
