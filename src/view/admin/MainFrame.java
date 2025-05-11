package view.admin;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.*;
import db.DBInitializer;

public class MainFrame extends JFrame {

    public MainFrame() {
    	setTitle("관리자 메뉴");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 정렬
        
        // 프레임의 레이아웃을 BorderLayout 으로 설정
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        // 가운데 라벨
        JLabel title = new JLabel("Camping 관리자 콘솔", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        cp.add(title, BorderLayout.CENTER);

        // 버튼 래퍼 패널: FlowLayout 으로 버튼 크기 고정
        JButton initBtn = new JButton("DB 초기화");
        initBtn.setPreferredSize(new Dimension(140, 28));
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnWrap.add(initBtn);
        cp.add(btnWrap, BorderLayout.NORTH);

        // 버튼 리스너
        initBtn.addActionListener(e -> {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                    this, "DB를 초기화할까요?", "확인", JOptionPane.YES_NO_OPTION)) {
                try {
                    DBInitializer.run();
                    JOptionPane.showMessageDialog(this, "DB 초기화 완료!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "초기화 실패:\n" + ex.getMessage());
                }
            }
        });
        
        setVisible(true);
    }
}
