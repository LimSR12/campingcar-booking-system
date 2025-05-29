package user.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RentalCalendarPanel extends JPanel {

    private Map<LocalDate, JLabel> dateLabelMap = new HashMap<>();
    private Set<LocalDate> reservedDates = new HashSet<>();
    private LocalDate startDate = null; 
    private LocalDate endDate = null;
    private JPanel calendarGrid;

    public RentalCalendarPanel(Set<LocalDate> reservedDates, int year, int month) {
    	this.reservedDates = reservedDates;
    	
        setLayout(new BorderLayout());

        JLabel title = new JLabel(year + "년 " + month + "월", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        calendarGrid = new JPanel(new GridLayout(5, 7));
        add(calendarGrid, BorderLayout.CENTER);

        drawCalendar(reservedDates, year, month);
    }

    // year, month 기준으로 달력 그려주는 메서드
    private void drawCalendar(Set<LocalDate> reservedDates, int year, int month) {
        calendarGrid.removeAll();
        dateLabelMap.clear();

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;

        LocalDate datePointer = firstDay.minusDays(startDayOfWeek);

        for (int i = 0; i < 35; i++) {
            LocalDate current = datePointer.plusDays(i);
            JLabel label = new JLabel(String.valueOf(current.getDayOfMonth()), SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // 예약된 날짜 처리
            if (reservedDates.contains(current)) {
                label.setBackground(Color.LIGHT_GRAY);
                label.setEnabled(false);
            } else if (current.getMonthValue() == month) {
                label.setBackground(Color.WHITE);
                label.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        handleDateClick(current);
                    }
                });
            } else {
                label.setBackground(Color.DARK_GRAY);
                label.setForeground(Color.LIGHT_GRAY);
            }

            calendarGrid.add(label);
            dateLabelMap.put(current, label);
        }

        revalidate();
        repaint();
    }

    // 선택한 날짜 핸들러 메서드
    private void handleDateClick(LocalDate clicked) {
    	// 첫 클릭: 시작일 설정
        if (startDate == null || (startDate != null && endDate != null)) {
            startDate = clicked;
            endDate = null;
            resetAllToWhite();
            highlightRange(startDate, startDate);
        }
        // 두 번째 클릭: 반납일 설정
        else if (startDate != null && endDate == null) {
            if (!clicked.isBefore(startDate)) {
            	if (hasReservedDateBetween(startDate, clicked)) {
                    JOptionPane.showMessageDialog(this, "해당 기간에는 이미 예약된 날짜가 포함되어 있습니다.");
                    startDate = null;
                    endDate = null;
                    return;
                }
                endDate = clicked;
                resetAllToWhite();
                highlightRange(startDate, endDate);
            } else {
                // 이전 날짜 클릭 시 다시 시작
                startDate = clicked;
                resetAllToWhite();
                highlightRange(startDate, startDate);
            }
        }
    }
    
    private boolean hasReservedDateBetween(LocalDate start, LocalDate end) {
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (reservedDates.contains(date)) {
                return true;
            }
        }
        return false;
    }

    private void highlightRange(LocalDate start, LocalDate end) {
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (dateLabelMap.containsKey(d)) {
                dateLabelMap.get(d).setBackground(new Color(119, 221, 119));
            }
        }
    }

    private void resetAllToWhite() {
        for (Map.Entry<LocalDate, JLabel> entry : dateLabelMap.entrySet()) {
            LocalDate date = entry.getKey();
            JLabel label = entry.getValue();
            if (label.isEnabled()) {
                label.setBackground(Color.WHITE);
            }
        }
    }
    
    // 예약 취소 시 기존 선택한 날짜 값 초기화하는 메서드 
    public void resetSelection() {
    	startDate = null;
    	endDate = null;
        resetAllToWhite();
    }


    // 선택된 날짜 범위 반환용
    public LocalDate getCheckInDate() {
        return startDate;
    }

    public LocalDate getCheckOutDate() {
        return endDate;
    }
}
