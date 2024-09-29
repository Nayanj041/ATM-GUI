package scr;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ATMFunctionality {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/enhanced_atm_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "nayan@041";

    public static void login(String username, String password, InteractiveATMGUI gui) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, balance FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                double balance = rs.getDouble("balance");
                gui.setUserId(userId);
                gui.setBalance(balance);
                gui.showMainPanel();
                JOptionPane.showMessageDialog(gui, "Login successful!");
            } else {
                JOptionPane.showMessageDialog(gui, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void logout(InteractiveATMGUI gui) {
        gui.setUserId(0);
        gui.setBalance(0);
    }

    public static void showRegistrationDialog(InteractiveATMGUI gui) {
        JTextField fullNameField = new JTextField(20);
        JTextField newUsernameField = new JTextField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JTextField initialBalanceField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Username:"));
        panel.add(newUsernameField);
        panel.add(new JLabel("Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Initial Balance:"));
        panel.add(initialBalanceField);

        int result = JOptionPane.showConfirmDialog(gui, panel, "Register New Account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            registerNewAccount(fullNameField.getText(), newUsernameField.getText(),
                    new String(newPasswordField.getPassword()), initialBalanceField.getText(), gui);
        }
    }

    private static void registerNewAccount(String fullName, String username, String password, String initialBalance, InteractiveATMGUI gui) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (full_name, username, password, balance) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setDouble(4, Double.parseDouble(initialBalance));
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(gui, "Account created successfully!");
            } else {
                JOptionPane.showMessageDialog(gui, "Failed to create account.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Error: " + ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void checkBalance(int userId, InteractiveATMGUI gui) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT balance FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                gui.setBalance(balance);
                JOptionPane.showMessageDialog(gui, "Your current balance is: " + ATMUtils.formatCurrency(balance));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void withdraw(int userId, InteractiveATMGUI gui) {
        String amountStr = JOptionPane.showInputDialog(gui, "Enter amount to withdraw:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(gui, "Invalid amount. Please enter a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                conn.setAutoCommit(false);

                // Check current balance
                String balanceQuery = "SELECT balance FROM users WHERE id = ? FOR UPDATE";
                PreparedStatement balancePstmt = conn.prepareStatement(balanceQuery);
                balancePstmt.setInt(1, userId);
                ResultSet rs = balancePstmt.executeQuery();

                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");
                    if (amount > currentBalance) {
                        JOptionPane.showMessageDialog(gui, "Insufficient funds!", "Withdrawal Error", JOptionPane.ERROR_MESSAGE);
                        conn.rollback();
                        return;
                    }

                    // Update balance
                    String updateSql = "UPDATE users SET balance = balance - ? WHERE id = ?";
                    PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
                    updatePstmt.setDouble(1, amount);
                    updatePstmt.setInt(2, userId);
                    updatePstmt.executeUpdate();

                    // Record transaction
                    recordTransaction(conn, userId, "WITHDRAWAL", amount);

                    conn.commit();
                    double newBalance = currentBalance - amount;
                    gui.setBalance(newBalance);
                    JOptionPane.showMessageDialog(gui, "Withdrawn: " + ATMUtils.formatCurrency(amount) + "\nNew balance: " + ATMUtils.formatCurrency(newBalance));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(gui, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(gui, "Invalid amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void deposit(int userId, InteractiveATMGUI gui) {
        String amountStr = JOptionPane.showInputDialog(gui, "Enter amount to deposit:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(gui, "Invalid amount. Please enter a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                conn.setAutoCommit(false);

                // Update balance
                String updateSql = "UPDATE users SET balance = balance + ? WHERE id = ?";
                PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
                updatePstmt.setDouble(1, amount);
                updatePstmt.setInt(2, userId);
                updatePstmt.executeUpdate();

                // Record transaction
                recordTransaction(conn, userId, "DEPOSIT", amount);

                conn.commit();

                // Fetch updated balance
                String balanceQuery = "SELECT balance FROM users WHERE id = ?";
                PreparedStatement balancePstmt = conn.prepareStatement(balanceQuery);
                balancePstmt.setInt(1, userId);
                ResultSet rs = balancePstmt.executeQuery();

                if (rs.next()) {
                    double newBalance = rs.getDouble("balance");
                    gui.setBalance(newBalance);
                    JOptionPane.showMessageDialog(gui, "Deposited: " + ATMUtils.formatCurrency(amount) + "\nNew balance: " + ATMUtils.formatCurrency(newBalance));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(gui, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(gui, "Invalid amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void recordTransaction(Connection conn, int userId, String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        pstmt.setString(2, type);
        pstmt.setDouble(3, amount);
        pstmt.executeUpdate();
    }

    public static void showTransactionHistory(int userId, InteractiveATMGUI gui) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT transaction_type, amount, transaction_date FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC LIMIT 5";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder history = new StringBuilder("Last 5 Transactions:\n\n");
            while (rs.next()) {
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                Timestamp date = rs.getTimestamp("transaction_date");
                history.append(String.format("%s - %s: %s\n", date, type, ATMUtils.formatCurrency(amount)));
            }

            JTextArea textArea = new JTextArea(history.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(300, 200));

            JOptionPane.showMessageDialog(gui, scrollPane, "Transaction History", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(gui, "Error fetching transaction history: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

