package admin.view;

import admin.dao.CompanyDao;
import global.entity.Company;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * company 테이블의 “조건식 기반 수정” 다이얼로그
 */
public class CompanyUpdate extends JDialog {
    private final CompanyDao   companyDao;
    private final Runnable     onSuccessRefresh;

    // --- 수정할 컬럼 입력 컴포넌트 ---
    private final JTextField tfName         = new JTextField(25);
    private final JTextField tfAddress      = new JTextField(25);
    private final JTextField tfPhone        = new JTextField(20);
    private final JTextField tfManagerName  = new JTextField(20);
    private final JTextField tfManagerEmail = new JTextField(20);

    // --- WHERE 조건식 입력용 ---
    private final JTextField tfCondition = new JTextField(30);

    public CompanyUpdate(Frame parent,
                         CompanyDao companyDao,
                         Runnable onSuccessRefresh) {
        super(parent, "조건식에 해당하는 company 행 수정하기", true);
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
        // 1) “한글→영문칼럼명” 안내 레이블
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
        // 2) “수정할 컬럼” 입력 폼 (GridBagLayout)
        // ──────────────────────────────────────────────
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("수정할 컬럼 (기본키 제외)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // ① 이름
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("이름:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfName, gbc);

        // ② 주소
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("주소:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfAddress, gbc);

        // ③ 전화번호
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("전화번호:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfPhone, gbc);

        // ④ 담당자 이름
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("담당자 이름:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfManagerName, gbc);

        // ⑤ 담당자 이메일
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("담당자 이메일:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfManagerEmail, gbc);

        content.add(editPanel);
        content.add(Box.createVerticalStrut(8));

        // ──────────────────────────────────────────────
        // 3) “조건식” 입력 패널
        // ──────────────────────────────────────────────
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        condPanel.setBorder(
            BorderFactory.createTitledBorder("조건식 (예: name = '서울캠핑렌트' AND phone LIKE '02-%')")
        );
        condPanel.add(tfCondition, FlowLayout.LEFT);
        tfCondition.setToolTipText("예: name = '서울캠핑렌트' AND phone LIKE '02-%'");
        content.add(condPanel);
        content.add(Box.createVerticalStrut(8));

        // ──────────────────────────────────────────────
        // 4) 버튼 패널
        // ──────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnUpdate = new JButton("수정");
        JButton btnCancel = new JButton("취소");
        btnPanel.add(btnUpdate);
        btnPanel.add(btnCancel);

        btnUpdate.addActionListener(e -> onUpdate());
        btnCancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnUpdate);

        content.add(btnPanel);
    }

    /** “수정” 버튼 클릭 시 호출 */
    private void onUpdate() {
        // 1) 변경할 컬럼값 수집
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

        // 2) WHERE 조건식
        String condition = tfCondition.getText().trim();
        if (condition.isEmpty()) {
            DialogUtil.showWarning(this, "조건식을 입력하세요.");
            return;
        }
        String upper = condition.toUpperCase();
        if (upper.contains(";") || upper.contains("DROP ")
         || upper.contains("DELETE ") || upper.contains("INSERT ")
         || upper.contains("UPDATE ")) {
            DialogUtil.showWarning(this, "조건식에 SQL 위험 키워드가 포함되어 있습니다.");
            return;
        }

        try {
            int count = companyDao.updateByCondition(newValues, condition);
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
