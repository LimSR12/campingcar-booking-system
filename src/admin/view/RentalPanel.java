// src/admin/view/RentalPanel.java
package admin.view;

import admin.dao.CampingCarDao;
import admin.dao.CompanyDao;
import admin.dao.CustomerDao;
import admin.dao.PartInventoryDao;
import admin.dao.RentalDao;
import global.entity.CampingCar;
import global.entity.Company;
import global.entity.Customer;
import global.entity.Rental;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class RentalPanel extends AbstractTableCRUDPanel<Rental> {
    // 1) 외래키용 콤보박스
    private JComboBox<CampingCar> cbCar;
    private JComboBox<Customer> cbCustomer;
    private JComboBox<Company> cbCompany;

    // 2) 추가 입력 컴포넌트
    private JSpinner spStartDate;
    private JSpinner spReturnDate;
    private JTextField tfRentalDays;
    private JTextField tfRentalFee;
    private JSpinner spFeeDueDate;
    private JTextArea taExtraDetails;
    private JTextField tfExtraFee;

    public RentalPanel() {
        super(new RentalDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));

        // ----------------------------------------------------
        // 1) camping_car 콤보박스 (외래키 car_id)
        // ----------------------------------------------------
        cbCar = new JComboBox<>();
        cbCar.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            // 예: "ID | 이름 | 번호판"
            return new JLabel(value.getId() + " | " + value.getName() + " | " + value.getPlateNumber());
        });
        p.add(new JLabel("캠핑카"));
        p.add(cbCar);

        // ----------------------------------------------------
        // 2) customer 콤보박스 (외래키 customer_id & license_number 자동 설정)
        // ----------------------------------------------------
        cbCustomer = new JComboBox<>();
        cbCustomer.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            // 예: "면허번호 | 이름 (ID)"
            return new JLabel(
                value.getLicenseNumber() + " | " + value.getName() + " (ID:" + value.getId() + ")"
            );
        });
        p.add(new JLabel("고객"));
        p.add(cbCustomer);

        // ----------------------------------------------------
        // 3) company 콤보박스 (외래키 company_id)
        // ----------------------------------------------------
        cbCompany = new JComboBox<>();
        cbCompany.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            // 예: "회사명 (ID)"
            return new JLabel(value.getName() + " (ID:" + value.getId() + ")");
        });
        p.add(new JLabel("회사"));
        p.add(cbCompany);

        // ----------------------------------------------------
        // 4) start_date (DATETIME)
        // ----------------------------------------------------
        spStartDate = new JSpinner(new SpinnerDateModel());
        spStartDate.setEditor(new JSpinner.DateEditor(spStartDate, "yyyy-MM-dd HH:mm:ss"));
        p.add(new JLabel("대여 시작"));
        p.add(spStartDate);

        // ----------------------------------------------------
        // 5) return_date (DATETIME)
        // ----------------------------------------------------
        spReturnDate = new JSpinner(new SpinnerDateModel());
        spReturnDate.setEditor(new JSpinner.DateEditor(spReturnDate, "yyyy-MM-dd HH:mm:ss"));
        p.add(new JLabel("반납 예정"));
        p.add(spReturnDate);

        // ----------------------------------------------------
        // 6) rental_days (INT > 0)
        // ----------------------------------------------------
        tfRentalDays = new JTextField();
        p.add(new JLabel("대여 일수"));
        p.add(tfRentalDays);

        // ----------------------------------------------------
        // 7) rental_fee (DECIMAL >= 0)
        // ----------------------------------------------------
        tfRentalFee = new JTextField();
        p.add(new JLabel("대여 비용"));
        p.add(tfRentalFee);

        // ----------------------------------------------------
        // 8) fee_due_date (DATETIME)
        // ----------------------------------------------------
        spFeeDueDate = new JSpinner(new SpinnerDateModel());
        spFeeDueDate.setEditor(new JSpinner.DateEditor(spFeeDueDate, "yyyy-MM-dd HH:mm:ss"));
        p.add(new JLabel("비용 납부일"));
        p.add(spFeeDueDate);

        // ----------------------------------------------------
        // 9) extra_details (TEXT NULL 가능)
        // ----------------------------------------------------
        taExtraDetails = new JTextArea(2, 20);
        JScrollPane spExtraDetailsScroll = new JScrollPane(taExtraDetails);
        p.add(new JLabel("추가 정보"));
        p.add(spExtraDetailsScroll);

        // ----------------------------------------------------
        // 10) extra_fee (DECIMAL NULL 가능)
        // ----------------------------------------------------
        tfExtraFee = new JTextField();
        p.add(new JLabel("추가 비용"));
        p.add(tfExtraFee);

        // ----------------------------------------------------
        // 버튼(저장/취소)
        // ----------------------------------------------------
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("저장");
        JButton cancel = new JButton("취소");
        save.addActionListener(e -> saveForm());
        cancel.addActionListener(e -> showView());
        btns.add(save);
        btns.add(cancel);

        p.add(new JLabel()); // 빈 라벨로 그리드 맞추기
        p.add(btns);

        return p;
    }

    /**
     * 모든 외래키 콤보박스를 최신 상태로 다시 로드
     * - camping_car, customer, company 테이블에서 각각 findAll()
     */
    private void reloadForeignKeyLists() {
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

        // customer 목록
        try {
            List<Customer> customers = new CustomerDao().findAll();
            DefaultComboBoxModel<Customer> custModel = new DefaultComboBoxModel<>();
            for (Customer c : customers) {
                // 예: “관리자” 같은 특별한 고객을 제외하려면 여기서 필터링 가능
                if (!"관리자".equals(c.getName())) {
                    custModel.addElement(c);
                }
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
        // 외래키 콤보박스들을 먼저 최신화
        reloadForeignKeyLists();

        // 나머지 입력 컴포넌트 초기화
        spStartDate.setValue(new Date());
        spReturnDate.setValue(new Date());
        tfRentalDays.setText("");
        tfRentalFee.setText("");
        spFeeDueDate.setValue(new Date());
        taExtraDetails.setText("");
        tfExtraFee.setText("");

        // 콤보박스 첫 항목 선택
        if (cbCar.getItemCount() > 0)       cbCar.setSelectedIndex(0);
        if (cbCustomer.getItemCount() > 0)  cbCustomer.setSelectedIndex(0);
        if (cbCompany.getItemCount() > 0)   cbCompany.setSelectedIndex(0);

        // FORM 카드로 전환
        cards.show(cardPane, "FORM");
    }

    @Override
    protected void saveForm() {
        // ----------------------------------------------------
        // 필수 입력값 검증
        // ----------------------------------------------------
        // 캠핑카 선택
        CampingCar selCar = (CampingCar) cbCar.getSelectedItem();
        if (selCar == null) {
            JOptionPane.showMessageDialog(this, "캠핑카를 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbCar.requestFocus();
            return;
        }
        // 고객 선택
        Customer selCust = (Customer) cbCustomer.getSelectedItem();
        if (selCust == null) {
            JOptionPane.showMessageDialog(this, "고객을 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbCustomer.requestFocus();
            return;
        }
        // 회사 선택
        Company selComp = (Company) cbCompany.getSelectedItem();
        if (selComp == null) {
            JOptionPane.showMessageDialog(this, "회사를 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbCompany.requestFocus();
            return;
        }

        // 날짜 검증: start_date, return_date
        Date startDateValue  = (Date) spStartDate.getValue();
        Date returnDateValue = (Date) spReturnDate.getValue();
        if (returnDateValue.before(startDateValue)) {
            JOptionPane.showMessageDialog(this,
                "반납 예정일은 대여 시작일 이후여야 합니다.",
                "입력 오류",
                JOptionPane.ERROR_MESSAGE);
            spReturnDate.requestFocus();
            return;
        }

        // 대여 일수 입력 및 검증 (> 0)
        String daysText = tfRentalDays.getText().trim();
        int rentalDays;
        if (daysText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "대여 일수를 입력하세요.",
                "입력 오류",
                JOptionPane.ERROR_MESSAGE);
            tfRentalDays.requestFocus();
            return;
        }
        try {
            rentalDays = Integer.parseInt(daysText);
            if (rentalDays <= 0) {
                JOptionPane.showMessageDialog(this,
                    "대여 일수는 1 이상이어야 합니다.",
                    "입력 오류",
                    JOptionPane.ERROR_MESSAGE);
                tfRentalDays.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "대여 일수는 정수만 입력해야 합니다.",
                "입력 오류",
                JOptionPane.ERROR_MESSAGE);
            tfRentalDays.requestFocus();
            return;
        }

        // 날짜 차이를 통해 “실제 대여 일수” 계산
        // 텍스트로 입력된 rentalDays와 비교하여 일치 여부를 검사
        LocalDateTime ldtStart  = startDateValue.toInstant()
                                               .atZone(ZoneId.systemDefault())
                                               .toLocalDateTime();
        LocalDateTime ldtReturn = returnDateValue.toInstant()
                                                 .atZone(ZoneId.systemDefault())
                                                 .toLocalDateTime();
        long diffMillis = Duration.between(ldtStart, ldtReturn).toMillis();
        // 하루(24시간) = 86,400,000 밀리초
        int actualDays = (int) (diffMillis / (1000L * 60 * 60 * 24));
        if (diffMillis % (1000L * 60 * 60 * 24) != 0) {
            // 만약 시간이 딱 맞지 않는다면, 넘치는 부분을 하루로 간주하여 +1
            actualDays += 1;
        }
        if (actualDays != rentalDays) {
            JOptionPane.showMessageDialog(this,
                "대여 시작일(" + new SimpleDateFormat("yyyy-MM-dd").format(startDateValue) + ")과\n"
                + "반납 예정일(" + new SimpleDateFormat("yyyy-MM-dd").format(returnDateValue) + ")의 차이는 "
                + actualDays + "일 입니다.\n"
                + "입력하신 대여 일수(" + rentalDays + "일)와 일치시키세요.",
                "입력 오류",
                JOptionPane.ERROR_MESSAGE);
            tfRentalDays.requestFocus();
            return;
        }

        // 대여 비용 (>= 0)
        String feeText = tfRentalFee.getText().trim();
        double rentalFee;
        if (feeText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "대여 비용을 입력하세요.",
                "입력 오류",
                JOptionPane.ERROR_MESSAGE);
            tfRentalFee.requestFocus();
            return;
        }
        try {
            rentalFee = Double.parseDouble(feeText);
            if (rentalFee < 0) {
                JOptionPane.showMessageDialog(this,
                    "대여 비용은 0 이상이어야 합니다.",
                    "입력 오류",
                    JOptionPane.ERROR_MESSAGE);
                tfRentalFee.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "대여 비용은 숫자로만 입력해야 합니다.",
                "입력 오류",
                JOptionPane.ERROR_MESSAGE);
            tfRentalFee.requestFocus();
            return;
        }

        // 비용 납부일 검증
        Date feeDueDateValue = (Date) spFeeDueDate.getValue();
        if (feeDueDateValue.before(startDateValue)) {
            JOptionPane.showMessageDialog(this,
                "비용 납부일은 대여 시작일 이후여야 합니다.",
                "입력 오류",
                JOptionPane.ERROR_MESSAGE);
            spFeeDueDate.requestFocus();
            return;
        }

        // 추가 비용 (NULL 허용, 입력 시 >= 0)
        String extraFeeText = tfExtraFee.getText().trim();
        Double extraFee = null;
        if (!extraFeeText.isEmpty()) {
            try {
                extraFee = Double.parseDouble(extraFeeText);
                if (extraFee < 0) {
                    JOptionPane.showMessageDialog(this,
                        "추가 비용은 0 이상이어야 합니다.",
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
                    tfExtraFee.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "추가 비용은 숫자로만 입력해야 합니다.",
                    "입력 오류",
                    JOptionPane.ERROR_MESSAGE);
                tfExtraFee.requestFocus();
                return;
            }
        }

        // DTO(Rental) 생성 및 DAO.insert() 호출
        try {
            Rental r = new Rental();

            // 외래키 설정
            r.setCarId(selCar.getId());
            r.setCustomerId(selCust.getId());
            r.setLicenseNumber(selCust.getLicenseNumber()); // 고객의 면허번호를 자동으로 사용
            r.setCompanyId(selComp.getId());

            // 날짜/시간 설정 (LocalDateTime)
            r.setStartDate(ldtStart);
            r.setReturnDate(ldtReturn);

            // rental_days, rental_fee
            r.setRentalDays(rentalDays);
            r.setRentalFee(rentalFee);

            // 비용 납부일 (LocalDateTime)
            LocalDateTime ldtFeeDue = feeDueDateValue.toInstant()
                                                     .atZone(ZoneId.systemDefault())
                                                     .toLocalDateTime();
            r.setFeeDueDate(ldtFeeDue);

            // 추가 정보
            r.setExtraDetails(taExtraDetails.getText().trim());
            r.setExtraFee(extraFee);

            // INSERT 실행
            dao.insert(r);
        } catch (SQLException ex) {
            DialogUtil.showError(this,
                "저장 실패:\n" + ex.getMessage());
            return;
        }

        // 저장 성공 후 목록 화면으로 돌아가기
        showView();
    }
    
	@Override
	protected void openUpdateByConditionDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        RentalDao eDao = (RentalDao) super.dao;
        RentalUpdate dlg = new RentalUpdate(parentFrame, eDao, this::refreshTable);
        dlg.setVisible(true);
	}

	@Override
	protected void openDeleteByConditionDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        RentalDao eDao = (RentalDao) super.dao;
        RentalDelete dlg = new RentalDelete(parentFrame, eDao, this::refreshTable);
        dlg.setVisible(true);
	}
}
