package admin.view;

import admin.dao.CampingCarDao;
import admin.dao.InternalMaintenanceDao;
import admin.dao.PartInventoryDao;
import admin.dao.StaffDao;
import global.entity.CampingCar;
import global.entity.InternalMaintenance;
import global.entity.PartInventory;
import global.entity.Staff;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class InternalMaintenancePanel extends AbstractTableCRUDPanel<InternalMaintenance> {
    // 외래키 콤보박스
    private JComboBox<CampingCar>    cbCar;
    private JComboBox<PartInventory> cbPart;
    private JComboBox<Staff>         cbStaff;

    // 기타 입력 컴포넌트
    private JSpinner   spRepairDate;
    private JTextField tfDuration;
    private JTextArea  taDescription;

    public InternalMaintenancePanel() {
        super(new InternalMaintenanceDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));

        // ----------------------------------------------------
        // camping_car 콤보박스 (외래키: car_id)
        // ----------------------------------------------------
        cbCar = new JComboBox<>();
        cbCar.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            // 예시: "ID | 이름 | 번호판"
            return new JLabel(value.getId() + " | " + value.getName() + " | " + value.getPlateNumber());
        });
        p.add(new JLabel("캠핑카"));
        p.add(cbCar);

        // ----------------------------------------------------
        // part_inventory 콤보박스 (외래키: part_id)
        // ----------------------------------------------------
        cbPart = new JComboBox<>();
        cbPart.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            // 예시: "ID | 부품명 | 재고"
            return new JLabel(
                value.getId() + " | " + value.getName() + " | 수량:" + value.getQuantity()
            );
        });
        p.add(new JLabel("부품"));
        p.add(cbPart);

        // ----------------------------------------------------
        // staff 콤보박스 (외래키: staff_id)
        // ----------------------------------------------------
        cbStaff = new JComboBox<>();
        cbStaff.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            // 예시: "ID | 이름 | 직책"
            return new JLabel(value.getId() + " | " + value.getName() + " | " + value.getRole());
        });
        p.add(new JLabel("담당 직원"));
        p.add(cbStaff);

        // ----------------------------------------------------
        // repair_date (DATETIME)
        // ----------------------------------------------------
        spRepairDate = new JSpinner(new SpinnerDateModel());
        spRepairDate.setEditor(new JSpinner.DateEditor(spRepairDate, "yyyy-MM-dd HH:mm:ss"));
        p.add(new JLabel("수리 날짜"));
        p.add(spRepairDate);

        // ----------------------------------------------------
        // duration (INT >= 0)
        // ----------------------------------------------------
        tfDuration = new JTextField();
        p.add(new JLabel("수리 시간(분)"));
        p.add(tfDuration);

        // ----------------------------------------------------
        // description (TEXT NULL 허용)
        // ----------------------------------------------------
        taDescription = new JTextArea(3, 20);
        JScrollPane spDescScroll = new JScrollPane(taDescription);
        p.add(new JLabel("설명"));
        p.add(spDescScroll);

        // ----------------------------------------------------
        // 버튼 (저장/취소)
        // ----------------------------------------------------
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("저장");
        JButton cancel = new JButton("취소");
        save.addActionListener(e -> saveForm());
        cancel.addActionListener(e -> showView());
        btns.add(save);
        btns.add(cancel);

        p.add(new JLabel());
        p.add(btns);

        return p;
    }

    /**
     * 외래키 콤보박스들(camping_car, part_inventory, staff)을
     * 최신 데이터베이스 상태로 매번 갱신합니다.
     */
    private void reloadForeignKeyLists() {
        // camping_car 목록 로드
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

        // part_inventory 목록 로드
        try {
            List<PartInventory> parts = new PartInventoryDao().findAll();
            DefaultComboBoxModel<PartInventory> partModel = new DefaultComboBoxModel<>();
            for (PartInventory p : parts) {
                partModel.addElement(p);
            }
            cbPart.setModel(partModel);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "부품 목록 로드 실패:\n" + ex.getMessage());
        }

        // staff 목록 로드
        try {
            List<Staff> staffs = new StaffDao().findAll();
            DefaultComboBoxModel<Staff> staffModel = new DefaultComboBoxModel<>();
            for (Staff s : staffs) {
                staffModel.addElement(s);
            }
            cbStaff.setModel(staffModel);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "직원 목록 로드 실패:\n" + ex.getMessage());
        }
    }

    @Override
    protected void clearAndShowForm() {
        // 외래키 콤보박스 최신화
        reloadForeignKeyLists();

        // 나머지 입력 필드 초기화
        spRepairDate.setValue(new Date());
        tfDuration.setText("");
        taDescription.setText("");

        // 콤보박스 첫 번째 항목(있는 경우) 선택
        if (cbCar.getItemCount() > 0)       cbCar.setSelectedIndex(0);
        if (cbPart.getItemCount() > 0)      cbPart.setSelectedIndex(0);
        if (cbStaff.getItemCount() > 0)     cbStaff.setSelectedIndex(0);

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
        // 부품 선택
        PartInventory selPart = (PartInventory) cbPart.getSelectedItem();
        if (selPart == null) {
            JOptionPane.showMessageDialog(this, "부품을 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbPart.requestFocus();
            return;
        }
        // 직원 선택
        Staff selStaff = (Staff) cbStaff.getSelectedItem();
        if (selStaff == null) {
            JOptionPane.showMessageDialog(this, "직원을 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbStaff.requestFocus();
            return;
        }

        // 수리 날짜 (NULL이 될 수 없으므로 JSpinner에서 자동 제공)
        Date repairDateValue = (Date) spRepairDate.getValue();
        if (repairDateValue == null) {
            JOptionPane.showMessageDialog(this, "수리 날짜를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            spRepairDate.requestFocus();
            return;
        }

        // duration (INT >= 0)
        String durText = tfDuration.getText().trim();
        int duration;
        if (durText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "수리 시간을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfDuration.requestFocus();
            return;
        }
        try {
            duration = Integer.parseInt(durText);
            if (duration < 0) {
                JOptionPane.showMessageDialog(this, "수리 시간은 0 이상이어야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                tfDuration.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "수리 시간은 정수만 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfDuration.requestFocus();
            return;
        }

        // description (NULL 허용)
        String desc = taDescription.getText().trim();
        // 빈 문자열도 허용

        // ----------------------------------------------------
        // DTO(InternalMaintenance) 생성 및 DAO.insert() 호출
        // ----------------------------------------------------
        try {
            InternalMaintenance im = new InternalMaintenance();

            // 외래키 세팅
            im.setCarId(selCar.getId());
            im.setPartId(selPart.getId());
            im.setStaffId(selStaff.getId());

            // repair_date: java.util.Date → java.sql.Timestamp
            Timestamp tsRepair = new Timestamp(repairDateValue.getTime());
            im.setRepairDate(tsRepair.toLocalDateTime());

            // duration, description
            im.setDuration(duration);
            im.setDescription(desc.isEmpty() ? null : desc);

            // INSERT 실행
            dao.insert(im);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "저장 실패:\n" + ex.getMessage());
            return;
        }

        // 저장 성공 후 목록 화면으로 전환
        showView();
    }
}