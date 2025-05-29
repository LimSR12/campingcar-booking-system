package view.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import db.DBConnection;
import db.ResultSetTableModel;

public class TableCRUDPanel extends JPanel {
	private String tableName;
	private CardLayout cards = new CardLayout();
	private JPanel cardPane = new JPanel(cards);
	private JTable table;
	private Map<String, JTextField> inputs = new LinkedHashMap<>();
	
	public TableCRUDPanel(String tableName) throws SQLException {
		this.tableName = tableName;
		setLayout(new BorderLayout());
		
		// 툴바
		JToolBar tb = new JToolBar();
        JButton bView = new JButton("조회");
        JButton bIns  = new JButton("입력");
        JButton bUpd  = new JButton("수정");
        JButton bDel  = new JButton("삭제");
        Stream.of(bView,bIns,bUpd,bDel).forEach(tb::add);
        add(tb, BorderLayout.NORTH);
        
        // 카드: List / Form
        cardPane.add(createListPanel(), "VIEW");
        cardPane.add(createFormPanel(), "FORM");
        add(cardPane, BorderLayout.CENTER);
        
        // 리스너
        bView.addActionListener(e -> showView());
        bIns .addActionListener(e -> showForm(null));
        bUpd .addActionListener(e -> {
            Object pk = getSelectedPK();
            if (pk!=null) showForm(loadRow(pk));
        });
        bDel .addActionListener(e -> deleteSelected());
        
        // 최초에는 조회 화면 보여주기
        showView();
	}
	
    private Object deleteSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object loadRow(Object pk) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getSelectedPK() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object showForm(Object object) {
		// 모든 입력 칸 비우기
		inputs.values().forEach(tf -> tf.setText(""));
		
		// 수정 기능: object 값으로 inputs 채워주기
		
		// Form 카드로 전환
		cards.show(cardPane, "FORM");
		
		return null;
	}

	private void showView() {
        cards.show(cardPane, "VIEW");
        refreshTable();
	}

	private JScrollPane createListPanel() {
		// 빈 테이블 생성
        table = new JTable();
        return new JScrollPane(table);
    }
    
    private void refreshTable() {
		if (tableName == null || tableName.startsWith("<")) return;
		
		String sql = "SELECT * FROM " + tableName;
        try (Connection conn = DBConnection.getConnection();
                Statement st    = conn.createStatement();
                ResultSet rs    = st.executeQuery(sql)) {

            // ResultSetTableModel은 이미 ResultSet → TableModel 변환해 주는 유틸
            try {
                table.setModel(new ResultSetTableModel(rs));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "테이블 로드 중 오류 발생:\n" + ex.getMessage(),
                    "DB 오류",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            
        } catch (SQLException sqlEx) {
            JOptionPane.showMessageDialog(
                this,
                "DB 연결/쿼리 오류:\n" + sqlEx.getMessage(),
                "SQL 오류",
                JOptionPane.ERROR_MESSAGE
            );
        }
		
	}

    // 입력 패널 생성
	private JPanel createFormPanel() throws SQLException {
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        try (Connection c = DBConnection.getConnection()) {
            DatabaseMetaData md = c.getMetaData();
            ResultSet rs = md.getColumns(null,null,tableName,"%");
            while(rs.next()) {
                String col = rs.getString("COLUMN_NAME");
                p.add(new JLabel(col));
                JTextField tf = new JTextField();
                inputs.put(col, tf);
                p.add(tf);
            }
        }
        JPanel btns = new JPanel();
        JButton save = new JButton("저장");
        JButton cancel = new JButton("취소");
        save.addActionListener(e -> saveForm());
        cancel.addActionListener(e -> cards.show(cardPane,"VIEW"));
        btns.add(save);
        btns.add(cancel);
        p.add(btns);
        return p;
    }

	private Object saveForm() {
		if (tableName == null) return null;
		
		try (Connection conn = DBConnection.getConnection()) {
			// ["id", "name", "email"]
			String cols = String.join(", ", inputs.keySet());
			
			// ["?", "?", "?"]
			String holes = inputs.keySet().stream()
								.map(c -> "?")
								.collect(Collectors.joining(", "));
			
			// "INSERT INTO user (id, name, email) VALUES (?, ?, ?)"
			String sql = "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + holes + ")";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			// INSERT INTO user (id, name, email) VALUES ('123', 'Alice', 'alice@example.com');
			int idx = 1;
			for (JTextField tf : inputs.values()) {
				try {
					ps.setString(idx++, tf.getText());
				} catch (Exception ex) {
	                JOptionPane.showMessageDialog(
	                        this,
	                        "텍스트 로드 중 오류 발생:\n" + ex.getMessage(),
	                        "쿼리문 작성 중 오류",
	                        JOptionPane.ERROR_MESSAGE
	                );
				}
			}
			
            // 실행 및 결과 알림
            int inserted = ps.executeUpdate();
            JOptionPane.showMessageDialog(
                this,
                inserted + " 행이 추가되었습니다.",
                "입력 완료",
                JOptionPane.INFORMATION_MESSAGE
            );
		} catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "저장 중 오류 발생:\n" + e.getMessage(),
                    "DB 오류",
                    JOptionPane.ERROR_MESSAGE
          );
		}
		showView();
		return null;
	}
    
    
    
    
}
