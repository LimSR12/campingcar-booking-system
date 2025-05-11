package view.user;

import javax.swing.*;
import java.awt.*;

public class CampingCarListFrame extends JFrame {

    public CampingCarListFrame() {
        setTitle("전체 캠핑카 조회");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 이 창만 닫히게

        // 샘플 캠핑카 목록 데이터 (나중에 DB에서 불러오면 됨)
        String[] columnNames = {"캠핑카 이름", "차량 번호", "승차 인원", "대여 비용"};
        String[][] data = {
                {"캐리어1호", "123가4567", "4", "120,000원"},
                {"로망스2호", "789나1234", "6", "150,000원"},
                {"패밀리카", "456다7890", "5", "130,000원"},
        };

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
