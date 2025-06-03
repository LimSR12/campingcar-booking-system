package admin.view;

import admin.dao.CampingCarDao;
import admin.dao.CompanyDao;
import global.entity.CampingCar;
import global.entity.Company;
import global.util.DialogUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * camping_car 테이블의 “조건식 기반 수정” 다이얼로그 (개선된 레이아웃 버전)
 * - 모든 컬럼(기본키 제외) 수정 입력란
 * - 하단에 조건식 빌더(WHERE 절) 입력란
 * - BoxLayout 을 사용해, 폼 전체를 세로로 배치
 */
public class CampingCarUpdate extends JDialog {
    private final CampingCarDao campingCarDao;
    private final Runnable     onSuccessRefresh;

    // --- 수정할 컬럼 입력 컴포넌트 ---
    private final JComboBox<Company> cbCompany     = new JComboBox<>();
    private final JTextField         tfName        = new JTextField(20);
    private final JTextField         tfPlate       = new JTextField(20);
    private final JSpinner           spCapacity    = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    private final JTextField         tfRentalPrice = new JTextField(20);
    private final JTextField         tfImage       = new JTextField(30);
    private final JTextField         tfDetail      = new JTextField(30);
    private final JSpinner           spRegDateTime = new JSpinner(new SpinnerDateModel());

    // --- (단순화) 조건 입력용 텍스트필드 하나만 사용 ---
    private final JTextField tfCondition = new JTextField(25);

    public CampingCarUpdate(Frame parent,
                            CampingCarDao campingCarDao,
                            Runnable onSuccessRefresh) {
        super(parent, "조건식에 해당하는 튜플 수정하기", true);
        this.campingCarDao     = campingCarDao;
        this.onSuccessRefresh  = onSuccessRefresh;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        // 메인 컨테이너: BoxLayout(Y_AXIS)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setContentPane(content);
        
        JLabel lblMapping = new JLabel(
                "<html>" +
                "&nbsp;<b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br>" +
                "&nbsp;회사 → company_id&nbsp;&nbsp;&nbsp;" +
                "이름 → name&nbsp;&nbsp;&nbsp;" +
                "번호판 → plate_number<br>" +
                "&nbsp;정원 → capacity&nbsp;&nbsp;&nbsp;" +
                "가격 → rental_price&nbsp;&nbsp;&nbsp;" +
                "이미지 경로 → image<br>" +
                "&nbsp;세부설명 → detail_info&nbsp;&nbsp;&nbsp;" +
                "등록일시 → registration_date" +
                "</html>"
            );
        lblMapping.setFont(lblMapping.getFont().deriveFont(12f));
            
        // 선호 크기 얻기
        Dimension pref = lblMapping.getPreferredSize();
        int desiredWidth = 500; // 최소 800px 이상 확보
            
        // 선호 크기(preferredSize)
        lblMapping.setPreferredSize(new Dimension(desiredWidth, pref.height));
        // 최소 크기(minimumSize)
        lblMapping.setMinimumSize(new Dimension(desiredWidth, pref.height));
        // 최대 크기(maximumSize)
        lblMapping.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
        // 왼쪽 정렬 (BoxLayout 에서 가로 전체폭을 사용하도록)
        lblMapping.setAlignmentX(Component.CENTER_ALIGNMENT);
            
        content.add(lblMapping);
        content.add(Box.createVerticalStrut(12));

        // ───────────────────────────────────────────────────────
        // “수정할 컬럼” 패널
        // ───────────────────────────────────────────────────────
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("수정할 컬럼 (기본키 제외)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // 회사(외래키)
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("회사:"), gbc);
        gbc.gridx = 1;
        editPanel.add(cbCompany, gbc);

        // 이름
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("이름:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfName, gbc);

        // 번호판
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("번호판:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfPlate, gbc);

        // 정원
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("정원:"), gbc);
        gbc.gridx = 1;
        spCapacity.setModel(new SpinnerNumberModel(1,1,20,1));
        editPanel.add(spCapacity, gbc);

        // 가격
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("가격:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfRentalPrice, gbc);

        // 이미지 경로
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("이미지 경로:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfImage, gbc);

        // 세부설명
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("세부설명:"), gbc);
        gbc.gridx = 1;
        editPanel.add(tfDetail, gbc);

        // 등록일시
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        editPanel.add(new JLabel("등록일시:"), gbc);
        gbc.gridx = 1;
        spRegDateTime.setEditor(new JSpinner.DateEditor(spRegDateTime, "yyyy-MM-dd HH:mm:ss"));
        editPanel.add(spRegDateTime, gbc);

        content.add(editPanel);
        content.add(Box.createVerticalStrut(8));

        // ───────────────────────────────────────────────────────
        // “조건식” 패널 (콤보박스 없이 단순 텍스트필드)
        // ───────────────────────────────────────────────────────
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        condPanel.setBorder(BorderFactory.createTitledBorder("조건식 (예: rental_price >= 5000 AND capacity < 4)"));
        condPanel.add(tfCondition, FlowLayout.LEFT);

        content.add(condPanel);
        content.add(Box.createVerticalStrut(8));

        // ───────────────────────────────────────────────────────
        // 버튼 패널
        // ───────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExecute = new JButton("실행");
        JButton btnCancel  = new JButton("취소");
        btnPanel.add(btnExecute);
        btnPanel.add(btnCancel);

        btnExecute.addActionListener(e -> onExecute());
        btnCancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnExecute);

        content.add(btnPanel);

        // ───────────────────────────────────────────────────────
        // 초기화: 회사 콤보박스 로드
        // ───────────────────────────────────────────────────────
        reloadCompanyList();
    }

    /** 회사 목록(JComboBox)을 다시 로드 */
    private void reloadCompanyList() {
        try {
            // java.util.List
            List<Company> comps = new CompanyDao().findAll();
            DefaultComboBoxModel<Company> model = new DefaultComboBoxModel<>();
            for (Company c : comps) {
                model.addElement(c);
            }
            cbCompany.setModel(model);

            // 콤보박스에는 회사 이름만 보여주도록 렌더러 설정
            cbCompany.setRenderer((JList<? extends Company> list, Company value, int idx, boolean isSelected, boolean cellHasFocus) -> {
                JLabel lbl = new JLabel(value.getName() + " (ID:" + value.getId() + ")");
                return lbl;
            });
        } catch (SQLException ex) {
            DialogUtil.showError(this, "회사 목록 조회 오류:\n" + ex.getMessage());
        }
    }

    /** “실행” 클릭 시 호출 */
    private void onExecute() {
        // 수정할 값들 수집
        Map<String, Object> newValues = new LinkedHashMap<>();

        // 회사(외래키)
        Company selComp = (Company) cbCompany.getSelectedItem();
        if (selComp != null) {
            newValues.put("company_id", selComp.getId());
        }

        // 이름
        String nameVal = tfName.getText().trim();
        if (!nameVal.isEmpty()) {
            newValues.put("name", nameVal);
        }

        // 번호판
        String plateVal = tfPlate.getText().trim();
        if (!plateVal.isEmpty()) {
            newValues.put("plate_number", plateVal);
        }

        // 정원
        int capVal = (Integer) spCapacity.getValue();
        if (capVal > 0) {
            newValues.put("capacity", capVal);
        }

        // 가격
        String priceText = tfRentalPrice.getText().trim();
        if (!priceText.isEmpty()) {
            try {
                double priceVal = Double.parseDouble(priceText);
                if (priceVal < 0) {
                    DialogUtil.showWarning(this, "가격은 0 이상이어야 합니다.");
                    return;
                }
                newValues.put("rental_price", priceVal);
            } catch (NumberFormatException ex) {
                DialogUtil.showWarning(this, "가격은 숫자로만 입력해야 합니다.");
                return;
            }
        }

        // 이미지 경로
        String imgVal = tfImage.getText().trim();
        if (!imgVal.isEmpty()) {
            newValues.put("image", imgVal);
        }

        // 세부설명
        String detailVal = tfDetail.getText().trim();
        if (!detailVal.isEmpty()) {
            newValues.put("detail_info", detailVal);
        }

        // 등록일시
        Object dateObj = spRegDateTime.getValue();
        if (dateObj instanceof java.util.Date) {
            String regStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format((java.util.Date) dateObj);
            newValues.put("registration_date", regStr);
        }

        if (newValues.isEmpty()) {
            DialogUtil.showWarning(this, "수정할 컬럼을 최소 한 개 이상 입력하세요.");
            return;
        }

        // 조건식 (WHERE 절) – 사용자가 입력한 문자열을 그대로 사용
        String condition = tfCondition.getText().trim();
        if (condition.isEmpty()) {
            DialogUtil.showWarning(this, "조건식을 입력하세요.");
            return;
        }
        // 간단 검증: 세미콜론 같은 위험 키워드가 들어갔으면 경고
        String upper = condition.toUpperCase();
        if (upper.contains(";") || upper.contains("DROP ") || upper.contains("DELETE ")
         || upper.contains("INSERT ") || upper.contains("UPDATE ")) {
            DialogUtil.showWarning(this, "조건식에 안전하지 않은 키워드가 포함되어 있습니다.");
            return;
        }

        // DAO 호출: updateByCondition(newValues, condition)
        try {
            int count = campingCarDao.updateByCondition(newValues, condition);
            if (count > 0) {
                DialogUtil.showInfo(this, "성공적으로 " + count + "건 수정되었습니다.");
                onSuccessRefresh.run();
                dispose();
            } else {
                DialogUtil.showWarning(this, "조건에 맞는 레코드가 없습니다.");
            }
        } catch (SQLException ex) {
            DialogUtil.showError(this, "수정 중 오류 발생:\n" + ex.getMessage());
        }
    }
}
