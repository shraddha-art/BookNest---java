import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("BookNest");
        setSize(480, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);

        // Main panel with taupe background
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(200, 191, 176)); // warm taupe

        // White card panel
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBounds(60, 60, 360, 370);

        // Title
        JLabel lblTitle = new JLabel("Login");
        lblTitle.setBounds(30, 30, 200, 35);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 26));
        lblTitle.setForeground(new Color(61, 43, 31));

        // Subtitle
        //JLabel lblSub = new JLabel("<html>Access your account to explore and<br>manage your records.</html>");
        JLabel lblSub = new JLabel();
        lblSub.setBounds(30, 68, 290, 40);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(new Color(120, 105, 95));

        // Email label
        JLabel lblEmail = new JLabel("Username");
        lblEmail.setBounds(30, 120, 200, 20);
        lblEmail.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblEmail.setForeground(new Color(61, 43, 31));

        // Email field
        JTextField txtUsername = new JTextField();
        txtUsername.setBounds(30, 142, 300, 36);
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(210, 200, 190), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtUsername.setBackground(new Color(252, 250, 248));
        txtUsername.setForeground(new Color(61, 43, 31));

        // Password label
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(30, 192, 200, 20);
        lblPassword.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPassword.setForeground(new Color(61, 43, 31));

        // Password field
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(30, 214, 300, 36);
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(210, 200, 190), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtPassword.setBackground(new Color(252, 250, 248));

        // Login button
        JButton btnLogin = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(107, 80, 72));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnLogin.setBounds(30, 272, 100, 36);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sign up link
        JLabel lblSignup = new JLabel();
        lblSignup.setBounds(30, 322, 280, 20);
        lblSignup.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSignup.setForeground(new Color(120, 105, 95));

        card.add(lblTitle);
        card.add(lblSub);
        card.add(lblEmail);
        card.add(txtUsername);
        card.add(lblPassword);
        card.add(txtPassword);
        card.add(btnLogin);
        card.add(lblSignup);

        mainPanel.add(card);
        add(mainPanel);

        // Login action
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            if (username.equals("admin") && password.equals("admin")) {
                new Dashboard();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm());
    }
}