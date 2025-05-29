package admin.view;

import admin.dao.CampingCarDao;
import global.entity.CampingCar;
import global.entity.Company;
import global.util.DialogUtil;
import admin.dao.CompanyDao;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * camping_car 전용 CRUD 패널
 */
public class CampingCarPanel extends AbstractTableCRUDPanel<CampingCar> {
    private JComboBox<Company> cbCompany;
    private JTextField tfName, tfPlate, tfPrice, tfImg, taDetail;
    private JSpinner spCap, spRegDateTime;

    public CampingCarPanel() {
        super(new CampingCarDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0,2,6,6));

        // company_id → 회사명으로 드롭다운
        List<Company> comps = null;
		try {
			comps = new CompanyDao().findAll();
		} catch (SQLException e) {
			DialogUtil.showError(this, e.getMessage());
		}
        cbCompany = new JComboBox<>(comps.toArray(new Company[0]));
        cbCompany.setRenderer((i1, i2, i3, i4, i5) -> new JLabel(i2.getName()));
        p.add(new JLabel("회사"));
        p.add(cbCompany);

        Font tfFont = new Font("Dialog", Font.PLAIN, 14);
        tfName = new JTextField();
        p.add(new JLabel("이름"));
        p.add(tfName);
        
        tfPlate = new JTextField();
        p.add(new JLabel("번호판"));
        p.add(tfPlate);

        spCap = new JSpinner(new SpinnerNumberModel(4,1,20,1));
        p.add(new JLabel("정원"));
        p.add(spCap);

        tfPrice = new JTextField();
        p.add(new JLabel("가격"));
        p.add(tfPrice);
        
        tfImg = new JTextField();
        p.add(new JLabel("이미지"));
        p.add(tfImg);

        taDetail = new JTextField();
        p.add(new JLabel("세부설명"));
        p.add(taDetail);

        // 날짜 + 시간 통합 스피너 생성
        spRegDateTime = new JSpinner(new SpinnerDateModel());
        spRegDateTime.setEditor(
            new JSpinner.DateEditor(spRegDateTime, "yyyy-MM-dd HH:mm:ss")
        );
        p.add(new JLabel("등록일시"));
        p.add(spRegDateTime);

        JPanel btns = new JPanel();
        JButton save = new JButton("저장"), cancel = new JButton("취소");
        save.addActionListener(e -> saveForm());
        cancel.addActionListener(e -> showView());
        btns.add(save);
        btns.add(cancel);

        p.add(new JLabel());
        p.add(btns);
        
        return p;
    }

    // 입력 패널 초기 설정
    @Override
    protected void clearAndShowForm() {
        cbCompany.setSelectedIndex(0);
        tfName.setText("");
        tfPlate.setText("");
        spCap.setValue(4);
        tfPrice.setText("");
        tfImg.setText("");
        taDetail.setText("");
        spRegDateTime.setValue(new Date()); // 현재 시각으로
        
        cards.show(cardPane, "FORM");
        
        super.cards.show(super.cardPane, "FORM");
    }

    // 저장 버튼 누를 시
    @Override
    protected void saveForm() {
        if (tfName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력하세요.", 
                "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfName.requestFocus();
            return;
        }
        
        if (tfPlate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "번호판을 입력하세요.", 
                "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPlate.requestFocus();
            return;
        }
        
        String priceText = tfPrice.getText().trim();
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "가격을 입력하세요.", 
                "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPrice.requestFocus();
            return;
        }
        
        int price;
        try {
            price = Integer.parseInt(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "가격은 숫자로만 입력해야 합니다.", 
                "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPrice.requestFocus();
            return;
        }
        
        if (price <= 0) {
            JOptionPane.showMessageDialog(this,
                "가격은 0보다 커야 합니다.",
                "입력 오류",
                JOptionPane.ERROR_MESSAGE);
            tfPrice.requestFocus();
            return;
        }

        try {
            CampingCar c = new CampingCar();
            c.setCompanyId(((Company)cbCompany.getSelectedItem()).getId());
            c.setName(tfName.getText().trim());
            c.setPlateNumber(tfPlate.getText().trim());
            c.setCapacity((Integer)spCap.getValue());
            c.setRentalPrice(Integer.parseInt(tfPrice.getText().trim()));
            c.setImage(tfImg.getText().trim());
            c.setDetailInfo(taDetail.getText().trim());
            Date dt = (Date)spRegDateTime.getValue();
            String reg = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                              .format(dt);
            c.setRegistrationDate(reg);
            
			super.dao.insert(c);
		} catch (SQLException e) {
			DialogUtil.showError(this, e.getMessage());
		}
        showView();
    }
}
