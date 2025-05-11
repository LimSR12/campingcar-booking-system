package view.admin;

import java.awt.BorderLayout;

import javax.swing.*;
import db.DBInitializer;

public class MainFrame extends JFrame {

    public MainFrame() {
    	setTitle("관리자 메뉴");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 정렬
        
        // 중앙 라벨
        add(new JLabel("Camping 관리자 콘솔", SwingConstants.CENTER), BorderLayout.CENTER);
        
        // 하단 버튼
        JButton initBtn = new JButton("DB 초기화");
        add(initBtn, BorderLayout.SOUTH);
        
        // 버튼 리스너
        initBtn.addActionListener(e -> {
        	int ans = JOptionPane.showConfirmDialog(
        			this, "DB를 초기화할까요?", "DB 초기화",
        			JOptionPane.YES_NO_OPTION);
        	if (ans != JOptionPane.YES_OPTION) return;
        	
        	try {
        		DBInitializer.run();
        		JOptionPane.showMessageDialog(this, "DB 초기화 완료!");
        	} catch (Exception ex) {
        		JOptionPane.showMessageDialog(this, "초기화 실패:\n" + ex.getMessage());
        	}
        });

        setVisible(true);
    }
}
