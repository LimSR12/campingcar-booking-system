package admin.view;

import admin.dao.CampingCarDao;
import admin.dao.CompanyDao;
import global.entity.Company;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * camping_car 테이블의 “조건식 기반 삭제” 다이얼로그
 * - 삭제할 WHERE 조건식만 입력받음
 * - 삭제 후 부모 패널(tableView) 새로고침
 */
public class CampingCarDelete extends JDialog {
    private final CampingCarDao campingCarDao;
    private final Runnable     onSuccessRefresh;

    // 단일 조건식 입력용 텍스트필드
    private final JTextField tfCondition = new JTextField(30);

    public CampingCarDelete(Frame parent,
                            CampingCarDao campingCarDao,
                            Runnable onSuccessRefresh) {
        super(parent, "조건식에 해당하는 튜플 삭제하기", true);
        this.campingCarDao    = campingCarDao;
        this.onSuccessRefresh = onSuccessRefresh;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        // 메인 패널: 세로(BoxLayout.Y_AXIS)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setContentPane(content);

        // ──────────────────────────────────────────────
        // 1) “한글→실제칼럼” 매핑 안내 (JLabel)
        // ──────────────────────────────────────────────
        JLabel lblMapping = new JLabel(
                "<html>" +
                "&nbsp;<b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br>" +
                "&nbsp;회사 → company_id&nbsp;&nbsp;&nbsp;" +
                "이름 → name&nbsp;&nbsp;&nbsp;" +
                "번호판 → plate_number<br>" +
                "&nbsp;정원 → capacity&nbsp;&nbsp;&nbsp;" +
                "가격 → rental_price&nbsp;&nbsp;&nbsp;" +
                "이미지 경로 → image<br>" +
                "&nbsp;세부설명 → detail_info&nbsp;&nbsp;&nbsp;" +
                "등록일시 → registration_date" +
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

        // ──────────────────────────────────────────────
        // 2) “조건식” 입력 폼 (단일 JTextField)
        // ──────────────────────────────────────────────
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        condPanel.setBorder(
            BorderFactory.createTitledBorder("조건식 (예: rental_price < 50000 AND capacity >= 4)")
        );
        condPanel.add(tfCondition, FlowLayout.LEFT);
        tfCondition.setToolTipText("예: rental_price < 50000 AND capacity >= 4");

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
        // 1) 조건식 비어 있으면 경고
        String condition = tfCondition.getText().trim();
        if (condition.isEmpty()) {
            DialogUtil.showWarning(this, "삭제할 조건식을 입력하세요.");
            return;
        }
        // 2) 간단 검증 (세미콜론, DROP 등 위험 키워드 포함 여부)
        String upper = condition.toUpperCase();
        if (upper.contains(";") || upper.contains("DROP ")
         || upper.contains("INSERT ") || upper.contains("UPDATE ")
         || upper.contains("DELETE ")) {
            DialogUtil.showWarning(this, "조건식에 SQL 위험 키워드가 포함되어 있습니다.");
            return;
        }

        // 3) DAO 호출: deleteByCondition
        try {
            // ▶ “자식→손자→부모” 순서로 삭제하며, 테이블별 삭제 건수를 반환
            Map<String, Integer> resultMap = campingCarDao.deleteByCondition(condition);

            if (resultMap.isEmpty()) {
                DialogUtil.showWarning(this, "조건에 맞는 레코드가 없습니다.");
            } else {
                // ▶ Map 내용을 읽어 사용자에게 보여 줍니다.
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
