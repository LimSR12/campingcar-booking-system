package user.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import user.dao.CompanyDao;
import user.dao.CustomerDao;
import global.entity.Customer;
import global.session.Session;

public class ReservationFormDialog extends JDialog {

    public ReservationFormDialog(JFrame parent, String campingCarName, String plateNumber,
                                  LocalDate checkInDate, LocalDate checkOutDate, ImageIcon carImageIcon) {
        super(parent, "예약 등록", true);
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(700, 500));

        // DAO 호출로 회사명 조회
        CompanyDao companyDao = new CompanyDao();
        String companyName = companyDao.getCompanyNameByCarPlate(plateNumber);

        // 세션에서 로그인한 사용자 ID를 이용해 고객 정보 조회
        CustomerDao customerDao = new CustomerDao();
        Customer customer = customerDao.getCustomerById(Session.getCustomerId());

        String licenseNumber = (customer != null) ? customer.getLicenseNumber() : "[정보 없음]";
        String phone = (customer != null) ? customer.getPhone() : "[정보 없음]";
        String customerName = (customer != null) ? customer.getName() : "[정보 없음]";

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("예약 등록 정보");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        JLabel imageLabel = new JLabel();
        if (carImageIcon != null) {
            imageLabel.setIcon(new ImageIcon(carImageIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH)));
        } else {
            imageLabel.setText("[이미지 없음]");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setPreferredSize(new Dimension(200, 150));
        }
        centerPanel.add(imageLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(createInfoLabel("캠핑카 이름: " + campingCarName));
        infoPanel.add(createInfoLabel("차량 번호: " + plateNumber));
        infoPanel.add(createInfoLabel("캠핑카 회사: " + companyName));
        infoPanel.add(createInfoLabel("대여 시작일: " + checkInDate));
        infoPanel.add(createInfoLabel("반납일: " + checkOutDate));
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(createInfoLabel("사용자 이름: " + customerName));
        infoPanel.add(createInfoLabel("운전면허 번호: " + licenseNumber));
        infoPanel.add(createInfoLabel("핸드폰 번호: " + phone));

        centerPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnSubmit = new JButton("예약");
        JButton btnCancel = new JButton("취소");
        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSubmit.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "예약이 완료되었습니다.");
            dispose();
        });

        add(contentPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
