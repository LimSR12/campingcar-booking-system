package admin.view;

import admin.dao.InternalMaintenanceDao;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

public class InternalMaintenanceDelete extends JDialog {
    private final InternalMaintenanceDao dao; private final Runnable ref;
    private final JTextField tfCond=new JTextField(30);

    public InternalMaintenanceDelete(Frame p, InternalMaintenanceDao dao, Runnable ref){
        super(p,"조건식에 해당하는 internal_maintenance 삭제",true);
        this.dao=dao; this.ref=ref;
        init(); pack(); setLocationRelativeTo(p);
    }

    private void init(){
        JPanel root=new JPanel(); root.setLayout(new BoxLayout(root,BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8,12,8,12)); setContentPane(root);

        JLabel map = new JLabel(
                "<html><b>※ 조건식에 영문으로 된 실제 칼럼명 사용하십시오.</b><br>"
              + "차량 → car_id &nbsp; 부품 → part_id &nbsp; 직원 → staff_id<br>"
              + "수리일 → repair_date &nbsp; 시간 → duration &nbsp; 내용 → description"
              + "</html>");
        map.setFont(map.getFont().deriveFont(12f));
        map.setPreferredSize(new Dimension(500,map.getPreferredSize().height));
        map.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(map); root.add(Box.createVerticalStrut(12));

        JPanel cond=new JPanel(new FlowLayout(FlowLayout.LEFT));
        cond.setBorder(BorderFactory.createTitledBorder("조건식 (예: duration > 60)"));
        cond.add(tfCond); root.add(cond); root.add(Box.createVerticalStrut(8));

        JPanel btn=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton del=new JButton("삭제"), cn=new JButton("취소");
        btn.add(del); btn.add(cn); root.add(btn); getRootPane().setDefaultButton(del);

        del.addActionListener(e->exec());
        cn.addActionListener(e->dispose());
    }

    private void exec(){
        String cond=tfCond.getText().trim();
        if(cond.isBlank()){DialogUtil.showWarning(this,"조건식을 입력하세요.");return;}
        String up=cond.toUpperCase();
        if(up.contains(";")||up.contains("DROP ")||up.contains("INSERT ")
           ||up.contains("UPDATE ")||up.contains("DELETE ")){
            DialogUtil.showWarning(this,"조건식에 위험 키워드가 포함돼 있습니다.");return;
        }
        try{
            Map<String,Integer> res=dao.deleteByCondition(cond);
            if(res.isEmpty()){DialogUtil.showWarning(this,"조건에 맞는 레코드가 없습니다.");return;}
            StringBuilder sb=new StringBuilder("삭제 완료:\n\n");
            res.forEach((k,v)->sb.append("- ").append(k).append(": ").append(v).append("건\n"));
            DialogUtil.showInfo(this,sb.toString());
            ref.run(); dispose();
        }catch(SQLException ex){DialogUtil.showError(this,ex.getMessage());}
    }
}
