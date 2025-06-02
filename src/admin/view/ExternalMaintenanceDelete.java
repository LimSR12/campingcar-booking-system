package admin.view;

import admin.dao.ExternalMaintenanceDao;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * external_maintenance 테이블의 “조건식 기반 삭제” 다이얼로그
 *  - 삭제할 WHERE 조건식만 입력받음
 *  - 삭제 후 부모 패널(tableView) 새로고침
 */
public class ExternalMaintenanceDelete extends JDialog {
    private final ExternalMaintenanceDao dao;
    private final Runnable               onSuccessRefresh;

    // 단일 조건식 입력용 텍스트필드
    private final JTextField tfCondition = new JTextField(30);

    public ExternalMaintenanceDelete(Frame parent,
                                     ExternalMaintenanceDao dao,
                                     Runnable onSuccessRefresh) {
        super(parent, "조건식에 해당하는 external_maintenance 삭제하기", true);
        this.dao              = dao;
        this.onSuccessRefresh = onSuccessRefresh;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setContentPane(content);

        // 1) “한글→영문컬럼명 안내” 레이블
        JLabel lblMapping = new JLabel(
            "<html>" +
            "&nbsp;<b>※ 조건식에 영문 컬럼명 사용하십시오.</b><br>" +
            "&nbsp;Car ID → car_id&nbsp;&nbsp;&nbsp;&nbsp;" +
            "Center ID → center_id&nbsp;&nbsp;&nbsp;&nbsp;<br>" +
            "&nbsp;Customer ID → customer_id&nbsp;&nbsp;&nbsp;&nbsp;" +
            "License No → license_number<br>" +
            "&nbsp;Company ID → company_id&nbsp;&nbsp;&nbsp;&nbsp;" +
            "Repair Details → repair_details<br>" +
            "&nbsp;Repair Date → repair_date&nbsp;&nbsp;&nbsp;&nbsp;" +
            "Repair Fee → repair_fee<br>" +
            "&nbsp;Fee Due Date → fee_due_date&nbsp;&nbsp;&nbsp;&nbsp;" +
            "Extra Details → extra_details" +
            "</html>"
        );
        lblMapping.setFont(lblMapping.getFont().deriveFont(12f));
        
        // 선호 크기 얻기
        Dimension pref = lblMapping.getPreferredSize();
        int desiredWidth = 500; // 최소 800px 이상 확보
            
        // 선호 크기(preferredSize)
        lblMapping.setPreferredSize(new Dimension(desiredWidth, pref.height));
        // 최소 크기(minimumSize)
        lblMapping.setMinimumSize(new Dimension(desiredWidth, pref.height));
        // 최대 크기(maximumSize)
        lblMapping.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
        // 왼쪽 정렬 (BoxLayout 에서 가로 전체폭을 사용하도록)
        lblMapping.setAlignmentX(Component.CENTER_ALIGNMENT);
            
        content.add(lblMapping);
        content.add(Box.createVerticalStrut(12));

        // 2) “조건식” 입력 폼
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        condPanel.setBorder(
            BorderFactory.createTitledBorder(
                "조건식 (예: repair_fee < 50000 AND company_id = 2)"
            )
        );
        condPanel.add(tfCondition);
        tfCondition.setToolTipText("예: repair_fee < 50000 AND company_id = 2");
        content.add(condPanel);
        content.add(Box.createVerticalStrut(8));

        // 3) 버튼 패널
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDelete = new JButton("삭제");
        JButton btnCancel = new JButton("취소");
        btnPanel.add(btnDelete);
        btnPanel.add(btnCancel);

        btnDelete.addActionListener(e -> onDelete());
        btnCancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnDelete);

        content.add(btnPanel);
    }

    /** “삭제” 버튼 클릭 시 호출 */
    private void onDelete() {
        String condition = tfCondition.getText().trim();
        if (condition.isEmpty()) {
            DialogUtil.showWarning(this, "삭제할 조건식을 입력하세요.");
            return;
        }
        String upper = condition.toUpperCase();
        if (upper.contains(";") || upper.contains("DROP ")
         || upper.contains("INSERT ") || upper.contains("UPDATE ")
         || upper.contains("DELETE ")) {
            DialogUtil.showWarning(this, "조건식에 위험 키워드가 포함되어 있습니다.");
            return;
        }

        try {
            // ▶ deleteByCondition(...) 호출, Map<테이블명, 삭제건수> 리턴
            Map<String, Integer> resultMap = dao.deleteByCondition(condition);

            if (resultMap.isEmpty()) {
                DialogUtil.showWarning(this, "조건에 맞는 레코드가 없습니다.");
            } else {
                // ▶ Map 내용(“external_maintenance”: 건수)을 사용자에게 보여줌
                StringBuilder msg = new StringBuilder();
                msg.append("삭제가 완료되었습니다:\n\n");
                for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
                    msg.append("- ").append(entry.getKey())
                       .append(": ").append(entry.getValue()).append("건\n");
                }
                DialogUtil.showInfo(this, msg.toString());
                onSuccessRefresh.run();
                dispose();
            }
        } catch (SQLException ex) {
            DialogUtil.showError(this, "삭제 중 오류 발생:\n" + ex.getMessage());
        }
    }
}
