package admin.view;

import admin.dao.CrudDao;
import global.util.DialogUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/*
CRUD 공통 로직 (조회·카드 전환·폼 초기화 등)을 처리하는 추상 패널
 */
public abstract class AbstractTableCRUDPanel<T> extends JPanel {
    protected final CrudDao<T> dao;
    private   final String[]   columnNames;

    protected final CardLayout cards   = new CardLayout();
    protected final JPanel   cardPane = new JPanel(cards);
    protected JTable         table;

    public AbstractTableCRUDPanel(CrudDao<T> dao) {
        super(new BorderLayout());
        this.dao = dao;
        this.columnNames = dao.getColumnNames();

        // 툴바
        JToolBar tb = new JToolBar();
        JButton bView = new JButton("조회");
        JButton bNew  = new JButton("입력");
        tb.add(bView);
        tb.add(bNew);
        add(tb, BorderLayout.NORTH);

        // 카드: 목록 / 폼
        cardPane.add(createListPanel(), "VIEW");
        cardPane.add(createFormPanel(), "FORM");
        add(cardPane, BorderLayout.CENTER);

        // 리스너
        bView.addActionListener(e -> showView());
        bNew .addActionListener(e -> clearAndShowForm());

        // 최초 표시
        showView();
    }

    // ─── 목록 카드 ───
    private JScrollPane createListPanel() {
        table = new JTable();
        return new JScrollPane(table);
    }

    protected void showView() {
        cards.show(cardPane, "VIEW");
        refreshTable();
    }

    protected void refreshTable() {
        try {
            List<T> rows = dao.findAll();
            DefaultTableModel m = new DefaultTableModel(columnNames, 0);
            for (T e : rows) m.addRow( dao.toRow(e) );
            table.setModel(m);
            
            // 헤더 폰트
            JTableHeader header = table.getTableHeader();
            Font origHeaderFont = header.getFont();
            header.setFont(origHeaderFont.deriveFont(Font.BOLD, 16f));
            
            // 본문 폰트
            Font cellFont = origHeaderFont.deriveFont(Font.PLAIN, 14f);
            table.setFont(cellFont);
            
            
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }

    // 폼 카드: 서브클래스가 구현
    protected abstract JPanel createFormPanel();

    // 폼 초기화 & 표시
    protected abstract void clearAndShowForm();

    // 폼 데이터 읽어 저장
    protected abstract void saveForm();
}
