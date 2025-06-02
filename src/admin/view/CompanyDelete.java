package admin.view;

import admin.dao.CompanyDao;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * company 테이블의 “조건식 기반 삭제” 다이얼로그
 */
public class CompanyDelete extends JDialog {
    private final CompanyDao companyDao;
    private final Runnable   onSuccessRefresh;

    // 단일 조건식 입력용 텍스트필드
    private final JTextField tfCondition = new JTextField(30);

    public CompanyDelete(Frame parent,
                         CompanyDao companyDao,
                         Runnable onSuccessRefresh) {
        super(parent, "조건식에 해당하는 company 행 삭제하기", true);
        this.companyDao       = companyDao;
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

        // ──────────────────────────────────────────────
        // 1) “한글→영문칼럼명” 매핑 안내 레이블
        // ──────────────────────────────────────────────
        JLabel lblMapping = new JLabel(
            "<html>" +
            "&nbsp;<b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br>" +
            "&nbsp;이름 → name&nbsp;&nbsp;&nbsp;&nbsp;주소 → address&nbsp;&nbsp;&nbsp;&nbsp;전화번호 → phone<br>" +
            "&nbsp;담당자 → manager_name&nbsp;&nbsp;&nbsp;&nbsp;담당자 이메일 → manager_email" +
            "</html>"
        );
        lblMapping.setFont(lblMapping.getFont().deriveFont(12f));
        Dimension pref = lblMapping.getPreferredSize();
        int desiredWidth = 600;
        lblMapping.setPreferredSize(new Dimension(desiredWidth, pref.height));
        lblMapping.setMinimumSize(new Dimension(desiredWidth, pref.height));
        lblMapping.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
        lblMapping.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lblMapping);
        content.add(Box.createVerticalStrut(12));

        // ──────────────────────────────────────────────
        // 2) “조건식” 입력 폼
        // ──────────────────────────────────────────────
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        condPanel.setBorder(
            BorderFactory.createTitledBorder("조건식 (예: phone LIKE '02-%' AND name = '서울캠핑렌트')")
        );
        condPanel.add(tfCondition, FlowLayout.LEFT);
        tfCondition.setToolTipText("예: phone LIKE '02-%' AND name = '서울캠핑렌트'");
        content.add(condPanel);
        content.add(Box.createVerticalStrut(8));

        // ──────────────────────────────────────────────
        // 3) 버튼 패널
        // ──────────────────────────────────────────────
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
            DialogUtil.showWarning(this, "조건식에 SQL 위험 키워드가 포함되어 있습니다.");
            return;
        }

        try {
            // ▶ “상세 삭제” 메서드를 호출하여, 테이블별 삭제 건수 Map 받기
            Map<String, Integer> resultMap = companyDao.deleteByCondition(condition);

            if (resultMap.isEmpty()) {
                DialogUtil.showWarning(this, "조건에 맞는 레코드가 없습니다.");
            } else {
                // ▶ Map 내용을 읽어 사용자에게 보여 줌
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
