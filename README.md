# ATM Application

An advanced Java-based ATM (Automated Teller Machine) simulation featuring a graphical user interface. This application provides a secure, user-friendly environment for performing common banking operations, demonstrating the integration of GUI design, database management, and financial transaction handling in a single cohesive system.

## Key Features

1. **User Authentication**
   - Secure login/logout functionality
   - Password-protected access to individual accounts

2. **New User Registration**
   - User-friendly registration process for new accounts
   - Capture of essential user details including full name, username, password, and initial balance

3. **Real-time Balance Management**
   - Instant balance checking
   - Automatic balance updates after each transaction

4. **Financial Transactions**
   - Secure withdrawal operation with insufficient funds protection
   - Deposit functionality with immediate balance update
   - Transaction recording for audit purposes

5. **Transaction History**
   - View last 5 transactions
   - Detailed transaction log including type, amount, and date/time

6. **Database Integration**
   - MySQL database for persistent data storage
   - Real-time data synchronization between application and database

7. **Graphical User Interface**
   - Intuitive, user-friendly interface built with Java Swing
   - Custom background image for enhanced visual appeal
   - Responsive design with proper layout management

## Technical Architecture

### 1. InteractiveATMGUI (Main GUI Class)
- Manages the overall user interface
- Implements the main application window using JFrame
- Utilizes CardLayout for seamless switching between login and main screens
- Integrates custom background image handling

### 2. ATMFunctionality (Business Logic Class)
- Handles core ATM operations (login, registration, withdrawals, deposits, etc.)
- Manages database interactions using JDBC
- Implements transaction management and database updates

### 3. ATMUtils (Utility Class)
- Provides helper methods, such as currency formatting

### 4. Database Schema
- Users table: Stores user account information
- Transactions table: Logs all financial transactions

## Implementation Details

1. **Java Swing for GUI**
   - Custom panels with background image support
   - Utilizes GridBagLayout and FlowLayout for component arrangement

2. **JDBC for Database Connectivity**
   - Prepared statements to prevent SQL injection
   - Transaction management for data integrity

3. **Security Measures**
   - Password field for secure input
   - Basic input validation to prevent invalid data entry

4. **Modular Design**
   - Separation of concerns between UI, business logic, and utilities
   - Enhances maintainability and allows for future expansions

## Getting Started

1. **Prerequisites**
   - Java Development Kit (JDK) 8 or higher
   - MySQL Server 5.7 or higher
   - MySQL Connector/J (JDBC driver)

2. **Database Setup**
   - Create a new MySQL database named `enhanced_atm_db`
   - Execute the provided SQL script to create necessary tables

3. **Configuration**
   - Update the `DB_URL`, `DB_USER`, and `DB_PASSWORD` constants in `InteractiveATMGUI.java` and `ATMFunctionality.java` with your MySQL connection details

4. **Compilation and Execution**
   ```
   javac -cp .:mysql-connector-java.jar scr/*.java
   java -cp .:mysql-connector-java.jar scr.InteractiveATMGUI
   ```

5. **Background Image**
   - Place an image named `atm.png` in the `D:\atm\img\` directory or in the resources folder of your project

## Future Enhancements

- Implement more robust security measures (e.g., encryption, multi-factor authentication)
- Add additional banking features (e.g., fund transfers, bill payments)
- Improve error handling and user feedback
- Implement logging for system events and user actions
- Create unit tests for core functionalities

#Screensort

![img1](https://github.com/user-attachments/assets/e3c4cbd8-6533-4eba-933c-d410a7d24fa7)
![img2](https://github.com/user-attachments/assets/32c2b1b5-39c1-4ddb-a4ca-cca27a0f6e96)
![img3](https://github.com/user-attachments/assets/1df5712e-cfb4-4068-bc03-c9f64bd054e8)
![img4](https://github.com/user-attachments/assets/e3478fde-c9b9-475e-825a-45a8154cd3ba)

