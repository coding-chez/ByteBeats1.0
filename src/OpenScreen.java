import javax.swing.*;
import java.awt.*;

public class OpenScreen extends JFrame {
    public OpenScreen() {
        // Configure JFrame
        setTitle("ByteBeats");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Custom panel with blue gradient background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Logo
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon("src/assets/logo.png"); 
        Image logoImage = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); 
        logoLabel.setIcon(new ImageIcon(logoImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        mainPanel.add(Box.createVerticalStrut(100)); 
        mainPanel.add(logoLabel);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Where every byte is a beat...");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE); 
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(welcomeLabel);

        // Add continue button
        JButton continueButton = new JButton("Start");
        continueButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT); 
        continueButton.addActionListener(e -> {
            // Open MusicPlayerGUI and dispose of OpenScreen
            new MusicPlayerGUI().setVisible(true);
            dispose();
        });
        mainPanel.add(Box.createVerticalStrut(20)); 
        mainPanel.add(continueButton);

        // Add main panel to JFrame
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OpenScreen openScreen = new OpenScreen();
            openScreen.setVisible(true);
        });
    }

    // Custom JPanel with blue gradient background
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(59, 128, 194), 
                    0, getHeight(), new Color(200, 200, 228) 
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
