package scr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;

public class InteractiveATMGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/enhanced_atm_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "nayan@041";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton logoutButton;
    private JLabel balanceLabel;
    private JButton checkBalanceButton;
    private JButton withdrawButton;
    private JButton depositButton;
    private JButton transactionHistoryButton;
    private JButton exitButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private int userId;
    private double balance;
    private Timer balanceUpdateTimer;

    private Image backgroundImage;

    public InteractiveATMGUI() {
        setTitle("ATM Application");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (loadBackgroundImage()) {
            setContentPane(new BackgroundPanel());
        } else {
            System.err.println("Failed to load background image. Using default background.");
        }

        initComponents();
        setupLayout();

        balanceUpdateTimer = new Timer(5000, e -> updateBalanceDisplay());
        balanceUpdateTimer.start();
    }

    private boolean loadBackgroundImage() {
        try {
            // Try loading from file
            File file = new File("D:\\atm\\img\\atm.png");
            if (file.exists()) {
                backgroundImage = ImageIO.read(file);
                System.out.println("Background image loaded from file: " + file.getAbsolutePath());
            } else {
                // If file doesn't exist, try loading from resources
                URL resourceUrl = getClass().getResource("/atm.png");
                if (resourceUrl != null) {
                    backgroundImage = ImageIO.read(resourceUrl);
                    System.out.println("Background image loaded from resources: " + resourceUrl);
                } else {
                    System.err.println("Background image file not found in both file system and resources.");
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading background image: " + e.getMessage());
            return false;
        }
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                System.err.println("Background image is null in paintComponent");
            }
        }
    }

    private void initComponents() {
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        logoutButton = new JButton("Logout");
        balanceLabel = new JLabel("Balance: $0.00");
        checkBalanceButton = new JButton("Check Balance");
        withdrawButton = new JButton("Withdraw");
        depositButton = new JButton("Deposit");
        transactionHistoryButton = new JButton("Transaction History");
        exitButton = new JButton("Exit");

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> showRegistrationDialog());
        logoutButton.addActionListener(e -> logout());
        checkBalanceButton.addActionListener(e -> checkBalance());
        withdrawButton.addActionListener(e -> withdraw());
        depositButton.addActionListener(e -> deposit());
        transactionHistoryButton.addActionListener(e -> showTransactionHistory());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.err.println("Background image is null in cardPanel paintComponent");
                }
            }
        };
        cardPanel.setOpaque(false);

        JPanel loginPanel = createLoginPanel();
        JPanel mainPanel = createMainPanel();

        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(mainPanel, "MAIN");

        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                if (isOpaque()) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                if (isOpaque()) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(logoutButton);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(balanceLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(checkBalanceButton, gbc);

        gbc.gridx = 1;
        centerPanel.add(withdrawButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(depositButton, gbc);

        gbc.gridx = 1;
        centerPanel.add(transactionHistoryButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        centerPanel.add(exitButton, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        ATMFunctionality.login(username, password, this);
    }

    private void logout() {
        ATMFunctionality.logout(this);
        cardLayout.show(cardPanel, "LOGIN");
        usernameField.setText("");
        passwordField.setText("");
    }

    private void showRegistrationDialog() {
        ATMFunctionality.showRegistrationDialog(this);
    }

    private void checkBalance() {
        ATMFunctionality.checkBalance(userId, this);
    }

    private void withdraw() {
        ATMFunctionality.withdraw(userId, this);
    }

    private void deposit() {
        ATMFunctionality.deposit(userId, this);
    }

    private void showTransactionHistory() {
        ATMFunctionality.showTransactionHistory(userId, this);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        updateBalanceDisplay();
    }

    public void updateBalanceDisplay() {
        balanceLabel.setText("Balance: " + ATMUtils.formatCurrency(balance));
    }

    public void showMainPanel() {
        cardLayout.show(cardPanel, "MAIN");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            InteractiveATMGUI atm = new InteractiveATMGUI();
            atm.setVisible(true);
        });
    }
}