package admin.view;

import admin.dao.CampingCarDao;
import admin.dao.CompanyDao;
import global.entity.CampingCar;
import global.entity.Company;
import global.util.DialogUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * camping_car 전용 CRUD 패널
 * → 조회 화면(View)에서 아래 항목을 모두 보여줍니다.
 *    1) ID
 *    2) Company 이름
 *    3) Name
 *    4) Plate Number
 *    5) Capacity
 *    6) Rental Price
 *    7) Registration Date
 *    8) Detail Info
 *    9) Image (썸네일)
 *
 * (기존의 입력/수정/삭제 로직은 동일하게 유지하되, 조회 화면만 확장)
 */
public class CampingCarPanel extends AbstractTableCRUDPanel<CampingCar> {
    private JComboBox<Company> cbCompany;
    private JTextField tfName, tfPlate, tfPrice, tfImg, taDetail;
    private JSpinner spCap, spRegDateTime;

    public CampingCarPanel() {
        super(new CampingCarDao());
        
        // ‘툴바’ 컴포넌트를 찾아 가져온다
        JToolBar tb = (JToolBar) ((BorderLayout)getLayout())
                      .getLayoutComponent(BorderLayout.NORTH);

        // 버튼 추가
        JButton btnHist = new JButton("내역");
        tb.add(btnHist);
        btnHist.addActionListener(e -> openHistoryDialog());
    }

    @Override
    protected JPanel createFormPanel() {
        // (기존의 입력 폼 로직과 동일하므로 생략)
        JPanel p = new JPanel(new GridLayout(0,2,6,6));

        // 회사 콤보박스
        // company 콤보박스
        cbCompany = new JComboBox<>();
        // 렌더러: "회사명 (ID)"
        cbCompany.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            return new JLabel(value.getName() + " (ID:" + value.getId() + ")");
        });
        p.add(new JLabel("회사"));
        p.add(cbCompany);

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

        spRegDateTime = new JSpinner(new SpinnerDateModel());
        spRegDateTime.setEditor(new JSpinner.DateEditor(spRegDateTime, "yyyy-MM-dd HH:mm:ss"));
        p.add(new JLabel("등록일시"));
        p.add(spRegDateTime);

        JPanel btns = new JPanel();
        JButton save   = new JButton("저장");
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
        // (기존과 동일하게 콤보박스 재로딩 및 폼 초기화)
        reloadCompanyList();
        cbCompany.setSelectedIndex(0);
        tfName.setText("");
        tfPlate.setText("");
        spCap.setValue(4);
        tfPrice.setText("");
        tfImg.setText("");
        taDetail.setText("");
        spRegDateTime.setValue(new java.util.Date());

        cards.show(cardPane, "FORM");
    }

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

    @Override
    protected void openUpdateByConditionDialog() {
        // 기존 “조건식 기반 수정” 다이얼로그 호출 로직
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CampingCarDao carDao = (CampingCarDao) super.dao;
        CampingCarUpdate dlg =
            new CampingCarUpdate(parentFrame, carDao, this::refreshTable);
        dlg.setVisible(true);
    }

    @Override
    protected void openDeleteByConditionDialog() {
        // 부모 Frame 가져오기
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        // DAO 객체 캐스팅
        CampingCarDao carDao = (CampingCarDao) super.dao;

        // 우리가 만든 삭제 다이얼로그 생성 (다이얼로그가 닫힌 뒤 refreshTable 호출)
        CampingCarDelete dlg = new CampingCarDelete(
            parentFrame,
            carDao,
            this::refreshTable  // 삭제 성공 시 표를 새로고침
        );
        dlg.setVisible(true);
    }

    /** 회사 목록(JComboBox)을 다시 로드 */
    private void reloadCompanyList() {
        try {
            List<Company> companies = new CompanyDao().findAll();
            DefaultComboBoxModel<Company> compModel = new DefaultComboBoxModel<>();
            for (Company c : companies) {
                compModel.addElement(c);
            }
            cbCompany.setModel(compModel);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "회사 목록 조회 오류:\n" + ex.getMessage());
        }
    }

    /** 조회 화면(View) – 테이블 생성 */
    @Override
    protected JScrollPane createListPanel() {
        table = new JTable();
        // 이미지가 들어갈 열을 렌더링하기 위한 커스텀 셀 렌더러 지정
        table.setRowHeight(100); // 썸네일 높이
        table.setDefaultRenderer(ImageIcon.class, new ImageRenderer());
        return new JScrollPane(table);
    }

    /** 조회 화면(View) – 테이블 모델/데이터 채우기 */
    @Override
    protected void refreshTable() {
        try {
            // 1) 모든 캠핑카 엔티티 조회
            List<CampingCar> list = ((CampingCarDao) dao).findAll();

            // 2) 칼럼명 정의: ID, 회사명, 이름, 번호판, 정원, 가격, 등록일시, 세부정보, 이미지
            String[] columnNames = {
                "ID", "회사 ID", "이름", "번호판", "정원",
                "가격", "등록일시", "세부정보", "이미지"
            };

            // 3) 모델 생성 (ImageIcon을 다룰 수 있도록 Object[][] 배열 사용)
            Object[][] data = new Object[list.size()][columnNames.length];
            CompanyDao companyDao = new CompanyDao();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < list.size(); i++) {
                CampingCar c = list.get(i);

                // 3-1) ID
                data[i][0] = c.getId();

                // 3-2) 회사명 조회 (company_id)
//                String companyName = "";
//                try {
//                    Company comp = companyDao.findById(c.getCompanyId());
//                    companyName = (comp != null ? comp.getName() : "");
//                } catch (SQLException e) {
//                    companyName = "";
//                }
                data[i][1] = c.getCompanyId();

                // 3-3) 나머지 기본 컬럼
                data[i][2] = c.getName();
                data[i][3] = c.getPlateNumber();
                data[i][4] = c.getCapacity();
                data[i][5] = c.getRentalPrice();

                // 3-4) 등록일시 (Date → 문자열)
                String regStr = c.getRegistrationDate();
                data[i][6] = regStr;

                // 3-5) 세부정보(detail_info)
                String detail = c.getDetailInfo() != null ? c.getDetailInfo() : "";
                data[i][7] = detail;

                // 3-6) 이미지(ImageIcon) – 상대경로(images/...)로부터 로드 시도
                String imgPath = c.getImage(); // e.g. "images/hyundai_starex.jpg"
                ImageIcon icon = null;
                if (imgPath != null && !imgPath.trim().isEmpty()) {
                    try {
                        Image img = ImageIO.read(new File(imgPath));
                        // 썸네일 크기(가로100px, 세로100px)로 조정
                        Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaled);
                    } catch (IOException ex) {
                        icon = null; // 이미지 로드 실패 시 null
                    }
                }
                data[i][8] = icon; // ImageIcon 또는 null
            }

            // 4) 테이블 모델 설정
            DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                // 이미지 컬럼은 editable=false로 고정, 타입은 ImageIcon
                @Override
                public Class<?> getColumnClass(int column) {
                    if (column == 8) {
                        return ImageIcon.class;
                    }
                    return super.getColumnClass(column);
                }
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            table.setModel(model);

            // 5) 컬럼 폭 조정 (특히 이미지 컬럼)
            TableColumnModel colModel = table.getColumnModel();
            colModel.getColumn(0).setPreferredWidth(50);   // ID
            colModel.getColumn(1).setPreferredWidth(120);  // 회사명
            colModel.getColumn(2).setPreferredWidth(120);  // 이름
            colModel.getColumn(3).setPreferredWidth(120);  // 번호판
            colModel.getColumn(4).setPreferredWidth(60);   // 정원
            colModel.getColumn(5).setPreferredWidth(80);   // 가격
            colModel.getColumn(6).setPreferredWidth(140);  // 등록일시
            colModel.getColumn(7).setPreferredWidth(200);  // 세부정보
            colModel.getColumn(8).setPreferredWidth(100);  // 이미지

            // 헤더 폰트·스타일
            JTableHeader header = table.getTableHeader();
            Font hdrFont = header.getFont();
            header.setFont(hdrFont.deriveFont(Font.BOLD, 14f));
            table.setRowHeight(100); // 이미지와 텍스트가 겹치지 않도록

            // 툴팁으로 full detail_info 보여주기
            table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (row >= 0 && col == 7) {
                        String fullText = (String) table.getValueAt(row, 7);
                        table.setToolTipText(fullText);
                    } else {
                        table.setToolTipText(null);
                    }
                }
            });

        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }

    /** 이미지 셀 렌더러 (ImageIcon을 중앙 정렬하여 보여 줌) */
    private static class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof ImageIcon) {
                JLabel lbl = new JLabel((ImageIcon) value);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    lbl.setOpaque(true);
                    lbl.setBackground(table.getSelectionBackground());
                }
                return lbl;
            } else {
                // 이미지가 없는 경우 빈 레이블 리턴
                JLabel lbl = new JLabel();
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        }
    }
    
    private void openHistoryDialog(){
        int sel = table.getSelectedRow();
        if(sel==-1){ DialogUtil.showWarning(this,"캠핑카를 선택하세요."); return; }
        long carId = (long) table.getValueAt(sel,0);        // ID 칼럼
        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
        new CarHistoryDialog(f, carId).setVisible(true);
    }
}
