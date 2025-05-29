package user.view;

import javax.swing.*;
import java.awt.*;

public class ReservationPanel extends JPanel {
    public ReservationPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("예약 확인 화면입니다."), BorderLayout.CENTER);
    }
}
