package admin.view;

import admin.dao.StaffDao;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;

public class StaffUpdate extends JDialog {
    private final StaffDao dao;
    private final Runnable refresh;

    /* 입력 필드 */
    private final JTextField tfName  = new JTextField(15);
    private final JTextField tfPhone = new JTextField(15);
    private final JTextField tfAddr  = new JTextField(25);
    private final JTextField tfSal   = new JTextField(10);
    private final JTextField tfFam   = new JTextField(4);
    private final JTextField tfDept  = new JTextField(15);
    private final JComboBox<String> cbRole =
        new JComboBox<>(new String[]{"정비","사무","관리"});

    private final JTextField tfCond  = new JTextField(35);

    public StaffUpdate(Frame parent, StaffDao dao, Runnable refresh) {
        super(parent, "Staff — 조건식 수정", true);
        this.dao = dao;
        this.refresh = refresh;
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    /* ───────────────────── UI ───────────────────── */
    private void initUI() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setContentPane(root);

        /* 1) 매핑 레이블 */
        JLabel lblMapping = new JLabel(
            "<html><b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br> 이름 → name&nbsp; 전화 → phone&nbsp; 주소 → address&nbsp;<br>"
          + "급여 → salary&nbsp; 가족수 → family_num&nbsp; 부서 → department&nbsp; 직무 → role</html>");
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
            
        root.add(lblMapping);
        root.add(Box.createVerticalStrut(12));

        /* 2) 입력 폼 */
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("수정 값 (빈칸 제외)"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);  g.anchor = GridBagConstraints.WEST; int r=0;

        g.gridx=0;g.gridy=r;form.add(new JLabel("이름:"),g); g.gridx=1;form.add(tfName,g);
        ++r;g.gridx=0;g.gridy=r;form.add(new JLabel("전화:"),g); g.gridx=1;form.add(tfPhone,g);
        ++r;g.gridx=0;g.gridy=r;form.add(new JLabel("주소:"),g); g.gridx=1;form.add(tfAddr,g);
        ++r;g.gridx=0;g.gridy=r;form.add(new JLabel("급여:"),g); g.gridx=1;form.add(tfSal,g);
        ++r;g.gridx=0;g.gridy=r;form.add(new JLabel("가족수:"),g); g.gridx=1;form.add(tfFam,g);
        ++r;g.gridx=0;g.gridy=r;form.add(new JLabel("부서:"),g); g.gridx=1;form.add(tfDept,g);
        ++r;g.gridx=0;g.gridy=r;form.add(new JLabel("직무:"),g); g.gridx=1;form.add(cbRole,g);

        root.add(form); root.add(Box.createVerticalStrut(8));

        /* 3) 조건식 */
        JPanel cond = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cond.setBorder(BorderFactory.createTitledBorder("조건식 (예: family_num > 1)"));
        cond.add(tfCond);
        root.add(cond);

        /* 4) 버튼 */
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("실행"), cancel = new JButton("취소");
        btn.add(ok); btn.add(cancel); root.add(btn);
        getRootPane().setDefaultButton(ok);

        ok.addActionListener(e -> execute());
        cancel.addActionListener(e -> dispose());
    }

    /* ───────────────────── 실행 ───────────────────── */
    private void execute() {
        Map<String,Object> set = new LinkedHashMap<>();
        if(!tfName.getText().isBlank())  set.put("name", tfName.getText().trim());
        if(!tfPhone.getText().isBlank()) set.put("phone", tfPhone.getText().trim());
        if(!tfAddr.getText().isBlank())  set.put("address", tfAddr.getText().trim());
        if(!tfSal.getText().isBlank())   set.put("salary", Integer.parseInt(tfSal.getText().trim()));
        if(!tfFam.getText().isBlank())   set.put("family_num", Integer.parseInt(tfFam.getText().trim()));
        if(!tfDept.getText().isBlank())  set.put("department", tfDept.getText().trim());
        set.put("role", cbRole.getSelectedItem());

        set.values().removeIf(Objects::isNull);
        if(set.isEmpty()){ DialogUtil.showWarning(this,"수정할 값이 없습니다."); return; }

        String cond = tfCond.getText().trim();
        if(cond.isBlank()){ DialogUtil.showWarning(this,"조건식을 입력하세요."); return; }

        try {
            int n = dao.updateByCondition(set, cond);
            DialogUtil.showInfo(this, n+"건 수정 완료");
            refresh.run();
            dispose();
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }
}
