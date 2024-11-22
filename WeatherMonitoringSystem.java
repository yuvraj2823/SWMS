import javax.swing.*;
import javax.swing.table.*;
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
    private static final String DB_URL = "jdbc:mysql://localhost:3306/swms";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1Thousandt";

    // UI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Connection connection;
    private Timer fadeTimer;
    private float alpha = 0f;

    // User Session
    private int currentUserId = -1;
    private String currentUsername = "";

    // Styling Constants
    private static final Color PRIMARY_COLOR = new Color(34, 139, 34);  // Forest Green
    private static final Color SECONDARY_COLOR = new Color(60, 179, 113);  // Medium Sea Green
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public WeatherMonitoringSystem() {
        initializeDatabase();
        initializeUI();
        setupFadeInAnimation();
    }

    private void setupFadeInAnimation() {
        fadeTimer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                alpha += 0.1f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadeTimer.stop();
                }
            }
        });
        fadeTimer.start();
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
                "password VARCHAR(255) NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
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
        }
    }

    private void initializeUI() {
        setTitle("Smart Weather Monitoring System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createMainDashboardPanel(), "DASHBOARD");
        mainPanel.add(createProfilePanel(), "PROFILE");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_COLOR, 
                    0, getHeight(), new Color(240, 255, 240));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Weather Monitoring System");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR);
        JButton signupButton = createStyledButton("Sign Up", SECONDARY_COLOR);

        // Add hover effect to buttons
        addButtonHoverEffect(loginButton);
        addButtonHoverEffect(signupButton);

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
                animateTransition("DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        signupButton.addActionListener(e -> showSignupDialog());

        return loginPanel;
    }

    private void showSignupDialog() {
        JDialog dialog = new JDialog(this, "Sign Up", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JButton signupButton = createStyledButton("Register", PRIMARY_COLOR);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(signupButton, gbc);

        signupButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (registerUser(username, password)) {
                JOptionPane.showMessageDialog(dialog, "Registration successful!");
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createMainDashboardPanel() {
        JPanel mainDashboardPanel = new JPanel(new BorderLayout());
        mainDashboardPanel.setBackground(BACKGROUND_COLOR);

        // Navbar
        JPanel navbarPanel = createNavbar();
        
        // Weather Content Panel
        JPanel weatherContentPanel = new JPanel(new BorderLayout());
        weatherContentPanel.setBackground(BACKGROUND_COLOR);
        
        // Weather Time Selection Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton todayButton = createStyledButton("Today's Weather", PRIMARY_COLOR);
        JButton yesterdayButton = createStyledButton("Yesterday's Weather", SECONDARY_COLOR);
        JButton historyButton = createStyledButton("Weather History", SECONDARY_COLOR);
        
        addButtonHoverEffect(todayButton);
        addButtonHoverEffect(yesterdayButton);
        addButtonHoverEffect(historyButton);
        
        buttonPanel.add(todayButton);
        buttonPanel.add(yesterdayButton);
        buttonPanel.add(historyButton);

        // Weather Data Panel
        JPanel weatherDataPanel = new JPanel(new CardLayout());
        weatherDataPanel.setBackground(BACKGROUND_COLOR);
        
        weatherDataPanel.add(createWeatherPanel("Today"), "TODAY");
        weatherDataPanel.add(createWeatherPanel("Yesterday"), "YESTERDAY");
        weatherDataPanel.add(createWeatherPanel("History"), "HISTORY");

        CardLayout weatherCardLayout = (CardLayout) weatherDataPanel.getLayout();

        todayButton.addActionListener(e -> weatherCardLayout.show(weatherDataPanel, "TODAY"));
        yesterdayButton.addActionListener(e -> weatherCardLayout.show(weatherDataPanel, "YESTERDAY"));
        historyButton.addActionListener(e -> weatherCardLayout.show(weatherDataPanel, "HISTORY"));

        weatherContentPanel.add(buttonPanel, BorderLayout.NORTH);
        weatherContentPanel.add(weatherDataPanel, BorderLayout.CENTER);

        mainDashboardPanel.add(navbarPanel, BorderLayout.NORTH);
        mainDashboardPanel.add(weatherContentPanel, BorderLayout.CENTER);

        return mainDashboardPanel;
    }

    private JPanel createNavbar() {
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
        JButton profileButton = createStyledButton("Profile", SECONDARY_COLOR);
        
        locationLabel.setForeground(Color.WHITE);
        timeLabel.setForeground(Color.WHITE);
        
        addButtonHoverEffect(profileButton);

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

        profileButton.addActionListener(e -> animateTransition("PROFILE"));

        return navbarPanel;
    }

    private JPanel createWeatherPanel(String timeframe) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));

        JLabel titleLabel = new JLabel(timeframe + "'s Weather Data");
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
        
        // Custom table styling
        table.setRowHeight(30);
        table.setFont(REGULAR_FONT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Custom header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(REGULAR_FONT.deriveFont(Font.BOLD));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel profileLabel = new JLabel("User Profile");
        profileLabel.setFont(TITLE_FONT);
        profileLabel.setForeground(PRIMARY_COLOR);

        // Profile fields
        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        
        // Load current user data
        // Load current user data
        loadUserData(usernameField);

        // Buttons
        JButton updateButton = createStyledButton("Update Profile", PRIMARY_COLOR);
        JButton deleteButton = createStyledButton("Delete Account", new Color(220, 53, 69));
        JButton backButton = createStyledButton("Back to Dashboard", SECONDARY_COLOR);
        JButton logoutButton = createStyledButton("Logout", new Color(108, 117, 125));

        addButtonHoverEffect(updateButton);
        addButtonHoverEffect(deleteButton);
        addButtonHoverEffect(backButton);
        addButtonHoverEffect(logoutButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        profilePanel.add(profileLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        profilePanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        profilePanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        profilePanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        profilePanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        profilePanel.add(updateButton, gbc);

        gbc.gridy = 5;
        profilePanel.add(deleteButton, gbc);

        gbc.gridy = 6;
        profilePanel.add(backButton, gbc);

        gbc.gridy = 7;
        profilePanel.add(logoutButton, gbc);

        // Button Actions
        updateButton.addActionListener(e -> {
            String newUsername = usernameField.getText();
            String newPassword = new String(passwordField.getPassword());
            updateUserProfile(newUsername, newPassword);
        });

        deleteButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete your account? This action cannot be undone.",
                "Confirm Account Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                deleteUserAccount();
            }
        });

        backButton.addActionListener(e -> animateTransition("DASHBOARD"));
        
        logoutButton.addActionListener(e -> {
            currentUserId = -1;
            currentUsername = "";
            animateTransition("LOGIN");
        });

        return profilePanel;
    }

    private void loadUserData(JTextField usernameField) {
        String query = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, currentUserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usernameField.setText(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUserProfile(String newUsername, String newPassword) {
        StringBuilder query = new StringBuilder("UPDATE users SET ");
        boolean needsComma = false;

        if (!newUsername.isEmpty() && !newUsername.equals(currentUsername)) {
            query.append("username = ?");
            needsComma = true;
        }
        
        if (!newPassword.isEmpty()) {
            if (needsComma) query.append(", ");
            query.append("password = ?");
            needsComma = true;
        }
        
        
        query.append(" WHERE id = ?");

        try (PreparedStatement pstmt = connection.prepareStatement(query.toString())) {
            int paramIndex = 1;
            
            if (!newUsername.isEmpty() && !newUsername.equals(currentUsername)) {
                pstmt.setString(paramIndex++, newUsername);
            }
            
            if (!newPassword.isEmpty()) {
                pstmt.setString(paramIndex++, hashPassword(newPassword));
            }
            
            
            pstmt.setInt(paramIndex, currentUserId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                if (!newUsername.isEmpty() && !newUsername.equals(currentUsername)) {
                    currentUsername = newUsername;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
        }
    }

    private void deleteUserAccount() {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, currentUserId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Account deleted successfully!");
                currentUserId = -1;
                currentUsername = "";
                animateTransition("LOGIN");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting account: " + e.getMessage());
        }
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

    private boolean registerUser(String username, String password ) {
        String query = "INSERT INTO users (username, password) VALUES (?, ? )";
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

    private void animateTransition(String targetCard) {
        Timer timer = new Timer(20, new ActionListener() {
            float currentAlpha = 1.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAlpha -= 0.1f;
                if (currentAlpha <= 0.0f) {
                    ((Timer)e.getSource()).stop();
                    cardLayout.show(mainPanel, targetCard);
                    startFadeIn();
                } else {
                }
            }
        });
        timer.start();
    }

    private void startFadeIn() {
        Timer timer = new Timer(20, new ActionListener() {
            float currentAlpha = 0.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAlpha += 0.1f;
                if (currentAlpha >= 1.0f) {
                    currentAlpha = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    private void addButtonHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }
        });
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
