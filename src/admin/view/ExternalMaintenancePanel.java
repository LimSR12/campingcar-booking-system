package admin.view;

import admin.dao.CampingCarDao;
import admin.dao.CompanyDao;
import admin.dao.CustomerDao;
import admin.dao.ExternalCenterDao;
import admin.dao.ExternalMaintenanceDao;

import global.entity.CampingCar;
import global.entity.Company;
import global.entity.Customer;
import global.entity.ExternalCenter;
import global.entity.ExternalMaintenance;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ExternalMaintenancePanel extends AbstractTableCRUDPanel<ExternalMaintenance> {
    // 외래키용 콤보박스들
    private JComboBox<CampingCar>    cbCar;      // camping_car
    private JComboBox<ExternalCenter> cbCenter;  // external_center
    private JComboBox<Customer>      cbCustomer; // customer
    private JComboBox<Company>       cbCompany;  // company

    // 나머지 입력 컴포넌트
    private JTextField taRepairDetails, taExtraDetails, tfRepairFee;
    private JSpinner spRepairDate, spFeeDueDate;

    public ExternalMaintenancePanel() {
        super(new ExternalMaintenanceDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));

        // camping_car 콤보박스
        cbCar = new JComboBox<>();
        // 렌더러: 예를 들어 "ID | 이름 | 번호판" 형태
        cbCar.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            return new JLabel(
                value.getId() + " | " + value.getName() + " | " + value.getPlateNumber()
            );
        });
        p.add(new JLabel("캠핑카"));
        p.add(cbCar);

        // external_center 콤보박스
        cbCenter = new JComboBox<>();
        // 렌더러: "ID | 센터명"
        cbCenter.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            return new JLabel(value.getId() + " | " + value.getName());
        });
        p.add(new JLabel("외부센터"));
        p.add(cbCenter);

        // customer 콤보박스 (고객 선택 → 면허번호 자동 연동)
        cbCustomer = new JComboBox<>();
        // 렌더러: "면허번호 | 이름(ID)"
        cbCustomer.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            return new JLabel(
                value.getLicenseNumber() + " | " + value.getName() + " (ID:" + value.getId() + ")"
            );
        });
        p.add(new JLabel("고객"));
        p.add(cbCustomer);

        // company 콤보박스
        cbCompany = new JComboBox<>();
        // 렌더러: "회사명 (ID)"
        cbCompany.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            return new JLabel(value.getName() + " (ID:" + value.getId() + ")");
        });
        p.add(new JLabel("회사"));
        p.add(cbCompany);

        // 수리 내역
        taRepairDetails = new JTextField();
        JScrollPane spRepairDetailsScroll = new JScrollPane(taRepairDetails);
        p.add(new JLabel("수리 내역"));
        p.add(spRepairDetailsScroll);

        // 수리 날짜
        spRepairDate = new JSpinner(new SpinnerDateModel());
        spRepairDate.setEditor(new JSpinner.DateEditor(spRepairDate, "yyyy-MM-dd HH:mm:ss"));
        p.add(new JLabel("수리 날짜"));
        p.add(spRepairDate);

        // 수리 비용 (TEXTFIELD → double)
        tfRepairFee = new JTextField();
        p.add(new JLabel("수리 비용"));
        p.add(tfRepairFee);

        // 8) 비용 납부일
        spFeeDueDate = new JSpinner(new SpinnerDateModel());
        spFeeDueDate.setEditor(new JSpinner.DateEditor(spFeeDueDate, "yyyy-MM-dd"));
        p.add(new JLabel("비용 납부일"));
        p.add(spFeeDueDate);

        // 9) 추가 세부사항
        taExtraDetails = new JTextField();
        JScrollPane spExtraDetailsScroll = new JScrollPane(taExtraDetails);
        p.add(new JLabel("추가 세부사항"));
        p.add(spExtraDetailsScroll);

        // ---------------------------------------
        // 버튼 (저장 / 취소)
        // ---------------------------------------
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("저장");
        JButton cancel = new JButton("취소");
        save.addActionListener(e -> saveForm());
        cancel.addActionListener(e -> showView());
        btns.add(save);
        btns.add(cancel);

        // 레이아웃 맞추기: 마지막 칸에 버튼 배치
        p.add(new JLabel());
        p.add(btns);

        return p;
    }

    /**
     * 모든 콤보박스를 최신 상태로 다시 로드
     * - camping_car, external_center, customer, company 테이블로부터 각각 findAll()
     */
    private void reloadAllForeignLists() {
        // camping_car 목록
        try {
            List<CampingCar> cars = new CampingCarDao().findAll();
            DefaultComboBoxModel<CampingCar> carModel = new DefaultComboBoxModel<>();
            for (CampingCar c : cars) {
                carModel.addElement(c);
            }
            cbCar.setModel(carModel);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "캠핑카 목록 로드 실패:\n" + ex.getMessage());
        }

        // external_center 목록
        try {
            List<ExternalCenter> centers = new ExternalCenterDao().findAll();
            DefaultComboBoxModel<ExternalCenter> centerModel = new DefaultComboBoxModel<>();
            for (ExternalCenter c : centers) {
                centerModel.addElement(c);
            }
            cbCenter.setModel(centerModel);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "외부센터 목록 로드 실패:\n" + ex.getMessage());
        }

        // customer 목록
        try {
            List<Customer> customers = new CustomerDao().findAll();
            DefaultComboBoxModel<Customer> custModel = new DefaultComboBoxModel<>();
            for (Customer c : customers) {
            	// 관리자는 제외
            	if("관리자".equals(c.getName())) continue;
            	custModel.addElement(c);
            }
            cbCustomer.setModel(custModel);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "고객 목록 로드 실패:\n" + ex.getMessage());
        }

        // company 목록
        try {
            List<Company> companies = new CompanyDao().findAll();
            DefaultComboBoxModel<Company> compModel = new DefaultComboBoxModel<>();
            for (Company c : companies) {
                compModel.addElement(c);
            }
            cbCompany.setModel(compModel);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "회사 목록 로드 실패:\n" + ex.getMessage());
        }
    }

    @Override
    protected void clearAndShowForm() {
        // ① 폼을 열기 직전에 외래키 콤보박스들을 모두 로드
        reloadAllForeignLists();

        // ② 나머지 입력 컴포넌트 초기화
        taRepairDetails.setText("");
        spRepairDate.setValue(new Date());
        tfRepairFee.setText("");
        spFeeDueDate.setValue(new Date());
        taExtraDetails.setText("");

        // ③ 각 콤보박스를 첫 번째 항목(있으면)으로 선택
        if (cbCar.getItemCount() > 0)        cbCar.setSelectedIndex(0);
        if (cbCenter.getItemCount() > 0)     cbCenter.setSelectedIndex(0);
        if (cbCustomer.getItemCount() > 0)   cbCustomer.setSelectedIndex(0);
        if (cbCompany.getItemCount() > 0)    cbCompany.setSelectedIndex(0);

        // ④ FORM 카드로 전환
        cards.show(cardPane, "FORM");
    }

    @Override
    protected void saveForm() {
        // --- 필수 입력값 검증 ---
        if (cbCar.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "캠핑카를 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbCar.requestFocus(); return;
        }
        if (cbCenter.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "외부센터를 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbCenter.requestFocus(); return;
        }
        if (cbCustomer.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "고객을 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbCustomer.requestFocus(); return;
        }
        if (cbCompany.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "회사를 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbCompany.requestFocus(); return;
        }
        if (taRepairDetails.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "수리 내역을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            taRepairDetails.requestFocus(); return;
        }
        String feeText = tfRepairFee.getText().trim();
        if (feeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "수리 비용을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfRepairFee.requestFocus(); return;
        }
        double repairPrice;
        try {
            repairPrice = Double.parseDouble(feeText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "수리 비용은 숫자만 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfRepairFee.requestFocus(); return;
        }
        if (repairPrice <= 0) {
            JOptionPane.showMessageDialog(this, "수리 비용은 0보다 커야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfRepairFee.requestFocus(); return;
        }

        // --- DAO를 통해 ExternalMaintenance 엔티티 생성/저장 ---
        try {
            ExternalMaintenance e = new ExternalMaintenance();

            // 캠핑카 ID
            CampingCar selectedCar = (CampingCar) cbCar.getSelectedItem();
            e.setCarId(selectedCar.getId());

            // 센터 ID
            ExternalCenter selectedCenter = (ExternalCenter) cbCenter.getSelectedItem();
            e.setCenterId(selectedCenter.getId());

            // 고객 ID + 면허번호
            Customer selectedCustomer = (Customer) cbCustomer.getSelectedItem();
            e.setCustomerId(selectedCustomer.getId());
            e.setLicenseNumber(selectedCustomer.getLicenseNumber());

            // 회사 ID
            Company selectedCompany = (Company) cbCompany.getSelectedItem();
            e.setCompanyId(selectedCompany.getId());

            // 수리 내역
            e.setRepairDetails(taRepairDetails.getText().trim());

            // 수리 날짜 → LocalDateTime 변환
            Date repairDateValue = (Date) spRepairDate.getValue();
            LocalDateTime ldt = repairDateValue.toInstant()
                                               .atZone(ZoneId.systemDefault())
                                               .toLocalDateTime();
            e.setRepairDate(ldt);

            // 수리 비용
            e.setRepairFee(repairPrice);

            // 비용 납부일 → LocalDate 변환
            Date feeDueDateValue = (Date) spFeeDueDate.getValue();
            LocalDate ld = feeDueDateValue.toInstant()
                                           .atZone(ZoneId.systemDefault())
                                           .toLocalDate();
            e.setFeeDueDate(ld);

            // 추가 세부사항
            e.setExtraDetails(taExtraDetails.getText().trim());

            // INSERT 실행
            dao.insert(e);

        } catch (SQLException ex) {
            DialogUtil.showError(this, "저장 실패:\n" + ex.getMessage());
            return;
        }

        // --- 저장 성공 후 목록 화면으로 돌아가기 ---
        showView();
    }
}
