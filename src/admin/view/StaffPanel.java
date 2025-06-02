package admin.view;

import admin.dao.StaffDao;
import global.entity.Staff;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * staff 전용 CRUD 패널
 */
public class StaffPanel extends AbstractTableCRUDPanel<Staff> {
    private JTextField tfName;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JTextField tfSalary;
    private JSpinner spFamilyNum;
    private JTextField tfDepartment;
    
    private JComboBox<String> cbRole;

    public StaffPanel() {
        super(new StaffDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));

        // 이름 (VARCHAR NOT NULL)
        tfName = new JTextField();
        p.add(new JLabel("이름"));
        p.add(tfName);

        // 전화 (VARCHAR NOT NULL)
        tfPhone = new JTextField();
        p.add(new JLabel("전화"));
        p.add(tfPhone);

        // 주소 (VARCHAR NOT NULL)
        tfAddress = new JTextField();
        p.add(new JLabel("주소"));
        p.add(tfAddress);

        // 급여 (DECIMAL >= 0)
        tfSalary = new JTextField();
        p.add(new JLabel("급여"));
        p.add(tfSalary);

        // 가족 수 (INT >= 0)
        spFamilyNum = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        p.add(new JLabel("가족 수"));
        p.add(spFamilyNum);

        // 부서 (VARCHAR NOT NULL)
        tfDepartment = new JTextField();
        p.add(new JLabel("부서"));
        p.add(tfDepartment);

        // 역할 (3가지 중 선택)
        cbRole = new JComboBox<>(new String[] { "정비", "사무", "관리" });
        p.add(new JLabel("역할"));
        p.add(cbRole);

        // 버튼 (저장/취소)
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

    @Override
    protected void clearAndShowForm() {
        tfName.setText("");
        tfPhone.setText("");
        tfAddress.setText("");
        tfSalary.setText("");
        spFamilyNum.setValue(0);
        tfDepartment.setText("");
        if (cbRole != null && cbRole.getItemCount() > 0) {
            cbRole.setSelectedIndex(0);
        }
        cards.show(cardPane, "FORM");
    }

    @Override
    protected void saveForm() {
        // 이름
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfName.requestFocus();
            return;
        }

        // 전화
        String phone = tfPhone.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "전화번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPhone.requestFocus();
            return;
        }

        // 주소
        String address = tfAddress.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "주소를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfAddress.requestFocus();
            return;
        }

        // 급여 (>= 0)
        String salaryText = tfSalary.getText().trim();
        double salary;
        if (salaryText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "급여를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfSalary.requestFocus();
            return;
        }
        try {
            salary = Double.parseDouble(salaryText);
            if (salary < 0) {
                JOptionPane.showMessageDialog(this, "급여는 0 이상이어야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                tfSalary.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "급여는 숫자로만 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfSalary.requestFocus();
            return;
        }

        // 가족 수 (>= 0)
        int familyNum = (Integer) spFamilyNum.getValue();
        if (familyNum < 0) {
            JOptionPane.showMessageDialog(this, "가족 수는 0 이상이어야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            spFamilyNum.requestFocus();
            return;
        }

        // 부서
        String department = tfDepartment.getText().trim();
        if (department.isEmpty()) {
            JOptionPane.showMessageDialog(this, "부서를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfDepartment.requestFocus();
            return;
        }

        // 역할 (콤보박스 선택)
        String role = (String) cbRole.getSelectedItem();
        if (role == null || role.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "역할을 선택하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            cbRole.requestFocus();
            return;
        }

        // DTO 생성 및 DAO.insert()
        try {
            Staff s = new Staff();
            s.setName(name);
            s.setPhone(phone);
            s.setAddress(address);
            s.setSalary((int)salary);
            s.setFamilyNum(familyNum);
            s.setDepartment(department);
            s.setRole(role);

            dao.insert(s);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "저장 실패:\n" + ex.getMessage());
            return;
        }

        showView();
    }
}
