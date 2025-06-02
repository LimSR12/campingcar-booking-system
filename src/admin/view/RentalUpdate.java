package admin.view;

import admin.dao.*;
import global.entity.*;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RentalUpdate extends JDialog {
    private final RentalDao rentalDao;
    private final Runnable  onSuccessRefresh;

    /* 수정 입력 컴포넌트 */
    private final JComboBox<CampingCar> cbCar  = new JComboBox<>();
    private final JComboBox<Customer>   cbCust = new JComboBox<>();
    private final JComboBox<Company>    cbComp = new JComboBox<>();
    private final JTextField  tfLicense = new JTextField(20);
    private final JSpinner    spStart   = new JSpinner(new SpinnerDateModel());
    private final JSpinner    spReturn  = new JSpinner(new SpinnerDateModel());
    private final JTextField  tfDays    = new JTextField(6);
    private final JTextField  tfFee     = new JTextField(10);
    private final JSpinner    spFeeDue  = new JSpinner(new SpinnerDateModel());
    private final JTextField  tfExtra   = new JTextField(25);
    private final JTextField  tfExtraFee= new JTextField(10);

    private final JTextField tfCondition = new JTextField(35);

    public RentalUpdate(Frame parent, RentalDao dao, Runnable onSuccessRefresh) {
        super(parent, "Rental – 조건식 수정", true);
        this.rentalDao = dao;
        this.onSuccessRefresh = onSuccessRefresh;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        setContentPane(content);

        /* ───── 컬럼 매핑 안내 ───── */
        JLabel lblMapping = new JLabel(
            "<html><b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br> &nbsp;" +
            "차량 → car_id,&nbsp; 고객 → customer_id,&nbsp; 회사 → company_id,<br>" +
            "면허번호 → license_number,  시작일 → start_date,  종료일 → return_date,<br>" +
            "일수 → rental_days,  금액 → rental_fee,  납부기한 → fee_due_date,<br>" +
            "추가사항 → extra_details,  추가금액 → extra_fee" +
            "</html>");
        
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

        /* ───── 입력 폼 ───── */
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("수정할 값 (빈 칸은 제외)"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.anchor = GridBagConstraints.WEST;

        int r = 0;
        g.gridx=0; g.gridy=r; form.add(new JLabel("차량:"),g);
        g.gridx=1; form.add(cbCar,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("고객:"),g);
        g.gridx=1; form.add(cbCust,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("회사:"),g);
        g.gridx=1; form.add(cbComp,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("면허번호:"),g);
        g.gridx=1; form.add(tfLicense,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("시작일:"),g);
        g.gridx=1; spStart.setEditor(new JSpinner.DateEditor(spStart,"yyyy-MM-dd HH:mm:ss"));
        form.add(spStart,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("종료일:"),g);
        g.gridx=1; spReturn.setEditor(new JSpinner.DateEditor(spReturn,"yyyy-MM-dd HH:mm:ss"));
        form.add(spReturn,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("일수:"),g);
        g.gridx=1; form.add(tfDays,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("금액:"),g);
        g.gridx=1; form.add(tfFee,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("납부기한:"),g);
        g.gridx=1; spFeeDue.setEditor(new JSpinner.DateEditor(spFeeDue,"yyyy-MM-dd HH:mm:ss"));
        form.add(spFeeDue,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("추가사항:"),g);
        g.gridx=1; form.add(tfExtra,g);

        ++r; g.gridx=0; g.gridy=r; form.add(new JLabel("추가금액:"),g);
        g.gridx=1; form.add(tfExtraFee,g);

        content.add(form);
        content.add(Box.createVerticalStrut(8));

        /* ───── 조건식 ───── */
        JPanel cond = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cond.setBorder(BorderFactory.createTitledBorder("조건식 (예: rental_fee < 300000)"));
        cond.add(tfCondition);
        content.add(cond);

        /* ───── 버튼 ───── */
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("실행"), cancel = new JButton("취소");
        btns.add(ok); btns.add(cancel);
        ok.addActionListener(e->onExecute());
        cancel.addActionListener(e->dispose());
        getRootPane().setDefaultButton(ok);
        content.add(btns);

        reloadCombos();
    }

    private void reloadCombos() {
        try {
            cbCar .setModel(new DefaultComboBoxModel<>(new Vector<>(new CampingCarDao().findAll())));
            cbCust.setModel(new DefaultComboBoxModel<>(new Vector<>(new CustomerDao().findAll())));
            cbComp.setModel(new DefaultComboBoxModel<>(new Vector<>(new CompanyDao().findAll())));
            cbCar .setRenderer((l,v,i,s,c)-> new JLabel(v==null?"":v.getName()+"("+v.getId()+")"));
            cbCust.setRenderer((l,v,i,s,c)-> new JLabel(v==null?"":v.getName()+"("+v.getId()+")"));
            cbComp.setRenderer((l,v,i,s,c)-> new JLabel(v==null?"":v.getName()+"("+v.getId()+")"));
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }

    /* 실행 버튼 */
    private void onExecute() {
        Map<String,Object> set = new LinkedHashMap<>();

        CampingCar car = (CampingCar) cbCar.getSelectedItem();
        if (car!=null) set.put("car_id", car.getId());

        Customer cu = (Customer) cbCust.getSelectedItem();
        if (cu!=null) set.put("customer_id", cu.getId());

        Company co = (Company) cbComp.getSelectedItem();
        if (co!=null) set.put("company_id", co.getId());

        if (!tfLicense.getText().trim().isEmpty())
            set.put("license_number", tfLicense.getText().trim());

        if (!tfDays.getText().trim().isEmpty())
            set.put("rental_days", Integer.parseInt(tfDays.getText().trim()));

        if (!tfFee.getText().trim().isEmpty())
            set.put("rental_fee", Double.parseDouble(tfFee.getText().trim()));

        if (!tfExtra.getText().trim().isEmpty())
            set.put("extra_details", tfExtra.getText().trim());

        if (!tfExtraFee.getText().trim().isEmpty())
            set.put("extra_fee", Double.parseDouble(tfExtraFee.getText().trim()));

        Date d1=(Date)spStart.getValue();
        set.put("start_date",   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d1));
        Date d2=(Date)spReturn.getValue();
        set.put("return_date",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d2));
        Date d3=(Date)spFeeDue.getValue();
        set.put("fee_due_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d3));

        set.values().removeIf(Objects::isNull);   // 불필요 제거

        if (set.isEmpty()) {
            DialogUtil.showWarning(this, "수정할 컬럼을 한 개 이상 입력하세요."); return;
        }

        String cond = tfCondition.getText().trim();
        if (cond.isEmpty()) { DialogUtil.showWarning(this,"조건식을 입력하세요."); return; }

        try {
            int n = rentalDao.updateByCondition(set, cond);
            DialogUtil.showInfo(this, n+"건 수정 완료");
            onSuccessRefresh.run();
            dispose();
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }
}
