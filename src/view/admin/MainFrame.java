package view.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import db.DBConnection;
import db.DBInitializer;
import util.DialogUtil;

/*
 * 관리자 전용 메인 프레임
 * DB 초기화 버튼
 * 전체 테이블 조회 버튼
 */

public class MainFrame extends JFrame {
	private JTree tree;
	private DefaultMutableTreeNode root;
	private CardLayout card = new CardLayout();
	private JPanel content = new JPanel(card);
	private boolean initialized = false; // DB 초기화 여부 플래그

    public MainFrame() {
        setTitle("관리자 콘솔");
        setSize(1200, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        
        // 상단에 버튼 2개만 담을 패널
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton initBtn = new JButton("DB 초기화");
        JButton allBtn  = new JButton("전체 테이블 보기");
        topButtons.add(initBtn);
        topButtons.add(allBtn);
        getContentPane().add(topButtons, BorderLayout.NORTH);
        
        initBtn.addActionListener(e -> onInitialize());
        allBtn.addActionListener(e -> selectFunction("ALL"));

        // 트리는 초기에 빈 루트만
        root = new DefaultMutableTreeNode("테이블");
        tree = new JTree(root);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(e -> {
        	String tbl = e.getPath().getLastPathComponent().toString();
        	selectFunction(tbl);
        });
        
        // content 카드: 초기화 안내
        content.add(makeInitPanel(), "INIT");
        
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tree),
                content
            );
        split.setDividerLocation(180);
        getContentPane().add(split, BorderLayout.CENTER);

        setVisible(true);
    }
    
    private void onInitialize() {
        if (initialized) return;
        if (JOptionPane.showConfirmDialog(this,
                "DB를 초기화할까요?", "확인",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            DBInitializer.run();
            initialized = true;
            DialogUtil.showInfo(this, "DB 초기화 완료!");

            // 메뉴에서 전체 테이블 보기 선택 시 켜질 ALL 카드 준비
            content.add(makeAllTablesPanel(), "ALL");
            
            // 트리에 실제 테이블 노드 추가
            DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode("테이블");
            for (String tbl : DBInitializer.getTableNames()) {
            	newRoot.add(new DefaultMutableTreeNode(tbl));
                // 오른쪽에 TableCRUDPanel(tbl) 카드 추가
                content.add(new TableCRUDPanel(tbl), tbl);
            }
            
            root = newRoot;
            tree.setModel(new DefaultTreeModel(root));
            
            // 트리 첫 항목 자동 선택
            tree.setSelectionRow(0);
            
        } catch (Exception ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }
    
    private void selectFunction(String key) {
        if (!initialized && !"INIT".equals(key)) {
            DialogUtil.showWarning(this, "먼저 DB 초기화를 해주세요.");
            return;
        }
        if ("ALL".equals(key)) {
            card.show(content, "ALL");
        } else if ("INIT".equals(key)) {
            // 트리가 아닌 메뉴바에서 “DB 초기화” 클릭 시
            onInitialize();
        } else {
            // 테이블 명(key) 클릭 시 해당 TableCRUDPanel 띄우기
            card.show(content, key);
        }
    }
    
    private JPanel makeInitPanel() {
        JPanel p = new JPanel(new FlowLayout());
        p.add(new JLabel("DB 초기화를 눌러 주세요."));
        return p;
    }

    // 전체 테이블 보기용 패널
    private JPanel makeAllTablesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        try {
        	// 각 테이블을 탭으로 구성해 누르면 조회 가능하도록
            for (String tbl : DBInitializer.getTableNames()) {
                DefaultTableModel model = new DefaultTableModel();
                JTable t = new JTable(model);
                refreshTable(tbl, model);   
                tabs.addTab(tbl, new JScrollPane(t));
            }
        } catch (SQLException ex) {
            DialogUtil.showError(this, "전체 테이블 조회 실패:\n" + ex.getMessage());
        }
        p.add(tabs, BorderLayout.CENTER);
        return p;
    }

    // helper: 지정 테이블의 데이터를 DefaultTableModel에 채워 줌
    private void refreshTable(String tableName, DefaultTableModel model) throws SQLException {
        model.setRowCount(0);
        // 컬럼 헤더 설정
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
                // LIMIT 1 로 컬럼만 가져오기
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
        	
            ResultSetMetaData md = rs.getMetaData();
            Vector<String> cols = new Vector<>();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                cols.add(md.getColumnLabel(i));
            }
            model.setColumnIdentifiers(cols);
        }
        
        // 실제 데이터 로드
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            	 
            ResultSetMetaData md2 = rs.getMetaData();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= md2.getColumnCount(); i++) {
                    row.add(rs.getObject(i));
                }
                model.addRow(row);
            }
        }
    }
}
