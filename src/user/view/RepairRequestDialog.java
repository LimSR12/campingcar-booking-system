package user.view;

import global.entity.ExternalMaintenance;
import global.entity.Customer;
import global.session.Session;
import user.dao.CompanyDao;
import user.dao.CustomerDao;
import user.dao.ExternalCenterDao;
import user.dao.ExternalMaintenanceDao;
import user.dao.ExternalCenterDto;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RepairRequestDialog extends JDialog {

    public RepairRequestDialog(JFrame parent, Long carId, Long rentalId) {
        super(parent, "외부 정비 등록", true);
        setSize(500, 500);
        setLayout(new BorderLayout(10, 10));

        // DAO 및 사용자 정보 조회
        ExternalCenterDao centerDao = new ExternalCenterDao();
        ExternalMaintenanceDao repairDao = new ExternalMaintenanceDao();
        CustomerDao customerDao = new CustomerDao();
        CompanyDao companyDao = new CompanyDao();

        Long customerId = Session.getCustomerId();
        Customer customer = customerDao.getCustomerById(customerId);
        String licenseNumber = customer.getLicenseNumber();
        Long companyId = companyDao.getCompanyIdByCarId(carId);

        // 외부 정비소 목록 조회
        List<ExternalCenterDto> centerList = centerDao.getAllCenters();
        JComboBox<ExternalCenterDto> centerCombo = new JComboBox<>(centerList.toArray(new ExternalCenterDto[0]));

        // 입력 필드
        JTextField dateField = new JTextField(10); // yyyy-MM-dd
        JTextField feeField = new JTextField(10);
        JTextArea detailsArea = new JTextArea(3, 30);
        JTextArea extraArea = new JTextArea(2, 30);

        JPanel form = new JPanel();
        form.setLayout(new GridLayout(0, 1, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        form.add(new JLabel("외부 정비소:"));
        form.add(centerCombo);
        form.add(new JLabel("정비 날짜 (yyyy-MM-dd):"));
        form.add(dateField);
        form.add(new JLabel("정비 비용:"));
        form.add(feeField);
        form.add(new JLabel("정비 내용:"));
        form.add(new JScrollPane(detailsArea));
        form.add(new JLabel("기타 정비 내용:"));
        form.add(new JScrollPane(extraArea));

        // 버튼
        JButton submitBtn = new JButton("등록");
        JButton cancelBtn = new JButton("취소");

        submitBtn.addActionListener(e -> {
            try {
                ExternalCenterDto selected = (ExternalCenterDto) centerCombo.getSelectedItem();
                LocalDate repairDate = LocalDate.parse(dateField.getText());
                double fee = Double.parseDouble(feeField.getText());
                String details = detailsArea.getText();
                String extra = extraArea.getText();

                ExternalMaintenance m = new ExternalMaintenance();
                m.setCarId(carId);
                m.setCustomerId(customerId);
                m.setLicenseNumber(licenseNumber);
                m.setCompanyId(companyId);
                m.setCenterId(selected.getId());
                m.setRepairDate(repairDate.atStartOfDay());
                m.setRepairFee(fee);
                m.setRepairDetails(details);
                m.setExtraDetails(extra);
                m.setFeeDueDate(repairDate.plusDays(7));

                boolean result = repairDao.insert(m);
                if (result) {
                    JOptionPane.showMessageDialog(this, "정비 등록 완료!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "등록에 실패했습니다.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력값 오류: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(parent);
    }
}
