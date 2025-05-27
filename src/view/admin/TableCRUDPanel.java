package view.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import db.DBConnection;

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
		// TODO Auto-generated method stub
		return null;
	}

	private Object showView() {
		// TODO Auto-generated method stub
		return null;
	}

	private JScrollPane createListPanel() {
        DefaultTableModel model = new DefaultTableModel();
        table = new JTable(model);
        refreshTable(null);
        return new JScrollPane(table);
    }
    
    private void refreshTable(Object object) {
		// TODO Auto-generated method stub
		
	}

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
        btns.add(save); btns.add(cancel);
        p.add(btns);
        return p;
    }

	private Object saveForm() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
    
    
}
