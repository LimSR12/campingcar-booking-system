package admin.view;

import admin.dao.CustomerDao;
import global.entity.Customer;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * customer 테이블의 “조건식 기반 수정” 다이얼로그
 */
public class CustomerUpdate extends JDialog {
    private final CustomerDao customerDao;
    private final Runnable    onSuccessRefresh;

    // --- 수정할 컬럼 입력용 컴포넌트들 ---
    private final JTextField tfUsername      = new JTextField(20);
    private final JTextField tfPassword      = new JTextField(20);
    private final JTextField tfLicenseNumber = new JTextField(20);
    private final JTextField tfName          = new JTextField(20);
    private final JTextField tfAddress       = new JTextField(30);
    private final JTextField tfPhone         = new JTextField(20);
    private final JTextField tfEmail         = new JTextField(25);
    private final JSpinner   spPrevReturn    = new JSpinner(new SpinnerDateModel());
    private final JTextField tfPrevCarType   = new JTextField(20);

    // --- WHERE 절 조건식 입력용 ---
    private final JTextField tfCondition = new JTextField(30);

    public CustomerUpdate(Frame parent,
                          CustomerDao customerDao,
                          Runnable onSuccessRefresh) {
        super(parent, "조건식에 해당하는 customer 수정하기", true);
        this.customerDao       = customerDao;
        this.onSuccessRefresh  = onSuccessRefresh;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        // 메인 컨테이너: BoxLayout(Y_AXIS)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setContentPane(content);

        // ────────────────────────────────────────────────────
        // 1) “한글→영문 칼럼명” 안내 레이블
        // ────────────────────────────────────────────────────
        JLabel lblMapping = new JLabel(
            "<html>" +
            "&nbsp;<b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br>" +
            "&nbsp;사용자 아이디 → username&nbsp;&nbsp;&nbsp;" +
            "비밀번호 → password&nbsp;&nbsp;&nbsp;" +
            "&nbsp;면허번호 → license_number&nbsp;&nbsp;&nbsp;<br>" +
            "이름 → name&nbsp;&nbsp;&nbsp;" +
            "&nbsp;주소 → address&nbsp;&nbsp;&nbsp;&nbsp;" +
            "전화번호 → phone&nbsp;&nbsp;&nbsp;<br>" +
            "&nbsp;이메일 → email&nbsp;&nbsp;&nbsp;&nbsp;" +
            "이전 반납일 → prev_return_date&nbsp;&nbsp;&nbsp;<br>" +
            "&nbsp;이전 자동차 종류 → prev_car_type" +
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

        // ────────────────────────────────────────────────────
        // 2) “수정할 컬럼” 입력 패널
        // ────────────────────────────────────────────────────
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("수정할 컬럼 (기본키 ID 제외)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // ① username
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("사용자 ID (username):"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfUsername, gbc);

        // ② password
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("비밀번호 (password):"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfPassword, gbc);

        // ③ license_number
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("면허번호 (license_number):"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfLicenseNumber, gbc);

        // ④ name
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("이름 (name):"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfName, gbc);

        // ⑤ address
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("주소 (address):"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfAddress, gbc);

        // ⑥ phone
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("전화번호 (phone):"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfPhone, gbc);

        // ⑦ email
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("이메일 (email):"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfEmail, gbc);

        // ⑧ prev_return_date (Date + Time)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("이전 반납일 (prev_return_date):"), gbc);
        gbc.gridx = 1;
        spPrevReturn.setEditor(
            new JSpinner.DateEditor(spPrevReturn, "yyyy-MM-dd HH:mm:ss")
        );
        editPanel.add(spPrevReturn, gbc);

        // ⑨ prev_car_type
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("이전 자동차 종류 (prev_car_type):"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfPrevCarType, gbc);

        content.add(editPanel);
        content.add(Box.createVerticalStrut(8));

        // ────────────────────────────────────────────────────
        // 3) “조건식” 입력 패널
        // ────────────────────────────────────────────────────
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        condPanel.setBorder(BorderFactory.createTitledBorder(
            "조건식 (예: license_number = 'DL2025-1001' AND phone LIKE '010-%')"
        ));
        condPanel.add(tfCondition);
        tfCondition.setToolTipText("예: license_number = 'DL2025-1001' AND phone LIKE '010-%'");
        content.add(condPanel);
        content.add(Box.createVerticalStrut(8));

        // ────────────────────────────────────────────────────
        // 4) 버튼 패널
        // ────────────────────────────────────────────────────
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

    /** “실행” 클릭 시 호출 */
    private void onExecute() {
        // 1) 수정할 값 수집 (Map<컬럼명, 값>)
        Map<String, Object> newValues = new LinkedHashMap<>();

        String userIdVal = tfUsername.getText().trim();
        if (!userIdVal.isEmpty()) {
            newValues.put("username", userIdVal);
        }

        String pwdVal = tfPassword.getText().trim();
        if (!pwdVal.isEmpty()) {
            newValues.put("password", pwdVal);
        }

        String licVal = tfLicenseNumber.getText().trim();
        if (!licVal.isEmpty()) {
            newValues.put("license_number", licVal);
        }

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

        String emailVal = tfEmail.getText().trim();
        if (!emailVal.isEmpty()) {
            newValues.put("email", emailVal);
        }

        Object prevReturnObj = spPrevReturn.getValue();
        if (prevReturnObj instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String prevReturnStr = sdf.format((Date) prevReturnObj);
            newValues.put("prev_return_date", prevReturnStr);
        }

        String prevCarVal = tfPrevCarType.getText().trim();
        if (!prevCarVal.isEmpty()) {
            newValues.put("prev_car_type", prevCarVal);
        }

        if (newValues.isEmpty()) {
            DialogUtil.showWarning(this, "최소 한 개 이상의 컬럼을 입력해야 합니다.");
            return;
        }

        // 2) WHERE 절(조건식) 입력 체크
        String condition = tfCondition.getText().trim();
        if (condition.isEmpty()) {
            DialogUtil.showWarning(this, "조건식을 입력하세요.");
            return;
        }
        // 간단 검증: 세미콜론, DROP, DELETE 등
        String upper = condition.toUpperCase();
        if (upper.contains(";") || upper.contains("DROP ")
                || upper.contains("DELETE ") || upper.contains("INSERT ")
                || upper.contains("UPDATE ")) {
            DialogUtil.showWarning(this, "조건식에 안전하지 않은 SQL 키워드가 포함되어 있습니다.");
            return;
        }

        // 3) 실제 DAO 호출
        try {
            int count = customerDao.updateByCondition(newValues, condition);
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
