package view.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import db.DBInitializer;

/*
 * 관리자 전용 메인 프레임
 * DB 초기화 버튼
 * 전체 테이블 조회 버튼
 */

public class MainFrame extends JFrame {
	private CardLayout card = new CardLayout();
	private JPanel content = new JPanel(card);
	private boolean initialized = false; // DB 초기화 여부 플래그

    public MainFrame() {
        setTitle("관리자 콘솔");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 왼쪽 메뉴 트리
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("기능");
        root.add(new DefaultMutableTreeNode("DB 초기화"));
        root.add(new DefaultMutableTreeNode("전체 테이블 보기"));
        JTree tree = new JTree(root);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(e -> {
            String key = e.getPath().getLastPathComponent().toString();
            
            // 왼쪽 트리 선택과 오른쪽 카드를 연결
            if (key.equals("전체 테이블 보기") && !initialized) {
            	// 초기화 전에는 조회 불가
            	JOptionPane.showMessageDialog(this, "먼저 DB 초기화를 해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            	tree.setSelectionRow(0);
            	return;
            }
            
            card.show(content, key);
        });

        // 오른쪽 카드들
        content.add(makeInitPanel(), "DB 초기화");
        content.add(new AllTablesPanel(), "전체 테이블 보기");

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tree), content);
        split.setDividerLocation(180);

        add(split);
        setVisible(true);
    }

    // DB 초기화 카드 (버튼 하나)
    private JPanel makeInitPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        JButton btn = new JButton("init.sql 실행 → DB 초기화");
        btn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "DB를 초기화할까요?", "확인",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { 
                	DBInitializer.run();
                	initialized = true;
                    JOptionPane.showMessageDialog(this, "DB 초기화 완료!");
                } catch (Exception ex) {
                      JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });
        p.add(btn);
        return p;
    }
}
