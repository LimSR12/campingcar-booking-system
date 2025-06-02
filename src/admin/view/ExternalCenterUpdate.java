package admin.view;

import admin.dao.ExternalCenterDao;
import global.entity.ExternalCenter;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * external_center 테이블의 “조건식 기반 수정” 다이얼로그
 * ───────────────────────────────────────────────────────
 * - 모든 컬럼(name, address, phone, manager_name, manager_email) 수정 입력란
 * - 하단에 조건식 빌더(WHERE 절) 입력란
 * - BoxLayout(Y_AXIS) 형태로 간단하게 배치
 */
public class ExternalCenterUpdate extends JDialog {
    private final ExternalCenterDao dao;
    private final Runnable          onSuccessRefresh;

    // --- 수정할 컬럼 입력 컴포넌트 ---
    private final JTextField tfName         = new JTextField(25);
    private final JTextField tfAddress      = new JTextField(30);
    private final JTextField tfPhone        = new JTextField(20);
    private final JTextField tfManagerName  = new JTextField(20);
    private final JTextField tfManagerEmail = new JTextField(25);

    // --- 조건식 입력용 텍스트필드 단일 사용 ---
    private final JTextField tfCondition = new JTextField(30);

    public ExternalCenterUpdate(Frame parent,
                                ExternalCenterDao dao,
                                Runnable onSuccessRefresh) {
        super(parent, "조건식에 해당하는 external_center 수정하기", true);
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

        // 1) “한글→영문컬럼명 안내” (JLabel)
        JLabel lblMapping = new JLabel(
            "<html>" +
            "&nbsp;<b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br>" +
            "&nbsp;이름 → name&nbsp;&nbsp;&nbsp;&nbsp;" +
            "주소 → address&nbsp;&nbsp;&nbsp;&nbsp;" +
            "전화번호 → phone<br>" +
            "&nbsp;담당자 → manager_name&nbsp;&nbsp;&nbsp;&nbsp;" +
            "담당자 이메일 → manager_email" +
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

        // ───────────────────────────────────────────────────
        // 2) “수정할 컬럼” 입력 폼 (GridBagLayout 으로 2열 배치)
        // ───────────────────────────────────────────────────
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("수정할 컬럼 (기본키 제외)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // (1) 이름
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("이름:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfName, gbc);

        // (2) 주소
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("주소:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfAddress, gbc);

        // (3) 전화번호
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("전화번호:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfPhone, gbc);

        // (4) 담당자 이름
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("담당자:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfManagerName, gbc);

        // (5) 담당자 이메일
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("담당자 이메일:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfManagerEmail, gbc);

        content.add(editPanel);
        content.add(Box.createVerticalStrut(8));

        // ───────────────────────────────────────────────────
        // 3) “조건식” 입력 폼
        // ───────────────────────────────────────────────────
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        condPanel.setBorder(
            BorderFactory.createTitledBorder("조건식 (예: phone LIKE '02-%' AND name = 'ABC')")
        );
        condPanel.add(tfCondition);
        tfCondition.setToolTipText("예: phone LIKE '02-%' AND name = '서울정비센터'");

        content.add(condPanel);
        content.add(Box.createVerticalStrut(8));

        // ───────────────────────────────────────────────────
        // 4) 버튼 패널
        // ───────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExecute = new JButton("실행");
        JButton btnCancel  = new JButton("취소");
        btnPanel.add(btnExecute);
        btnPanel.add(btnCancel);
        btnExecute.addActionListener(e -> onExecute());
        btnCancel .addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnExecute);

        content.add(btnPanel);
    }

    /** “실행” 버튼 클릭 시 호출 */
    private void onExecute() {
        // 1) 수정할 컬럼들 수집
        Map<String, Object> newValues = new LinkedHashMap<>();

        String nameVal = tfName.getText().trim();
        if (!nameVal.isEmpty()) {
            newValues.put("name", nameVal);
        }
        String addrVal = tfAddress.getText().trim();
        if (!addrVal.isEmpty()) {
            newValues.put("address", addrVal);
        }
        String phoneVal = tfPhone.getText().trim();
        if (!phoneVal.isEmpty()) {
            newValues.put("phone", phoneVal);
        }
        String mgrNameVal = tfManagerName.getText().trim();
        if (!mgrNameVal.isEmpty()) {
            newValues.put("manager_name", mgrNameVal);
        }
        String mgrEmailVal = tfManagerEmail.getText().trim();
        if (!mgrEmailVal.isEmpty()) {
            newValues.put("manager_email", mgrEmailVal);
        }

        if (newValues.isEmpty()) {
            DialogUtil.showWarning(this, "수정할 컬럼을 최소 한 개 이상 입력하세요.");
            return;
        }

        // 2) 조건식
        String condition = tfCondition.getText().trim();
        if (condition.isEmpty()) {
            DialogUtil.showWarning(this, "조건식을 입력하세요.");
            return;
        }
        String upper = condition.toUpperCase();
        if (upper.contains(";") || upper.contains("DROP ") || upper.contains("DELETE ")
         || upper.contains("INSERT ") || upper.contains("UPDATE ")) {
            DialogUtil.showWarning(this, "조건식에 위험 키워드가 포함되어 있습니다.");
            return;
        }

        // 3) DAO 호출
        try {
            int count = dao.updateByCondition(newValues, condition);
            if (count > 0) {
                DialogUtil.showInfo(this, "성공적으로 " + count + "건 수정되었습니다.");
                onSuccessRefresh.run();
                dispose();
            } else {
                DialogUtil.showWarning(this, "조건에 맞는 레코드가 없습니다.");
            }
        } catch (SQLException ex) {
            DialogUtil.showError(this, "수정 중 오류 발생:\n" + ex.getMessage());
        }
    }
}
