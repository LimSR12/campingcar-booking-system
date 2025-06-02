package admin.view;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import global.db.DBConnection;

/*
 * 전체 테이블 조회 전용 패널
 * 위의 JComboBox에서 테이블 이름을 선택하면
 * 그 아래 JTable에 SELECT * 결과를 표시
 */

public class AllTablesPanel extends JPanel {
	private JComboBox<String> tableCombo = new JComboBox<>();
	private JTable tableView = new JTable();
	
	public AllTablesPanel() {
		setLayout(new BorderLayout());
		
		// 콤보박스에 테이블 목록 로드
		try {
			Connection conn = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/camping", "root", "1234"
	            );
			
			ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null, "%", new String[] {"TABLE"});
			
			while (rs.next()) tableCombo.addItem(rs.getString("TABLE_NAME"));
		} catch (Exception e) {
			tableCombo.addItem("<로드 실패>");
		}
		
		// 테이블 선택 시 데이터 조회
		tableCombo.addActionListener(e -> loadTable());
		
		// UI 배치
		add(tableCombo, BorderLayout.NORTH);
		add(new JScrollPane(tableView), BorderLayout.SOUTH);
		
		// 초기 선택
		if (tableCombo.getItemCount() > 0) tableCombo.setSelectedIndex(0);
	}
	
	// 실제 SELECT 쿼리 실행
	private void loadTable() {
		String tbl = (String) tableCombo.getSelectedItem();
		if (tbl == null || tbl.startsWith("<")) return;
		
		try {
			Connection conn = DBConnection.getConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + tbl);
			
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i - 1] = md.getColumnLabel(i);
            }
            
            DefaultTableModel model = new DefaultTableModel(headers, 0);
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }
			
			tableView.setModel(model);
		} catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
	}
}
