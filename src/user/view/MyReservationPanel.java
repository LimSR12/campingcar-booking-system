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
        
        // 삭제 버튼 추가
        JButton deleteButton = new JButton("선택 삭제");
        deleteButton.addActionListener(e -> deleteSelectedReservation());
        add(deleteButton, BorderLayout.SOUTH);
    }

    // 예약정보 받아오는 메서드
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
    
    // 선택한 예약정보 삭제하는 메서드
    private void deleteSelectedReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 예약을 선택하세요.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "정말로 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Long rentalId = (Long) tableModel.getValueAt(selectedRow, 0);
        RentalDao rentalDao = new RentalDao();
        rentalDao.deleteByRentalId(rentalId);

        loadReservationData();  // 삭제 후 테이블 새로고침
        JOptionPane.showMessageDialog(this, "삭제가 완료되었습니다.");
    }

}
