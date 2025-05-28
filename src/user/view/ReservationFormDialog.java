package user.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ReservationFormDialog extends JDialog {

    public ReservationFormDialog(JFrame parent, String campingCarName, String plateNumber,
                                  LocalDate checkInDate, LocalDate checkOutDate) {
        super(parent, "예약 등록", true); // true = 모달로 띄움
        setLayout(new BorderLayout());

        // 기본 정보 출력
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.add(new JLabel("캠핑카 이름: " + campingCarName));
        infoPanel.add(new JLabel("차량 번호: " + plateNumber));
        infoPanel.add(new JLabel("대여 시작일: " + checkInDate));
        infoPanel.add(new JLabel("반납일: " + checkOutDate));

        // 추가 입력 예시 (고객명)
        JPanel formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(new JLabel("고객명:"));
        JTextField customerNameField = new JTextField();
        formPanel.add(customerNameField);

        // 버튼
        JPanel btnPanel = new JPanel();
        JButton btnSubmit = new JButton("예약");
        JButton btnCancel = new JButton("취소");
        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);

        btnCancel.addActionListener(e -> dispose());
        btnSubmit.addActionListener(e -> {
            String customerName = customerNameField.getText();
            if (customerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "고객명을 입력하세요.");
                return;
            }
            // TODO: 예약 정보 DB 저장 로직 추가
            System.out.println("예약됨: " + customerName + ", 차량: " + plateNumber);
            dispose();
        });

        add(infoPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }
}
