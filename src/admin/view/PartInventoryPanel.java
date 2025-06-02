package admin.view;

import admin.dao.InternalMaintenanceDao;
import admin.dao.PartInventoryDao;
import global.entity.PartInventory;
import global.util.DialogUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * part_inventory 전용 CRUD 패널
 */
public class PartInventoryPanel extends AbstractTableCRUDPanel<PartInventory> {
    private JTextField tfName;
    private JTextField tfPrice;
    private JSpinner spQuantity;
    private JSpinner spReceivedDate;
    private JTextField tfSupplierName;

    public PartInventoryPanel() {
        super(new PartInventoryDao());
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));

        // 부품명 (VARCHAR NOT NULL)
        tfName = new JTextField();
        p.add(new JLabel("부품명"));
        p.add(tfName);

        // 가격 (DECIMAL >= 0)
        tfPrice = new JTextField();
        p.add(new JLabel("가격"));
        p.add(tfPrice);

        // 수량 (INT >= 0, 기본값 0)
        spQuantity = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        p.add(new JLabel("수량"));
        p.add(spQuantity);

        // 입고일 (DATETIME NOT NULL) → 스피너로 날짜/시간 입력
        spReceivedDate = new JSpinner(new SpinnerDateModel());
        spReceivedDate.setEditor(new JSpinner.DateEditor(spReceivedDate, "yyyy-MM-dd HH:mm:ss"));
        p.add(new JLabel("입고일"));
        p.add(spReceivedDate);

        // 공급자 이름 (VARCHAR NOT NULL)
        tfSupplierName = new JTextField();
        p.add(new JLabel("공급자 이름"));
        p.add(tfSupplierName);

        // 버튼 (저장/취소)
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("저장");
        JButton cancel = new JButton("취소");
        save.addActionListener(e -> saveForm());
        cancel.addActionListener(e -> showView());
        btns.add(save);
        btns.add(cancel);

        p.add(new JLabel());
        p.add(btns);

        return p;
    }

    @Override
    protected void clearAndShowForm() {
        tfName.setText("");
        tfPrice.setText("");
        spQuantity.setValue(0);
        spReceivedDate.setValue(new Date());
        tfSupplierName.setText("");
        cards.show(cardPane, "FORM");
    }

    @Override
    protected void saveForm() {
        // 이름 입력 확인
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "부품명을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfName.requestFocus();
            return;
        }

        // 가격 입력 확인 (>= 0)
        String priceText = tfPrice.getText().trim();
        double price;
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "가격을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPrice.requestFocus();
            return;
        }
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "가격은 0 이상이어야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                tfPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "가격은 숫자로만 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfPrice.requestFocus();
            return;
        }

        // 수량 확인 (INT >= 0)
        int quantity = (Integer) spQuantity.getValue();
        if (quantity < 0) {
            JOptionPane.showMessageDialog(this, "수량은 0 이상이어야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            spQuantity.requestFocus();
            return;
        }

        // 입고일 확인
        Date receivedDateValue = (Date) spReceivedDate.getValue();
        if (receivedDateValue == null) {
            JOptionPane.showMessageDialog(this, "입고일을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            spReceivedDate.requestFocus();
            return;
        }

        // 공급자 이름 확인
        String supplier = tfSupplierName.getText().trim();
        if (supplier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "공급자 이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            tfSupplierName.requestFocus();
            return;
        }

        // DAO로 저장
        try {
            PartInventory part = new PartInventory();
            part.setName(name);
            part.setPrice(price);
            part.setQuantity(quantity);

            // DATETIME → LocalDateTime
            LocalDateTime ldtReceived = receivedDateValue.toInstant()
                                                      .atZone(ZoneId.systemDefault())
                                                      .toLocalDateTime();
            part.setReceivedDate(ldtReceived);

            part.setSupplierName(supplier);

            dao.insert(part);
        } catch (SQLException ex) {
            DialogUtil.showError(this, "저장 실패:\n" + ex.getMessage());
            return;
        }

        showView();
    }
    
	@Override
	protected void openUpdateByConditionDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        PartInventoryDao eDao = (PartInventoryDao) super.dao;
        PartInventoryUpdate dlg = new PartInventoryUpdate(parentFrame, eDao, this::refreshTable);
        dlg.setVisible(true);
	}

	@Override
	protected void openDeleteByConditionDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        PartInventoryDao eDao = (PartInventoryDao) super.dao;
        PartInventoryDelete dlg = new PartInventoryDelete(parentFrame, eDao, this::refreshTable);
        dlg.setVisible(true);
	}
}
