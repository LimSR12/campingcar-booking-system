package admin.view;

import admin.dao.ExternalMaintenanceDao;
import admin.dao.CampingCarDao;
import admin.dao.ExternalCenterDao;
import admin.dao.CustomerDao;
import admin.dao.CompanyDao;
import global.entity.CampingCar;
import global.entity.ExternalCenter;
import global.entity.Customer;
import global.entity.Company;
import global.util.DialogUtil;

import javax.swing.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * external_maintenance 테이블의 “조건식 기반 수정” 다이얼로그
 *  - 모든 컬럼(기본키 제외) 수정 입력란
 *  - 하단에 조건식 빌더(WHERE 절) 입력란
 *  - BoxLayout(Y_AXIS) 형태로 세로 배치
 */
public class ExternalMaintenanceUpdate extends JDialog {
    private final ExternalMaintenanceDao dao;
    private final Runnable               onSuccessRefresh;

    // --- 수정할 컬럼 입력 컴포넌트 ---
    private final JComboBox<CampingCar>    cbCar        = new JComboBox<>();
    private final JComboBox<ExternalCenter> cbCenter    = new JComboBox<>();
    private final JComboBox<Customer>      cbCustomer  = new JComboBox<>();
    private final JTextField               tfLicense   = new JTextField(20);
    private final JComboBox<Company>       cbCompany   = new JComboBox<>();
    private final JTextArea                taRepairDetails = new JTextArea(3, 30);
    private final JSpinner                 spRepairDate    = new JSpinner(new SpinnerDateModel());
    private final JTextField               tfRepairFee     = new JTextField(10);
    private final JSpinner                 spFeeDueDate    = new JSpinner(new SpinnerDateModel());
    private final JTextArea                taExtraDetails  = new JTextArea(2, 30);

    // --- 조건식 입력용 텍스트필드 ---
    private final JTextField tfCondition = new JTextField(30);

    public ExternalMaintenanceUpdate(Frame parent,
                                     ExternalMaintenanceDao dao,
                                     Runnable onSuccessRefresh) {
        super(parent, "조건식에 해당하는 external_maintenance 수정하기", true);
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

        // 1) “한글→영문컬럼명 안내” 라벨
        JLabel lblMapping = new JLabel(
            "<html>" +
            "&nbsp;<b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br>" +
            "&nbsp;Car ID → car_id&nbsp;&nbsp;&nbsp;" +
            "Center ID → center_id&nbsp;&nbsp;&nbsp;<br>" +
            "&nbsp;Customer ID → customer_id&nbsp;&nbsp;&nbsp;" +
            "License No → license_number<br>" +
            "&nbsp;Company ID → company_id&nbsp;&nbsp;&nbsp;" +
            "Repair Details → repair_details<br>" +
            "&nbsp;Repair Date → repair_date&nbsp;&nbsp;&nbsp;" +
            "Repair Fee → repair_fee<br>" +
            "&nbsp;Fee Due Date → fee_due_date&nbsp;&nbsp;&nbsp;" +
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

        // ───────────────────────────────────────────────────
        // 2) “수정할 컬럼” 패널 (GridBagLayout 으로 2열 구성)
        // ───────────────────────────────────────────────────
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("수정할 컬럼 (기본키 제외)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // ① Car ID (외래키 → JComboBox)
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Car ID:"), gbc);
        gbc.gridx = 1;
        editPanel.add(cbCar, gbc);

        // ② Center ID (외래키 → JComboBox)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Center ID:"), gbc);
        gbc.gridx = 1;
        editPanel.add(cbCenter, gbc);

        // ③ Customer ID (외래키 → JComboBox)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1;
        editPanel.add(cbCustomer, gbc);

        // ④ License Number
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("License No:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfLicense, gbc);

        // ⑤ Company ID (외래키 → JComboBox)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Company ID:"), gbc);
        gbc.gridx = 1;
        editPanel.add(cbCompany, gbc);

        // ⑥ Repair Details (JTextArea)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        editPanel.add(new JLabel("Repair Details:"), gbc);
        gbc.gridx = 1;
        JScrollPane spRepair = new JScrollPane(taRepairDetails);
        taRepairDetails.setLineWrap(true);
        taRepairDetails.setWrapStyleWord(true);
        spRepair.setPreferredSize(new Dimension(300, 60));
        editPanel.add(spRepair, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        // ⑦ Repair Date (SpinnerDateModel)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Repair Date:"), gbc);
        gbc.gridx = 1;
        spRepairDate.setEditor(new JSpinner.DateEditor(spRepairDate, "yyyy-MM-dd HH:mm:ss"));
        editPanel.add(spRepairDate, gbc);

        // ⑧ Repair Fee
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Repair Fee:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfRepairFee, gbc);

        // ⑨ Fee Due Date (SpinnerDateModel → DATE Only)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("Fee Due Date:"), gbc);
        gbc.gridx = 1;
        spFeeDueDate.setEditor(new JSpinner.DateEditor(spFeeDueDate, "yyyy-MM-dd"));
        editPanel.add(spFeeDueDate, gbc);

        // ⑩ Extra Details (JTextArea)
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        editPanel.add(new JLabel("Extra Details:"), gbc);
        gbc.gridx = 1;
        JScrollPane spExtra = new JScrollPane(taExtraDetails);
        taExtraDetails.setLineWrap(true);
        taExtraDetails.setWrapStyleWord(true);
        spExtra.setPreferredSize(new Dimension(300, 50));
        editPanel.add(spExtra, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        content.add(editPanel);
        content.add(Box.createVerticalStrut(10));

        // ───────────────────────────────────────────────────
        // 3) “조건식” 입력 폼 (단일 JTextField)
        // ───────────────────────────────────────────────────
        JPanel condPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.LEFT, 6, 6));
        condPanel.setBorder(BorderFactory.createTitledBorder(
            "조건식 (예: repair_fee < 50000 AND company_id = 2)"
        ));
        condPanel.add(tfCondition);
        tfCondition.setToolTipText("예: repair_fee < 50000 AND company_id = 2");
        content.add(condPanel);
        content.add(Box.createVerticalStrut(10));

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

        // ───────────────────────────────────────────────────
        // 5) 초기화: 각 FK용 JComboBox 로드
        // ───────────────────────────────────────────────────
        reloadCarList();
        reloadCenterList();
        reloadCustomerList();
        reloadCompanyList();
    }

    /** CampingCar JComboBox를 다시 로드 */
    private void reloadCarList() {
        try {
            List<CampingCar> cars = new CampingCarDao().findAll();
            DefaultComboBoxModel<CampingCar> model = new DefaultComboBoxModel<>();
            for (CampingCar c : cars) {
                model.addElement(c);
            }
            cbCar.setModel(model);
            cbCar.setRenderer((JList<? extends CampingCar> list, CampingCar value, int idx, boolean isSelected, boolean cellHasFocus) -> {
                String text = value == null ? "" : (value.getId() + ": " + value.getName());
                return new JLabel(text);
            });
        } catch (SQLException ex) {
            DialogUtil.showError(this, "Car 목록 조회 오류:\n" + ex.getMessage());
        }
    }

    /** ExternalCenter JComboBox를 다시 로드 */
    private void reloadCenterList() {
        try {
            List<ExternalCenter> centers = new ExternalCenterDao().findAll();
            DefaultComboBoxModel<ExternalCenter> model = new DefaultComboBoxModel<>();
            for (ExternalCenter c : centers) {
                model.addElement(c);
            }
            cbCenter.setModel(model);
            cbCenter.setRenderer((JList<? extends ExternalCenter> list, ExternalCenter value, int idx, boolean isSelected, boolean cellHasFocus) -> {
                String text = value == null ? "" : (value.getId() + ": " + value.getName());
                return new JLabel(text);
            });
        } catch (SQLException ex) {
            DialogUtil.showError(this, "Center 목록 조회 오류:\n" + ex.getMessage());
        }
    }

    /** Customer JComboBox를 다시 로드 */
    private void reloadCustomerList() {
        try {
            List<Customer> custs = new CustomerDao().findAll();
            DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
            for (Customer c : custs) {
                model.addElement(c);
            }
            cbCustomer.setModel(model);
            cbCustomer.setRenderer((JList<? extends Customer> list, Customer value, int idx, boolean isSelected, boolean cellHasFocus) -> {
                String text = value == null ? "" : (value.getId() + ": " + value.getName());
                return new JLabel(text);
            });
        } catch (SQLException ex) {
            DialogUtil.showError(this, "Customer 목록 조회 오류:\n" + ex.getMessage());
        }
    }

    /** Company JComboBox를 다시 로드 */
    private void reloadCompanyList() {
        try {
            List<Company> comps = new CompanyDao().findAll();
            DefaultComboBoxModel<Company> model = new DefaultComboBoxModel<>();
            for (Company c : comps) {
                model.addElement(c);
            }
            cbCompany.setModel(model);
            cbCompany.setRenderer((JList<? extends Company> list, Company value, int idx, boolean isSelected, boolean cellHasFocus) -> {
                String text = value == null ? "" : (value.getId() + ": " + value.getName());
                return new JLabel(text);
            });
        } catch (SQLException ex) {
            DialogUtil.showError(this, "Company 목록 조회 오류:\n" + ex.getMessage());
        }
    }

    /** “실행” 클릭 시 호출 */
    private void onExecute() {
        // 1) 수정할 컬럼들 수집
        Map<String, Object> newValues = new LinkedHashMap<>();

        // (1) Car ID
        CampingCar selCar = (CampingCar) cbCar.getSelectedItem();
        if (selCar != null) {
            newValues.put("car_id", selCar.getId());
        }

        // (2) Center ID
        ExternalCenter selCenter = (ExternalCenter) cbCenter.getSelectedItem();
        if (selCenter != null) {
            newValues.put("center_id", selCenter.getId());
        }

        // (3) Customer ID
        Customer selCust = (Customer) cbCustomer.getSelectedItem();
        if (selCust != null) {
            newValues.put("customer_id", selCust.getId());
        }

        // (4) License Number
        String licVal = tfLicense.getText().trim();
        if (!licVal.isEmpty()) {
            newValues.put("license_number", licVal);
        }

        // (5) Company ID
        Company selComp = (Company) cbCompany.getSelectedItem();
        if (selComp != null) {
            newValues.put("company_id", selComp.getId());
        }

        // (6) Repair Details
        String rdVal = taRepairDetails.getText().trim();
        if (!rdVal.isEmpty()) {
            newValues.put("repair_details", rdVal);
        }

        // (7) Repair Date
        Object rdObj = spRepairDate.getValue();
        if (rdObj instanceof java.util.Date) {
            LocalDateTime ldt = new java.sql.Timestamp(((java.util.Date) rdObj).getTime())
                                 .toLocalDateTime();
            newValues.put("repair_date", ldt);
        }

        // (8) Repair Fee
        String feeText = tfRepairFee.getText().trim();
        if (!feeText.isEmpty()) {
            try {
                double feeVal = Double.parseDouble(feeText);
                newValues.put("repair_fee", feeVal);
            } catch (NumberFormatException ex) {
                DialogUtil.showWarning(this, "Repair Fee는 숫자로만 입력해야 합니다.");
                tfRepairFee.requestFocus();
                return;
            }
        }

        // (9) Fee Due Date
        Object fddObj = spFeeDueDate.getValue();
        if (fddObj instanceof java.util.Date) {
            LocalDate ld = ((java.util.Date) fddObj).toInstant()
                              .atZone(java.time.ZoneId.systemDefault())
                              .toLocalDate();
            newValues.put("fee_due_date", ld);
        }

        // (10) Extra Details
        String edVal = taExtraDetails.getText().trim();
        if (!edVal.isEmpty()) {
            newValues.put("extra_details", edVal);
        }

        if (newValues.isEmpty()) {
            DialogUtil.showWarning(this, "수정할 컬럼을 최소 한 개 이상 입력하세요.");
            return;
        }

        // 2) 조건식 검증
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

        // 3) DAO 호출: updateByCondition
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
