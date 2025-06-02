package admin.view;

import admin.dao.*;
import global.entity.*;
import global.util.DialogUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class CarHistoryDialog extends JDialog {
    /* DAOs */
    private final InternalMaintenanceDao  imDao = new InternalMaintenanceDao();
    private final ExternalMaintenanceDao  emDao = new ExternalMaintenanceDao();
    private final PartInventoryDao        partDao = new PartInventoryDao();
    private final ExternalCenterDao       centerDao = new ExternalCenterDao();

    /* UI */
    private final JTable tblInternal = new JTable();
    private final JTable tblExternal = new JTable();
    private final ExternalCenterInfoPanel pnlCenterInfo = new ExternalCenterInfoPanel();

    private final long carId;
    
    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CarHistoryDialog(Frame parent, long carId) {
        super(parent, "캠핑카 정비 내역 (ID="+carId+")", true);
        this.carId = carId;
        buildUI();
        loadInternalTable();
        tblInternal.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount()==2 && tblInternal.getSelectedRow()!=-1) {
                    long partId = extractPartId(tblInternal.getSelectedRow());
                    showPartDetail(partId);
                }
            }
        });
        tblInternal.getSelectionModel()
        .addListSelectionListener(this::internalRowChanged);
        
        loadExternalTable();
        
        // 첫 행 자동선택
        if (tblExternal.getRowCount() > 0)
            tblExternal.setRowSelectionInterval(0,0);
        setSize(900, 500);
        setLocationRelativeTo(parent);
    }

    /* ───────────────────────────────────── UI 레이아웃 */
    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();

        /* 내부 정비 탭 */
        tblInternal.setRowHeight(24);
        JScrollPane spInt = new JScrollPane(tblInternal);
        tabs.addTab("내부 정비", spInt);

        /* 외부 정비 탭 : 위 테이블 + 아래 센터정보 */
        JPanel extPanel = new JPanel(new BorderLayout(6,6));
        tblExternal.setRowHeight(24);
        extPanel.add(new JScrollPane(tblExternal), BorderLayout.CENTER);
        extPanel.add(pnlCenterInfo  , BorderLayout.SOUTH);
        tabs.addTab("외부 정비", extPanel);

        getContentPane().add(tabs, BorderLayout.CENTER);

        /* 내부-부품 더블클릭 → PartDetailDialog */
        tblInternal.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount()==2 && tblInternal.getSelectedRow()!=-1) {
                    long partId = (long) tblInternal.getValueAt(tblInternal.getSelectedRow(), /*part_id colIdx*/2);
                    showPartDetail(partId);
                }
            }
        });

        /* 외부 테이블 선택 → 센터 정보 채우기 */
        tblExternal.getSelectionModel().addListSelectionListener(this::externalRowChanged);
    }

    /* ───────────────────────────────────── 데이터 로딩 */
    private void loadInternalTable() {
        try {
            List<InternalMaintenance> list = imDao.findByCarId(carId);

            String[] cols = { "ID","Car","Part","Staff","날짜","시간(m)","설명" };
            DefaultTableModel model = new DefaultTableModel(cols, 0);

            for (InternalMaintenance im : list) {
                model.addRow(new Object[]{
                    im.getId(), im.getCarId(), im.getPartId(),
                    im.getStaffId(), im.getRepairDate(),
                    im.getDuration(), im.getDescription()
                });
            }
            tblInternal.setModel(model);
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }
    
    private void loadExternalTable() {
        try {
            List<ExternalMaintenance> list = emDao.findByCarId(carId);

            String[] cols = { "ID","Center","고객","회사","날짜","요금","내용" };
            DefaultTableModel model = new DefaultTableModel(cols, 0);

            for (ExternalMaintenance em : list) {
                model.addRow(new Object[]{
                    em.getId(),
                    em.getCenterId(),
                    em.getCustomerId(),
                    em.getCompanyId(),
                    em.getRepairDate(),
                    em.getRepairFee(),
                    em.getRepairDetails()
                });
            }
            tblExternal.setModel(model);

        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }

    /* ───────────────────────────────────── 콜백 */
    private void showPartDetail(long partId) {
        try {
            PartInventory p = partDao.findById(partId);
            if (p == null) {
                DialogUtil.showWarning(this, "부품 정보를 찾을 수 없습니다.");
                return;
            }

            String received = p.getReceivedDate() != null
                            ? p.getReceivedDate().format(DT_FMT)  // ← 여기만 변경
                            : "-";

            String msg = String.format(
                "부품명: %s%n가격: %,d%n재고: %d%n입고일: %s%n공급사: %s",
                p.getName(),
                (int) p.getPrice(),
                p.getQuantity(),
                received,
                p.getSupplierName()
            );
            DialogUtil.showInfo(this, msg);

        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }

    private void externalRowChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = tblExternal.getSelectedRow();
        if (row == -1) return;
        long centerId = (long) tblExternal.getValueAt(row, 1);
        try {
            ExternalCenter c = centerDao.findById(centerId);
            pnlCenterInfo.load(c);
        } catch (SQLException ex) {
            DialogUtil.showError(this, ex.getMessage());
        }
    }
    
    private long extractPartId(int row) {
        // 내부 정비 테이블에서 "Part" 컬럼(index를 컬럼명으로 검색)
        int partCol = tblInternal.getColumnModel().getColumnIndex("Part");
        Object val  = tblInternal.getValueAt(row, partCol);
        return (val instanceof Number) ? ((Number)val).longValue() : -1;
    }

    /* ------------- NEW: 선택 변경 콜백 ------------- */
    private void internalRowChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;          // 최종 확정 시점만 처리
        int row = tblInternal.getSelectedRow();
        if (row == -1) return;                        // 선택 해제
        long partId = extractPartId(row);
        if (partId > 0) showPartDetail(partId);
        
        tblInternal.clearSelection();
    }

    /* ───────────────────────────────────── 센터 상세 패널 (하단) */
    private static class ExternalCenterInfoPanel extends JPanel {
        private final JLabel lbName=new JLabel(), lbAddr=new JLabel(),
                             lbPhone=new JLabel(), lbMgr=new JLabel();

        ExternalCenterInfoPanel() {
            setBorder(BorderFactory.createTitledBorder("외부 정비소 정보"));
            setLayout(new GridLayout(0,2,4,2));
            add(new JLabel("센터명:")); add(lbName);
            add(new JLabel("주소:"));   add(lbAddr);
            add(new JLabel("전화:"));   add(lbPhone);
            add(new JLabel("담당자:")); add(lbMgr);
        }
        void load(ExternalCenter c){
            if(c==null){lbName.setText("-"); lbAddr.setText(""); lbPhone.setText(""); lbMgr.setText(""); return;}
            lbName .setText(c.getName());
            lbAddr .setText(c.getAddress());
            lbPhone.setText(c.getPhone());
            lbMgr  .setText(c.getManagerName()+" / "+c.getManagerEmail());
        }
    }
}
