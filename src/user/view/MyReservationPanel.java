package user.view;

import global.entity.Rental;
import global.session.Session;
import user.dao.RentalDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyReservationPanel extends JPanel {

    private JTable reservationTable;
    private DefaultTableModel tableModel;

    public MyReservationPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("나의 예약 내역");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // 테이블 컬럼 설정
        String[] columnNames = {"예약번호", "캠핑카 ID", "대여 시작일", "반납일", "대여일수", "요금"};
        tableModel = new DefaultTableModel(columnNames, 0);
        reservationTable = new JTable(tableModel);
        add(new JScrollPane(reservationTable), BorderLayout.CENTER);

        // 데이터 불러오기
        loadReservationData();
    }

    private void loadReservationData() {
        Long customerId = Session.getCustomerId();
        RentalDao rentalDao = new RentalDao();
        List<Rental> rentals = rentalDao.getReservationsByCustomerId(customerId);

        tableModel.setRowCount(0); // 기존 데이터 초기화
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Rental r : rentals) {
            Object[] row = {
                r.getId(),
                r.getCarId(),
                r.getStartDate().toLocalDate().format(fmt),
                r.getReturnDate().toLocalDate().format(fmt),
                r.getRentalDays(),
                String.format("%,d원", (int) r.getRentalFee())
            };
            tableModel.addRow(row);
        }
    }
}
