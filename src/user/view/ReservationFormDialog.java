package user.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import user.dao.CompanyDao;
import user.dao.CustomerDao;
import user.dao.RentalDao;
import global.entity.Customer;
import global.entity.Rental;
import global.session.Session;

public class ReservationFormDialog extends JDialog {

    public ReservationFormDialog(JFrame parent, Long carId, String campingCarName, String plateNumber, LocalDate startDate, LocalDate endDate, int pricePerDay, ImageIcon carImageIcon) {
        super(parent, "예약 등록", true);
        setPreferredSize(new Dimension(700, 400));
        setLayout(new BorderLayout(10, 10));

        CompanyDao companyDao = new CompanyDao();
        CustomerDao customerDao = new CustomerDao();
        RentalDao rentalDao = new RentalDao();

        String companyName = companyDao.getCompanyNameByCarId(carId);
        Customer customer = customerDao.getCustomerById(Session.getCustomerId());

        String licenseNumber = customer != null ? customer.getLicenseNumber() : "[정보 없음]";
        String phone = customer != null ? customer.getPhone() : "[정보 없음]";
        String customerName = customer != null ? customer.getName() : "[정보 없음]";

        // 제목
        JLabel titleLabel = new JLabel("예약 등록 정보");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // 중앙 내용
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        centerPanel.setBackground(Color.LIGHT_GRAY);

        // 왼쪽 이미지
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(216, 171));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        if (carImageIcon != null) {
            imageLabel.setIcon(new ImageIcon(carImageIcon.getImage().getScaledInstance(216, 171, Image.SCALE_SMOOTH)));
        } else {
            imageLabel.setText("이미지");
        }

        // 오른쪽 정보
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(300, 170));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(createInfoLabel("캠핑카 이름: " + campingCarName));
        infoPanel.add(createInfoLabel("차량 번호: " + plateNumber));
        infoPanel.add(createInfoLabel("캠핑카 회사: " + companyName));
        infoPanel.add(createInfoLabel("대여 시작일: " + startDate));
        infoPanel.add(createInfoLabel("반납일: " + endDate));
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(createInfoLabel("사용자 이름: " + customerName));
        infoPanel.add(createInfoLabel("운전면허 번호: " + licenseNumber));
        infoPanel.add(createInfoLabel("핸드폰 번호: " + phone));

        centerPanel.add(imageLabel);
        centerPanel.add(infoPanel);
        add(centerPanel, BorderLayout.CENTER);

        // 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnSubmit = new JButton("예약");
        JButton btnCancel = new JButton("취소");
        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());

        btnSubmit.addActionListener(e -> {
            long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);
            Rental rental = new Rental();
            rental.setCarId(carId);
            rental.setCustomerId(Session.getCustomerId());
            rental.setLicenseNumber(customer.getLicenseNumber());
            rental.setCompanyId(companyDao.getCompanyIdByCarId(carId));
            rental.setStartDate(startDate.atStartOfDay());
            rental.setReturnDate(endDate.atStartOfDay());
            rental.setRentalDays((int) rentalDays);
            rental.setRentalFee(pricePerDay);
            rental.setFeeDueDate(endDate.plusDays(7).atStartOfDay());
            rental.setExtraDetails(null);
            rental.setExtraFee(null);

          //System.out.println("=== 예약 정보 확인 ===");
          //System.out.println("carId: " + rental.getCarId());
          //System.out.println("customerId: " + rental.getCustomerId());
          //System.out.println("licenseNumber: " + rental.getLicenseNumber());
          //System.out.println("companyId: " + rental.getCompanyId());
          //System.out.println("startDate: " + rental.getStartDate());
          //System.out.println("returnDate: " + rental.getReturnDate());
          //System.out.println("rentalDays: " + rental.getRentalDays());
          //System.out.println("rentalFee: " + rental.getRentalFee());
          //System.out.println("feeDueDate: " + rental.getFeeDueDate());
          //System.out.println("extraDetails: " + rental.getExtraDetails());
          //System.out.println("extraFee: " + rental.getExtraFee());
          //System.out.println("======================");
            
            boolean reserved = rentalDao.insertRental(rental);
            if (reserved) {
                JOptionPane.showMessageDialog(this, "예약이 완료되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "예약을 실패했습니다.");
            }
            dispose();
        });

        pack();
        setLocationRelativeTo(parent);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return label;
    }
}


