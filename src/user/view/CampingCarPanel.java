package user.view;

import javax.swing.*;

import global.entity.CampingCar;
import user.dao.CampingCarDao;
import user.dao.RentalDao;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class CampingCarPanel extends JPanel {
    private List<CampingCar> carList;
    private final int columns = 8;
    private JTable table;
    private JPanel calendarContainer;          // 교체 가능한 달력 컨테이너
    private RentalCalendarPanel calendarPanel; // 하단 달력 패널

    public CampingCarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 캠핑카 테이블 설정
        String[] columnNames = {"", "캠핑카 이름", "차량 번호", "승차 인원", "대여 비용", "차량 등록일자", "차량 세부정보", "이미지 경로"};
        String[][] data = loadCampingCarData();
        
        table = new JTable(data, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // 캠핑카 이름
        table.getColumnModel().getColumn(2).setPreferredWidth(70);	// 차량 번호
        table.getColumnModel().getColumn(3).setPreferredWidth(70);	// 승차 인원
        table.getColumnModel().getColumn(4).setPreferredWidth(70);	// 대여 비용
        table.getColumnModel().getColumn(5).setPreferredWidth(130);	// 차량 등록일자
        table.getColumnModel().getColumn(6).setPreferredWidth(250);	// 차량 세부 정보
        
        table.getColumnModel().getColumn(0).setMinWidth(0); // 0번 컬럼 : car id -> 숨기기
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.getColumnModel().getColumn(7).setMinWidth(0); // 7번 컬럼 : 이미지 -> 숨기기
        table.getColumnModel().getColumn(7).setMaxWidth(0);
        table.getColumnModel().getColumn(7).setWidth(0);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(1024, 480)); // 상단
        add(tableScroll);

        // 하단 상세정보 패널
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setPreferredSize(new Dimension(1024, 240));
        detailPanel.setBorder(BorderFactory.createTitledBorder("대여 가능 일자 / 예약 상세 정보"));

        // 버튼 영역
        JButton checkDateButton = new JButton("대여 가능 일자 조회");
        JButton reserveButton = new JButton("예약하기");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(checkDateButton);
        btnPanel.add(reserveButton);
        detailPanel.add(btnPanel, BorderLayout.SOUTH);

        // 달력 컨테이너 (처음엔 안내 문구)
        calendarContainer = new JPanel(new BorderLayout());
        calendarContainer.setPreferredSize(new Dimension(1024, 180));
        JLabel guideLabel = new JLabel("캠핑카를 선택하고 대여 가능 일자를 조회하세요.", SwingConstants.CENTER);
        calendarContainer.add(guideLabel, BorderLayout.CENTER);
        detailPanel.add(calendarContainer, BorderLayout.CENTER);

        add(detailPanel);

        // "대여 가능 일자 조회" 버튼 이벤트 처리
        checkDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "캠핑카를 선택하세요.");
                    return;
                }
                String plateNumber = table.getValueAt(selectedRow, 2).toString();
                RentalDao dao = new RentalDao();
                Set<LocalDate> reservedDates = dao.getReservedDatesByPlate(plateNumber);
                System.out.println("예약된 날짜 개수: " + reservedDates.size());
                for (LocalDate d : reservedDates) {
                    System.out.println("예약된 날짜: " + d);
                }

                calendarPanel = new RentalCalendarPanel(reservedDates, 2025, 5);
                calendarContainer.removeAll();
                calendarContainer.add(calendarPanel, BorderLayout.CENTER);
                calendarContainer.revalidate();
                calendarContainer.repaint();
            }
        });

        // "예약하기" 버튼 이벤트 처리
        reserveButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "예약할 캠핑카를 선택하세요.");
                return;
            }

            // 캠핑카 정보 추출
            Long carId = Long.valueOf(table.getValueAt(selectedRow, 0).toString());
            String campingCarName = table.getValueAt(selectedRow, 1).toString();
            String plateNumber = table.getValueAt(selectedRow, 2).toString();
            
            // 날짜 선택 정보
            LocalDate checkInDate = calendarPanel.getCheckInDate();
            LocalDate checkOutDate = calendarPanel.getCheckOutDate();

            if (checkInDate == null || checkOutDate == null) {
                JOptionPane.showMessageDialog(null, "대여 시작일과 반납일을 선택하세요.");
                return;
            }

            // 이미지 경로 전달
            String imagePath = table.getValueAt(selectedRow, 7).toString();
            ImageIcon carImage = null;

            // 다이얼로그 호출
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ReservationFormDialog dialog = new ReservationFormDialog(
                topFrame,
                carId,
                campingCarName,
                plateNumber,
                checkInDate,
                checkOutDate,
                Integer.parseInt(table.getValueAt(selectedRow, 4).toString().replaceAll("[^0-9]", "")),
                new ImageIcon(imagePath)
            );
            dialog.setVisible(true);
        });

    }

    private String[][] loadCampingCarData() {
        CampingCarDao dao = new CampingCarDao();
        List<CampingCar> list = dao.getAllCampingCars();
        String[][] data = new String[list.size()][columns];

        for (int i = 0; i < list.size(); i++) {
            CampingCar car = list.get(i);
            data[i][0] = car.getId().toString();
            data[i][1] = car.getName();
            data[i][2] = car.getPlateNumber();
            data[i][3] = car.getCapacity() + "인승";
            data[i][4] = car.getRentalPrice() + "원";
            data[i][5] = String.valueOf(car.getRegistrationDate());
            data[i][6] = car.getDetailInfo();
            data[i][7] = car.getImage();
        }

        return data;
    }
}