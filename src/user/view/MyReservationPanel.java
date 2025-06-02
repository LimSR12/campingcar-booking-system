package user.view;

import global.entity.Rental;
import global.session.Session;
import user.dao.RentalDao;
import user.dao.RentalDao.CampingCarDto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
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
        JButton deleteButton = new JButton("선택한 예약내역 삭제");
        deleteButton.addActionListener(e -> deleteSelectedReservation());
        
        // 캠핑카 변경 버튼 추가
        JButton changeCarButton = new JButton("예약한 캠핑카 변경");
        changeCarButton.addActionListener(e -> changeCampingCar());
        
        // 예약 일정 변경 버튼 추가
        JButton changeDateButton = new JButton("예약 일정 변경");
        changeDateButton.addActionListener(e -> changeRentalDates());

        // 버튼 패널
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(deleteButton);
        btnPanel.add(changeCarButton);
        btnPanel.add(changeDateButton);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // 예약정보 받아오는 메서드
    public void loadReservationData() {
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

    // 캠핑카 예약 변경하는 메서드
    private void changeCampingCar() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "변경할 예약을 선택하세요.");
            return;
        }

        Long rentalId = (Long) tableModel.getValueAt(selectedRow, 0);
        RentalDao rentalDao = new RentalDao();
        List<CampingCarDto> carList = rentalDao.getAllCampingCars();

        if (carList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "변경 가능한 캠핑카가 없습니다.");
            return;
        }

        JComboBox<CampingCarDto> comboBox = new JComboBox<>(carList.toArray(new CampingCarDto[0]));
        int result = JOptionPane.showConfirmDialog(this, comboBox, "변경할 캠핑카 선택", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            CampingCarDto selectedCar = (CampingCarDto) comboBox.getSelectedItem();
            boolean success = rentalDao.updateCampingCar(rentalId, selectedCar.getId());

            if (success) {
                JOptionPane.showMessageDialog(this, "캠핑카가 성공적으로 변경되었습니다.");
                loadReservationData();
            } else {
                JOptionPane.showMessageDialog(this, "변경에 실패했습니다.");
            }
        }
    }
    
    // 예약 일정 변경하는 메서드
    private void changeRentalDates() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "변경할 예약을 선택하세요.");
            return;
        }

        Long rentalId = (Long) tableModel.getValueAt(selectedRow, 0);
        String startInput = JOptionPane.showInputDialog(this, "새 대여 시작일을 입력하세요 (yyyy-MM-dd):");
        String endInput = JOptionPane.showInputDialog(this, "새 반납일을 입력하세요 (yyyy-MM-dd):");

        try {
            LocalDate newStart = LocalDate.parse(startInput);
            LocalDate newEnd = LocalDate.parse(endInput);

            if (newEnd.isBefore(newStart)) {
                JOptionPane.showMessageDialog(this, "반납일은 대여 시작일 이후여야 합니다.");
                return;
            }

            RentalDao dao = new RentalDao();
            boolean success = dao.updateRentalDates(rentalId, newStart, newEnd);

            if (success) {
                JOptionPane.showMessageDialog(this, "날짜가 성공적으로 변경되었습니다.");
                loadReservationData();
            } else {
                JOptionPane.showMessageDialog(this, "날짜 변경에 실패했습니다.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "날짜 형식을 확인하세요 (예: 2025-06-01)");
        }
    }


}
