package view.user;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.CampingCar;
import dao.CampingCarDao;

public class CampingCarPanel extends JPanel {
    public CampingCarPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"캠핑카 이름", "차량 번호", "승차 인원", "대여 비용", "차량 등록일자"};
        String[][] data = loadCampingCarData();

        JTable table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private String[][] loadCampingCarData() {
        CampingCarDao dao = new CampingCarDao();
        List<CampingCar> list = dao.getAllCampingCars();

        String[][] data = new String[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            CampingCar car = list.get(i);
            data[i][0] = car.getName();
            data[i][1] = car.getPlateNumber();
            data[i][2] = String.valueOf(car.getCapacity()) + "인승";     
            data[i][3] = String.valueOf(car.getRentalPrice()) + "원";
            data[i][4] = String.valueOf(car.getRegistrationDate());
        }
        return data;
    }
}
