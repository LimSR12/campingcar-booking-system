package admin.view;

import admin.dao.*;
import global.entity.*;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InternalMaintenanceUpdate extends JDialog {
    private final InternalMaintenanceDao dao;
    private final Runnable onRefresh;

    /* 입력 컴포넌트 */
    private final JComboBox<CampingCar>   cbCar   = new JComboBox<>();
    private final JComboBox<PartInventory>cbPart  = new JComboBox<>();
    private final JComboBox<Staff>        cbStaff = new JComboBox<>();
    private final JSpinner  spDate      = new JSpinner(new SpinnerDateModel());
    private final JTextField tfDur      = new JTextField(6);
    private final JTextField tfDesc     = new JTextField(30);
    private final JTextField tfCond     = new JTextField(35);

    public InternalMaintenanceUpdate(Frame p, InternalMaintenanceDao dao, Runnable ref) {
        super(p, "Internal Maintenance – 조건식 수정", true);
        this.dao = dao; this.onRefresh = ref;
        init(); pack(); setLocationRelativeTo(p);
    }

    /* ───────── UI ───────── */
    private void init() {
        JPanel root = new JPanel(); root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8,12,8,12)); setContentPane(root);

        /* 1) 매핑 레이블 (가변폭) */
        JLabel map = new JLabel(
            "<html><b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br>"
          + "차량 → car_id &nbsp; 부품 → part_id &nbsp; 직원 → staff_id<br>"
          + "수리일 → repair_date &nbsp; 시간 → duration &nbsp; 내용 → description"
          + "</html>");
        map.setFont(map.getFont().deriveFont(12f));
        Dimension pref = map.getPreferredSize();
        map.setPreferredSize(new Dimension(500, pref.height));
        map.setMinimumSize( new Dimension(500, pref.height));
        map.setMaximumSize( new Dimension(Integer.MAX_VALUE, pref.height));
        map.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(map);  root.add(Box.createVerticalStrut(12));

        /* 2) 입력 폼 */
        JPanel f = new JPanel(new GridBagLayout());
        f.setBorder(BorderFactory.createTitledBorder("수정 값 (빈 칸 제외)"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.anchor = GridBagConstraints.WEST; int r=0;

        g.gridx=0; g.gridy=r; f.add(new JLabel("차량:"),g);
        g.gridx=1; f.add(cbCar,g);

        ++r; g.gridx=0; g.gridy=r; f.add(new JLabel("부품:"),g);
        g.gridx=1; f.add(cbPart,g);

        ++r; g.gridx=0; g.gridy=r; f.add(new JLabel("직원:"),g);
        g.gridx=1; f.add(cbStaff,g);

        ++r; g.gridx=0; g.gridy=r; f.add(new JLabel("수리일:"),g);
        g.gridx=1; spDate.setEditor(new JSpinner.DateEditor(spDate,"yyyy-MM-dd HH:mm:ss"));
        f.add(spDate,g);

        ++r; g.gridx=0; g.gridy=r; f.add(new JLabel("소요시간:"),g);
        g.gridx=1; f.add(tfDur,g);

        ++r; g.gridx=0; g.gridy=r; f.add(new JLabel("설명:"),g);
        g.gridx=1; f.add(tfDesc,g);

        root.add(f); root.add(Box.createVerticalStrut(8));

        /* 3) 조건식 */
        JPanel c = new JPanel(new FlowLayout(FlowLayout.LEFT));
        c.setBorder(BorderFactory.createTitledBorder("조건식 (예: duration > 60)"));
        c.add(tfCond); root.add(c);

        /* 4) 버튼 */
        JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok=new JButton("실행"), cn=new JButton("취소");
        b.add(ok); b.add(cn); root.add(b);
        getRootPane().setDefaultButton(ok);
        ok.addActionListener(e->exec()); cn.addActionListener(e->dispose());

        reloadCombos();
    }

    private void reloadCombos() {
        try {
            cbCar .setModel(new DefaultComboBoxModel<>(new Vector<>(new CampingCarDao().findAll())));
            cbPart.setModel(new DefaultComboBoxModel<>(new Vector<>(new PartInventoryDao().findAll())));
            cbStaff.setModel(new DefaultComboBoxModel<>(new Vector<>(new StaffDao().findAll())));
            cbCar .setRenderer((l,v,i,s,c)->new JLabel(v==null?"":v.getId()+": "+v.getName()));
            cbPart.setRenderer((l,v,i,s,c)->new JLabel(v==null?"":v.getId()+": "+v.getName()));
            cbStaff.setRenderer((l,v,i,s,c)->new JLabel(v==null?"":v.getId()+": "+v.getName()));
        } catch (SQLException ex) { DialogUtil.showError(this, ex.getMessage()); }
    }

    private void exec() {
        Map<String,Object> set = new LinkedHashMap<>();
        CampingCar  car=(CampingCar)cbCar.getSelectedItem();
        PartInventory part=(PartInventory)cbPart.getSelectedItem();
        Staff st=(Staff)cbStaff.getSelectedItem();
        if(car!=null)  set.put("car_id", car.getId());
        if(part!=null) set.put("part_id",part.getId());
        if(st!=null)   set.put("staff_id",st.getId());
        set.put("repair_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                               .format((Date)spDate.getValue()));
        if(!tfDur.getText().isBlank())  set.put("duration",Integer.parseInt(tfDur.getText().trim()));
        if(!tfDesc.getText().isBlank()) set.put("description",tfDesc.getText().trim());

        set.values().removeIf(Objects::isNull);
        if(set.isEmpty()){DialogUtil.showWarning(this,"수정할 값이 없습니다.");return;}

        String cond=tfCond.getText().trim();
        if(cond.isBlank()){DialogUtil.showWarning(this,"조건식을 입력하세요.");return;}

        try{
            int n=dao.updateByCondition(set,cond);
            DialogUtil.showInfo(this,n+"건 수정 완료");
            onRefresh.run(); dispose();
        }catch(SQLException ex){DialogUtil.showError(this,ex.getMessage());}
    }
}
