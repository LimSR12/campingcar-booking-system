package admin.view;

import admin.dao.PartInventoryDao;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

public class PartInventoryDelete extends JDialog {
    private final PartInventoryDao dao; private final Runnable ref;
    private final JTextField tfCond=new JTextField(30);

    public PartInventoryDelete(Frame p, PartInventoryDao dao, Runnable ref){
        super(p,"조건식에 해당하는 part_inventory 삭제",true);
        this.dao=dao; this.ref=ref;
        init(); pack(); setLocationRelativeTo(p);
    }

    private void init(){
        JPanel root=new JPanel(); root.setLayout(new BoxLayout(root,BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8,12,8,12)); setContentPane(root);

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

        JPanel cond=new JPanel(new FlowLayout(FlowLayout.LEFT));
        cond.setBorder(BorderFactory.createTitledBorder("조건식 (예: quantity = 0)"));
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
