package user.view;

import global.session.Session;
import user.dao.ExternalMaintenanceDao;
import user.dao.ExternalMaintenanceDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;

public class ExternalMaintenancePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public ExternalMaintenancePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("외부 정비 내역");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"정비번호", "정비소", "정비내용", "정비일시", "정비비용"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadRepairData(); // 첫 화면 진입 시 데이터 로드
    }

    private void loadRepairData() {
        Long customerId = Session.getCustomerId();
        ExternalMaintenanceDao dao = new ExternalMaintenanceDao();
        List<ExternalMaintenanceDto> list = dao.getRepairsByCustomerId(customerId);

        tableModel.setRowCount(0);
        for (ExternalMaintenanceDto dto : list) {
            Object[] row = {
                dto.getId(),
                dto.getCenterName(),
                dto.getRepairDetails(),
                dto.getFormattedRepairDate(),
                String.format("%,.0f원", dto.getRepairFee())
            };
            tableModel.addRow(row);
        }
    }
}
