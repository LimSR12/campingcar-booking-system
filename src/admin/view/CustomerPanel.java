package admin.view;

import admin.dao.CustomerDao;
import global.entity.Customer;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomerPanel extends AbstractTableCRUDPanel<Customer> {
    private JTextField tfUsername, tfPassword, tfLicense, tfName, tfAddress, tfPhone, tfEmail;
    private JSpinner spPrevReturnDate;
    private JTextField tfPrevCarType;

    public CustomerPanel() {
        super(new CustomerDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0,2,6,6));

        tfUsername = new JTextField();
        p.add(new JLabel("아이디")); p.add(tfUsername);

        tfPassword = new JTextField();
        p.add(new JLabel("비밀번호")); p.add(tfPassword);

        tfLicense = new JTextField();
        p.add(new JLabel("면허번호")); p.add(tfLicense);

        tfName = new JTextField();
        p.add(new JLabel("이름")); p.add(tfName);

        tfAddress = new JTextField();
        p.add(new JLabel("주소")); p.add(tfAddress);

        tfPhone = new JTextField();
        p.add(new JLabel("전화")); p.add(tfPhone);

        tfEmail = new JTextField();
        p.add(new JLabel("이메일")); p.add(tfEmail);

        spPrevReturnDate = new JSpinner(new SpinnerDateModel());
        spPrevReturnDate.setEditor(new JSpinner.DateEditor(spPrevReturnDate, "yyyy-MM-dd HH:mm:ss"));
        p.add(new JLabel("이전 반납일")); p.add(spPrevReturnDate);

        tfPrevCarType = new JTextField();
        p.add(new JLabel("이전 차량 유형")); p.add(tfPrevCarType);

        JPanel btns = new JPanel();
        JButton save = new JButton("저장"), cancel = new JButton("취소");
        save.addActionListener(e -> saveForm());
        cancel.addActionListener(e -> showView());
        btns.add(save); btns.add(cancel);
        p.add(new JLabel()); p.add(btns);
        return p;
    }

    @Override
    protected void clearAndShowForm() {
        tfUsername.setText("");
        tfPassword.setText("");
        tfLicense.setText("");
        tfName.setText("");
        tfAddress.setText("");
        tfPhone.setText("");
        tfEmail.setText("");
        spPrevReturnDate.setValue(new Date());
        tfPrevCarType.setText("");
        cards.show(cardPane, "FORM");
    }

    @Override
    protected void saveForm() {
    	// 아이디 입력 빈칸 확인
        if (tfUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfUsername.requestFocus(); return;
        }
        // 비밀번호 입력 빈칸 확인
        if (tfPassword.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "패스워드를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPassword.requestFocus(); return;
        }
        // 면허번호 입력 빈칸 확인
        if (tfLicense.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "면허번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfLicense.requestFocus(); return;
        }
        // 이름 입력 빈칸 확인
        if (tfName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfName.requestFocus(); return;
        }
        // 주소 입력 빈칸 확인
        if (tfAddress.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "주소를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfAddress.requestFocus(); return;
        }
        // 전화번호 입력 빈칸 확인
        if (tfPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "전화번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPhone.requestFocus(); return;
        }
        // 이메일 입력 빈칸 확인
        if (tfEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "이메일을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfEmail.requestFocus(); return;
        }
        
        try {
        	
            // DAO를 이용해 중복 체크
            CustomerDao cDao = new CustomerDao();
            if (cDao.existsByLicense(tfLicense.getText().trim())) {
                JOptionPane.showMessageDialog(
                    this,
                    "이미 등록된 면허번호입니다. 다른 면허번호를 입력하세요.",
                    "중복 오류",
                    JOptionPane.ERROR_MESSAGE
                );
                tfLicense.requestFocus();
                return;
            }
            if (cDao.existsByPhone(tfPhone.getText().trim())) {
                JOptionPane.showMessageDialog(
                    this,
                    "이미 등록된 전화번호입니다. 다른 전화번호를 입력하세요.",
                    "중복 오류",
                    JOptionPane.ERROR_MESSAGE
                );
                tfPhone.requestFocus();
                return;
            }
            if (cDao.existsByEmail(tfEmail.getText().trim())) {
                JOptionPane.showMessageDialog(
                    this,
                    "이미 등록된 이메일입니다. 다른 이메일을 입력하세요.",
                    "중복 오류",
                    JOptionPane.ERROR_MESSAGE
                );
                tfEmail.requestFocus();
                return;
            }
        	
            Customer c = new Customer();
            c.setUsername(tfUsername.getText().trim());
            c.setPassword(tfPassword.getText().trim());
            c.setLicenseNumber(tfLicense.getText().trim());
            c.setName(tfName.getText().trim());
            c.setAddress(tfAddress.getText().trim());
            c.setPhone(tfPhone.getText().trim());
            c.setEmail(tfEmail.getText().trim());
            Date dt = (Date) spPrevReturnDate.getValue();
            c.setPrevReturnDate(new Timestamp(dt.getTime()));
            c.setPrevCarType(tfPrevCarType.getText().trim());
            dao.insert(c);
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
        showView();
    }

	@Override
	protected void openUpdateByConditionDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CustomerDao custDao = (CustomerDao) super.dao;
        CustomerUpdate dlg = new CustomerUpdate(parentFrame, custDao, this::refreshTable);
        dlg.setVisible(true);
	}

	@Override
	protected void openDeleteByConditionDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CustomerDao custDao = (CustomerDao) super.dao;
        CustomerDelete dlg = new CustomerDelete(parentFrame, custDao, this::refreshTable);
        dlg.setVisible(true);
	}
}