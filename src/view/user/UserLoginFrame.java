package view.user;

import javax.swing.*;
import dao.LoginDao;
import util.DialogUtil;

public class UserLoginFrame extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;

    public UserLoginFrame() {
        setTitle("로그인");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 아이디 패널
        JPanel idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
        JLabel idLabel = new JLabel("아이디: ");
        idLabel.setHorizontalAlignment(SwingConstants.LEFT);
        idLabel.setPreferredSize(new java.awt.Dimension(70, 30));
        userIdField = new JTextField();
        idPanel.add(idLabel);
        idPanel.add(userIdField);

        // 비밀번호 패널
        JPanel pwPanel = new JPanel();
        pwPanel.setLayout(new BoxLayout(pwPanel, BoxLayout.X_AXIS));
        JLabel pwLabel = new JLabel("비밀번호: ");
        pwLabel.setHorizontalAlignment(SwingConstants.LEFT);
        pwLabel.setPreferredSize(new java.awt.Dimension(70, 30));
        passwordField = new JPasswordField();
        pwPanel.add(pwLabel);
        pwPanel.add(passwordField);

        // 로그인 버튼
        JPanel btnPanel = new JPanel();
        JButton loginBtn = new JButton("로그인");
        loginBtn.addActionListener(e -> handleLogin()); // 버튼 누를 때 실행
        btnPanel.add(loginBtn);

        // 메인 패널에 추가
        mainPanel.add(idPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(pwPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(btnPanel);

        add(mainPanel);
        setVisible(true);
    }
    
    private void handleLogin() {
        String inputId = userIdField.getText();
        String inputPw = new String(passwordField.getPassword());

        if (inputId.isEmpty() || inputPw.isEmpty()) {
        	DialogUtil.showWarning(this, "올바르지 않은 입력입니다.");
            return;
        }

        // DB 연결 및 검증
        boolean isValid = LoginDao.verifyLogin(inputId, inputPw);

        if (isValid) {
            JOptionPane.showMessageDialog(this, "로그인 성공!");
            dispose(); // 현재 로그인 창 닫기
            new MainFrame();
        } else {
            JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 틀렸습니다.");
        }
    }

}
