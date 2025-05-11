package view.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        
        // 버튼
        JButton viewBtn = new JButton("전체 캠핑카 조회하기");
        viewBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 18));

        viewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CampingCarListFrame(); // 새 창 띄우기
            }
        });

        JPanel centerPanel = new JPanel();
        centerPanel.add(viewBtn);
        add(centerPanel, BorderLayout.CENTER);


        setVisible(true);
    }
}
