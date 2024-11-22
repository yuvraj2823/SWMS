import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

public class WeatherMonitoringSystem extends JFrame {
    // Database Configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/weather_monitoring_db";
    private static final String DB_USER = "weatheradmin";
    private static final String DB_PASSWORD = "secureWeather2024!";

    // UI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Connection connection;

    // User Session
    private int currentUserId = -1;
    private String currentUsername = "";

    // Styling Constants
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(41, 128, 185);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public WeatherMonitoringSystem() {
        initializeDatabase();
        initializeUI();
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTablesIfNotExist();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Database Error: " + e.getMessage(), 
                "Connection Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createTablesIfNotExist() throws SQLException {
        String[] createTableQueries = {
            "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL" +
            ")",
            "CREATE TABLE IF NOT EXISTS weather_data (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT," +
                "temperature DOUBLE," +
                "humidity DOUBLE," +
                "pressure DOUBLE," +
                "air_quality DOUBLE," +
                "uv_index DOUBLE," +
                "recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
            ")"
        };
    
        try (Statement stmt = connection.createStatement()) {
            for (String query : createTableQueries) {
                stmt.execute(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error while creating tables", e);
        }
    }

    private void initializeUI() {
        setTitle("Smart Weather Monitoring System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createMainDashboardPanel(), "DASHBOARD");
        mainPanel.add(createProfilePanel(), "PROFILE");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");

        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Weather Monitoring System");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR);
        JButton signupButton = createStyledButton("Sign Up", SECONDARY_COLOR);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 4;
        loginPanel.add(signupButton, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (authenticateUser(username, password)) {
                cardLayout.show(mainPanel, "DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        signupButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Enter Username:");
            String password = JOptionPane.showInputDialog("Enter Password:");
            if (username != null && password != null) {
                if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(this, "User registered successfully!");
                }
            }
        });

        return loginPanel;
    }

    private JPanel createMainDashboardPanel() {
        JPanel mainDashboardPanel = new JPanel(new BorderLayout());
        mainDashboardPanel.setBackground(BACKGROUND_COLOR);

        // Navbar
        JPanel navbarPanel = new JPanel(new BorderLayout());
        navbarPanel.setBackground(PRIMARY_COLOR);
        navbarPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titleLabel = new JLabel("Smart Weather Monitoring System");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(PRIMARY_COLOR);
        JLabel locationLabel = new JLabel("Location: Neemrana");
        JLabel timeLabel = new JLabel();
        JButton profileButton = new JButton("Profile");

        locationLabel.setForeground(Color.WHITE);
        timeLabel.setForeground(Color.WHITE);
        profileButton.setBackground(SECONDARY_COLOR);
        profileButton.setForeground(Color.WHITE);

        rightPanel.add(locationLabel);
        rightPanel.add(timeLabel);
        rightPanel.add(profileButton);

        navbarPanel.add(titleLabel, BorderLayout.WEST);
        navbarPanel.add(rightPanel, BorderLayout.EAST);

        // Time update thread
        new Thread(() -> {
            while (true) {
                timeLabel.setText("Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Main Content
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);

        String[] timeframes = {"Today", "Yesterday", "Past 10 Days"};
        for (String timeframe : timeframes) {
            JPanel timeframePanel = createTimeframePanel(timeframe);
            contentPanel.add(timeframePanel);
        }

        // Profile Button Action
        profileButton.addActionListener(e -> cardLayout.show(mainPanel, "PROFILE"));

        mainDashboardPanel.add(navbarPanel, BorderLayout.NORTH);
        mainDashboardPanel.add(contentPanel, BorderLayout.CENTER);

        return mainDashboardPanel;
    }

    private JPanel createTimeframePanel(String timeframe) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));

        JLabel titleLabel = new JLabel(timeframe);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(REGULAR_FONT);
        titleLabel.setBackground(PRIMARY_COLOR);
        titleLabel.setOpaque(true);
        titleLabel.setForeground(Color.WHITE);

        String[] columns = {"Metric", "Value"};
        Object[][] data = generateRandomWeatherData();
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private Object[][] generateRandomWeatherData() {
        Random random = new Random();
        return new Object[][]{
            {"Temperature", String.format("%.1f °C", 20 + random.nextDouble() * 15)},
            {"Humidity", String.format("%.1f %%", 40 + random.nextDouble() * 40)},
            {"Pressure", String.format("%.1f hPa", 1000 + random.nextDouble() * 50)},
            {"Air Quality", String.format("%.1f AQI", 50 + random.nextDouble() * 150)},
            {"UV Index", String.format("%.1f", 1 + random.nextDouble() * 11)}
        };
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel profileLabel = new JLabel("User Profile");
        profileLabel.setFont(TITLE_FONT);
        profileLabel.setForeground(PRIMARY_COLOR);

        JButton logoutButton = createStyledButton("Logout", Color.RED);
        JButton backButton = createStyledButton("Back to Dashboard", SECONDARY_COLOR);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        profilePanel.add(profileLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        profilePanel.add(new JLabel("Username: " + currentUsername), gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        profilePanel.add(backButton, gbc);

        gbc.gridy = 3;
        profilePanel.add(logoutButton, gbc);

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));
        logoutButton.addActionListener(e -> {
            currentUserId = -1;
            currentUsername = "";
            cardLayout.show(mainPanel, "LOGIN");
        });

        return profilePanel;
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentUserId = rs.getInt("id");
                    currentUsername = username;
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Registration Error: " + e.getMessage());
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(REGULAR_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(REGULAR_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new WeatherMonitoringSystem().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
