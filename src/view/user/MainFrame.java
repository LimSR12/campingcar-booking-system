package view.user;

import javax.swing.*;

import campingcar.view.CampingCarPanel;

import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel rightPanel = new JPanel(cardLayout);
    
    public static void main(String[] args) {
        new MainFrame();
    }

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
        JButton btnCamping = createMenuButton("캠핑카 조회");
        JButton btnReservation = createMenuButton("예약 확인");
        JButton btnRepair = createMenuButton("정비 의뢰");

        // 여백 + 버튼 추가
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnCamping);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnReservation);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(btnRepair);
        menuPanel.add(Box.createVerticalGlue());

        add(menuPanel, BorderLayout.WEST); // 패널을 왼쪽 배치

        // 오른쪽 패널 (CardLayout)
        rightPanel.add(new CampingCarPanel(), "camping");
        rightPanel.add(new ReservationPanel(), "reservation");
        add(rightPanel, BorderLayout.CENTER);

        // 버튼 이벤트
        btnCamping.addActionListener(e -> cardLayout.show(rightPanel, "camping"));
        btnReservation.addActionListener(e -> cardLayout.show(rightPanel, "reservation"));

        setVisible(true);
    }

    // 왼쪽 메뉴 패널에 사용하기 좋게 버튼 생성해서 반환하는 메서드
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        // 버튼 중앙 정렬
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        // 버튼 크기 고정
        button.setMaximumSize(new Dimension(140, 40));
        return button;
    }

}
