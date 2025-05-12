package view.user;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel rightPanel = new JPanel(cardLayout);

    public MainFrame() {
        setTitle("캠핑카 예약 시스템");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 왼쪽 메뉴 패널
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setPreferredSize(new Dimension(150, getHeight())); // 폭 고정

        // 버튼 생성
        JButton btnCamping = new JButton("캠핑카 조회");
        JButton btnReservation = new JButton("예약 확인");

        // 버튼 크기 고정
        Dimension buttonSize = new Dimension(140, 40);
        btnCamping.setMaximumSize(buttonSize);
        btnReservation.setMaximumSize(buttonSize);

        // 여백 + 버튼 추가
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnCamping);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnReservation);
        menuPanel.add(Box.createVerticalGlue());

        add(menuPanel, BorderLayout.WEST); // 왼쪽에 배치

        // 오른쪽 패널 (CardLayout)
        rightPanel.add(new CampingCarPanel(), "camping");
        rightPanel.add(new ReservationPanel(), "reservation"); // 예시용 빈 패널
        add(rightPanel, BorderLayout.CENTER);

        // 버튼 이벤트
        btnCamping.addActionListener(e -> cardLayout.show(rightPanel, "camping"));
        btnReservation.addActionListener(e -> cardLayout.show(rightPanel, "reservation"));

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
