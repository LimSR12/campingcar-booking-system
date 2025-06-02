package admin.view;

import admin.dao.PartInventoryDao;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.SQLException;

public class PartInventoryUpdate extends JDialog {
    private final PartInventoryDao dao;
    private final Runnable refresh;

    /* 입력 필드 */
    private final JTextField tfName   = new JTextField(20);
    private final JTextField tfPrice  = new JTextField(10);
    private final JTextField tfQty    = new JTextField(6);
    private final JSpinner   spDate   = new JSpinner(new SpinnerDateModel());
    private final JTextField tfSupplier = new JTextField(20);

    private final JTextField tfCond   = new JTextField(35);

    public PartInventoryUpdate(Frame parent, PartInventoryDao dao, Runnable refresh) {
        super(parent, "Part-Inventory — 조건식 수정", true);
        this.dao = dao;
        this.refresh = refresh;
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    /* ───────────────────────── UI ───────────────────────── */
    private void initUI() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setContentPane(root);

        /* 1) 칼럼 매핑 레이블 */
        JLabel lblMapping = new JLabel(
            "<html><b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br> 부품명 → name&nbsp; 가격 → price&nbsp;<br>"
          + "수량 → quantity&nbsp; 입고일 → received_date&nbsp; 공급사 → supplier_name</html>");
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
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST; int r=0;

        g.gridx=0; g.gridy=r; form.add(new JLabel("부품명:"),g);
        g.gridx=1; form.add(tfName,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("가격:"),g);
        g.gridx=1; form.add(tfPrice,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("수량:"),g);
        g.gridx=1; form.add(tfQty,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("입고일:"),g);
        g.gridx=1; spDate.setEditor(new JSpinner.DateEditor(spDate,"yyyy-MM-dd HH:mm:ss"));
        form.add(spDate,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("공급사:"),g);
        g.gridx=1; form.add(tfSupplier,g);

        root.add(form);
        root.add(Box.createVerticalStrut(8));

        /* 3) 조건식 */
        JPanel cond = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cond.setBorder(BorderFactory.createTitledBorder("조건식 (예: quantity > 20)"));
        cond.add(tfCond);
        root.add(cond);

        /* 4) 버튼 */
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("실행"), cancel = new JButton("취소");
        btn.add(ok); btn.add(cancel);
        root.add(btn);
        getRootPane().setDefaultButton(ok);

        ok.addActionListener(e -> execute());
        cancel.addActionListener(e -> dispose());
    }

    private void execute() {
        Map<String,Object> set = new LinkedHashMap<>();
        if(!tfName.getText().isBlank())   set.put("name", tfName.getText().trim());
        if(!tfPrice.getText().isBlank())  set.put("price", Double.parseDouble(tfPrice.getText().trim()));
        if(!tfQty.getText().isBlank())    set.put("quantity", Integer.parseInt(tfQty.getText().trim()));
        set.put("received_date",
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) spDate.getValue()));
        if(!tfSupplier.getText().isBlank()) set.put("supplier_name", tfSupplier.getText().trim());

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
