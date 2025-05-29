package user.view;

import javax.swing.*;

import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel rightPanel = new JPanel(cardLayout);
    private MyReservationPanel myReservationPanel = new MyReservationPanel();
    
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
        rightPanel.add(myReservationPanel, "reservation");
        add(rightPanel, BorderLayout.CENTER);

        // 버튼 이벤트
        btnCamping.addActionListener(e -> cardLayout.show(rightPanel, "camping"));
        btnReservation.addActionListener(e -> {
        	myReservationPanel.loadReservationData();
        	cardLayout.show(rightPanel, "reservation");
        });

        setVisible(true);
    }

    // 왼쪽 메뉴 패널에 사용하기 좋게 버튼 생성해서 반환하는 메서드
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        
        button.setAlignmentX(Component.CENTER_ALIGNMENT);// 버튼 중앙 정렬
        button.setMaximumSize(new Dimension(140, 40));// 버튼 크기 고정
        
     // 색상 & 디자인
        button.setBackground(new Color(70, 130, 180)); // 스틸 블루
        button.setForeground(Color.WHITE);            // 글자색 흰색
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setFocusPainted(false);                // 클릭 테두리 제거
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 안쪽 여백

        // 마우스 오버 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // 연한 블루
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180)); // 원래 색상
            }
        });
        return button;
    }

}
