package admin.view;

import admin.dao.ExternalCenterDao;
import global.entity.ExternalCenter;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ExternalCenterPanel extends AbstractTableCRUDPanel<ExternalCenter> {
    private JTextField tfName, tfAddress, tfPhone, tfManagerName, tfManagerEmail;

    public ExternalCenterPanel() {
        super(new ExternalCenterDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        tfName = new JTextField(); p.add(new JLabel("이름")); p.add(tfName);
        tfAddress = new JTextField(); p.add(new JLabel("주소")); p.add(tfAddress);
        tfPhone = new JTextField(); p.add(new JLabel("전화")); p.add(tfPhone);
        tfManagerName = new JTextField(); p.add(new JLabel("관리자 이름")); p.add(tfManagerName);
        tfManagerEmail = new JTextField(); p.add(new JLabel("관리자 이메일")); p.add(tfManagerEmail);
        JPanel btns = new JPanel();
        JButton save = new JButton("저장"), cancel = new JButton("취소");
        save.addActionListener(e -> saveForm()); cancel.addActionListener(e -> showView());
        btns.add(save); btns.add(cancel);
        p.add(new JLabel()); p.add(btns);
        return p;
    }

    @Override
    protected void clearAndShowForm() {
        tfName.setText(""); tfAddress.setText(""); tfPhone.setText("");
        tfManagerName.setText(""); tfManagerEmail.setText("");
        cards.show(cardPane, "FORM");
    }

    @Override
    protected void saveForm() {
        if (tfName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfName.requestFocus(); return;
        }
        if (tfAddress.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "주소를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfAddress.requestFocus(); return;
        }
        if (tfPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "전화번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPhone.requestFocus(); return;
        }
        if (tfManagerName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "관리자 이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfManagerName.requestFocus(); return;
        }
        if (tfManagerEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "관리자 번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfManagerEmail.requestFocus(); return;
        }
        try {
            ExternalCenter ec = new ExternalCenter();
            ec.setName(tfName.getText().trim());
            ec.setAddress(tfAddress.getText().trim());
            ec.setPhone(tfPhone.getText().trim());
            ec.setManagerName(tfManagerName.getText().trim());
            ec.setManagerEmail(tfManagerEmail.getText().trim());
            dao.insert(ec);
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
        showView();
    }
}