package admin.view;

import global.db.DBConnection;
import global.util.DialogUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SimpleQueryDialog extends JDialog {
    private final JTextArea ta  = new JTextArea(4, 60);
    private final JTable    tbl = new JTable();

    public SimpleQueryDialog(Frame parent) {
        super(parent, "SQL Query 실행", true);
        setLayout(new BorderLayout(6,6));
        add(new JScrollPane(ta),  BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        JButton run = new JButton("실행");
        add(run, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(run);
        run.addActionListener(e -> runQuery());

        setSize(800,500);
        setLocationRelativeTo(parent);
    }

    private void runQuery() {
        String sql = ta.getText().trim();
        if (sql.isEmpty() || !sql.toUpperCase().startsWith("SELECT")) {
            DialogUtil.showWarning(this,"SELECT 쿼리문을 입력하세요.");
            return;
        }
        try (Connection c = DBConnection.getConnection();
             Statement  st= c.createStatement();
             ResultSet  rs= st.executeQuery(sql)) {

            // ResultSet → DefaultTableModel
            ResultSetMetaData md = rs.getMetaData();
            int colCnt = md.getColumnCount();

            String[] headers = new String[colCnt];
            for (int i = 1; i <= colCnt; i++) headers[i-1] = md.getColumnLabel(i);

            DefaultTableModel model = new DefaultTableModel(headers, 0);
            while (rs.next()) {
                Object[] row = new Object[colCnt];
                for (int i = 1; i <= colCnt; i++) row[i-1] = rs.getObject(i);
                model.addRow(row);
            }
            tbl.setModel(model);

        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }
}
