package admin.view;

import admin.dao.CampingCarDao;
import global.entity.CampingCar;
import global.entity.Company;
import global.util.DialogUtil;
import admin.dao.CompanyDao;
import admin.dao.CustomerDao;

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
        cbCompany = new JComboBox<>();
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
    
    // 콤보박스 모델을 매번 최신 상태로 다시 로드해 주는 헬퍼 메서드
    private void reloadCompanyList() {
        try {
            // DAO 통해 최신 회사 리스트 가져오기
            List<Company> comps = new CompanyDao().findAll();

            // 콤보박스 모델 초기화
            DefaultComboBoxModel<Company> model = new DefaultComboBoxModel<>();
            for (Company c : comps) {
                model.addElement(c);
            }
            cbCompany.setModel(model);

            // 화면엔 회사명만 보이도록 렌더러 적용
            cbCompany.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value == null ? "" : value.getName())
            );
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }

    // 입력 패널 초기 설정
    @Override
    protected void clearAndShowForm() {
        // 폼을 열기 직전에 최신 회사 목록을 로드
        reloadCompanyList();
    	
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
    	// 이름이 비었는지 확인
        if (tfName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력하세요.", 
                "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfName.requestFocus();
            return;
        }
        
        // 번호판이 비었는지 확인
        if (tfPlate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "번호판을 입력하세요.", 
                "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPlate.requestFocus();
            return;
        }
        
        // 가격이 비었는지 확인
        String priceText = tfPrice.getText().trim();
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "가격을 입력하세요.", 
                "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPrice.requestFocus();
            return;
        }
        
        // 가격이 숫자인지 확인
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
        	
            // DAO를 이용해 중복 체크
            CampingCarDao cDao = new CampingCarDao();
            if (cDao.existsByPlateNumber(tfPlate.getText().trim())) {
                JOptionPane.showMessageDialog(
                    this,
                    "이미 등록된 번호판입니다. 다른 번호판을 입력하세요.",
                    "중복 오류",
                    JOptionPane.ERROR_MESSAGE
                );
                tfPlate.requestFocus();
                return;
            }
        	
            CampingCar c = new CampingCar();
            c.setCompanyId(((Company)cbCompany.getSelectedItem()).getId());
            c.setName(tfName.getText().trim());
            c.setPlateNumber(tfPlate.getText().trim());
            c.setCapacity((Integer)spCap.getValue());
            c.setRentalPrice(price);
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
