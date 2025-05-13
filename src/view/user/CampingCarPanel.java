package view.user;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.CampingCar;
import dao.CampingCarDao;

public class CampingCarPanel extends JPanel {
	private List<CampingCar> carList;
	private final int columns = 6;
	
    public CampingCarPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"캠핑카 이름", "차량 번호", "승차 인원", "대여 비용", "차량 등록일자", "차량 세부정보"};
        String[][] data = loadCampingCarData();

        JTable table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        
        JPanel buttonPanel = new JPanel();
        JButton reserveButton = new JButton("예약하기");
        buttonPanel.add(reserveButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        reserveButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "예약할 캠핑카를 선택하세요.");
                return;
            }
            
            // 선택된 행에서 데이터 추출
            String name = table.getValueAt(selectedRow, 0).toString(); // 캠핑카 이름
            String plate = table.getValueAt(selectedRow, 1).toString(); // 차량번호
            String price = table.getValueAt(selectedRow, 3).toString(); // 대여 비용

            // 예약 화면에 값 넘기기 
            System.out.println("선택된 차량: " + name + " / " + plate + " / " + price);
        });
    }

    private String[][] loadCampingCarData() {
        CampingCarDao dao = new CampingCarDao();
        List<CampingCar> list = dao.getAllCampingCars();

        String[][] data = new String[list.size()][columns];
        for (int i = 0; i < list.size(); i++) {
            CampingCar car = list.get(i);
            data[i][0] = car.getName();
            data[i][1] = car.getPlateNumber();
            data[i][2] = String.valueOf(car.getCapacity()) + "인승";     
            data[i][3] = String.valueOf(car.getRentalPrice()) + "원";
            data[i][4] = String.valueOf(car.getRegistrationDate());
            data[i][5] = String.valueOf(car.getDetailInfo());
        }
        return data;
    }
}
