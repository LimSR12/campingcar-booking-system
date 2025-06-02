package admin.view;

import admin.dao.CompanyDao;
import global.entity.Company;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CompanyPanel extends AbstractTableCRUDPanel<Company> {
    private JTextField tfName, tfAddress, tfPhone, tfManagerName, tfManagerEmail;

    public CompanyPanel() {
        super(new CompanyDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0,2,6,6));

        tfName = new JTextField();
        p.add(new JLabel("이름")); p.add(tfName);

        tfAddress = new JTextField();
        p.add(new JLabel("주소")); p.add(tfAddress);

        tfPhone = new JTextField();
        p.add(new JLabel("전화")); p.add(tfPhone);

        tfManagerName = new JTextField();
        p.add(new JLabel("관리자 이름")); p.add(tfManagerName);

        tfManagerEmail = new JTextField();
        p.add(new JLabel("관리자 이메일")); p.add(tfManagerEmail);

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
        tfName.setText("");
        tfAddress.setText("");
        tfPhone.setText("");
        tfManagerName.setText("");
        tfManagerEmail.setText("");
        cards.show(cardPane, "FORM");
    }

    @Override
    protected void saveForm() {
    	// 회사 이름 빈칸 확인
        if (tfName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "회사 이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfName.requestFocus(); return;
        }
        // 회사 주소 빈칸 확인
        if (tfAddress.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "회사 주소를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfAddress.requestFocus(); return;
        }
        // 회사 번호 빈칸 확인
        if (tfPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "회사 번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPhone.requestFocus(); return;
        }
        // 관리자 이름 빈칸 확인
        if (tfManagerName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "관리자 이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfManagerName.requestFocus(); return;
        }
        // 관리자 이메일 빈칸 확인
        if (tfManagerEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "관리자 이메일을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfManagerEmail.requestFocus(); return;
        }
        
        try {
            Company c = new Company();
            c.setName(tfName.getText().trim());
            c.setAddress(tfAddress.getText().trim());
            c.setPhone(tfPhone.getText().trim());
            c.setManagerName(tfManagerName.getText().trim());
            c.setManagerEmail(tfManagerEmail.getText().trim());
            dao.insert(c);
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
        showView();
    }

	@Override
	protected void openUpdateByConditionDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CompanyDao dao = (CompanyDao) super.dao;
        CompanyUpdate dlg = new CompanyUpdate(parentFrame, dao, this::refreshTable);
        dlg.setVisible(true);
	}

	@Override
	protected void openDeleteByConditionDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CompanyDao dao = (CompanyDao) super.dao;
        CompanyDelete dlg = new CompanyDelete(parentFrame, dao, this::refreshTable);
        dlg.setVisible(true);
	}
}