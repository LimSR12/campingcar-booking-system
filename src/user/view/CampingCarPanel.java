package user.view;

import javax.swing.*;

import global.entity.CampingCar;
import user.dao.CampingCarDao;

import java.awt.*;
import java.util.List;

public class CampingCarPanel extends JPanel {
    private List<CampingCar> carList;
    private final int columns = 6;
    private JLabel infoLabel; // 상세 정보 출력용

    public CampingCarPanel() {
        // 전체 패널을 상/하 2단으로 나누기 위해 BoxLayout 사용
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 캠핑카 테이블 설정
        String[] columnNames = {"캠핑카 이름", "차량 번호", "승차 인원", "대여 비용", "차량 등록일자", "차량 세부정보"};
        String[][] data = loadCampingCarData();
        JTable table = new JTable(data, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(1024, 480)); // 상단 2/3 크기
        add(tableScroll);

        // 하단 상세정보 + 버튼 패널
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setPreferredSize(new Dimension(1024, 160)); // 하단 1/3 크기
        detailPanel.setBorder(BorderFactory.createTitledBorder("대여 가능 일자 / 예약 상세 정보"));

        // 버튼 영역
        JButton checkDateButton = new JButton("대여 가능 일자 조회");
        JButton reserveButton = new JButton("예약하기");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(checkDateButton);
        btnPanel.add(reserveButton);
        detailPanel.add(btnPanel, BorderLayout.SOUTH);

        // 정보 표시용 라벨 (기본 안내 메시지)
        infoLabel = new JLabel("캠핑카를 선택하고 버튼을 클릭하세요.");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailPanel.add(infoLabel, BorderLayout.CENTER);

        add(detailPanel);

        // 버튼 기능 추가
        checkDateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "조회할 캠핑카를 선택하세요.");
                return;
            }
            String carName = table.getValueAt(selectedRow, 0).toString();
            infoLabel.setText(carName + "의 대여 가능 일자는 추후 구현 예정입니다.");
        });

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

            // 예약 화면에 값 넘기기 또는 로그 출력
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
            data[i][2] = car.getCapacity() + "인승";
            data[i][3] = car.getRentalPrice() + "원";
            data[i][4] = String.valueOf(car.getRegistrationDate());
            data[i][5] = car.getDetailInfo();
        }
        return data;
    }
}
