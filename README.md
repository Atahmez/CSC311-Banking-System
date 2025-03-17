
# Banking System with Fraud Detection

This JavaFX application simulates a basic banking system with fraud detection capabilities. It demonstrates fundamental banking operations like deposits and withdrawals, and includes a simple fraud detection mechanism.

## Features

-   **Deposit:** Allows users to deposit funds into their account.
-   **Withdrawal:** Allows users to withdraw funds from their account.
-   **Transaction Logging:** Records all transactions with timestamps and displays them in a transaction log.
-   **Basic Fraud Detection:** Implements simple fraud detection rules, such as flagging large transactions and random alerts.
-   **JavaFX GUI:** Provides a user-friendly graphical interface for banking operations.
-   **Database Integration (Optional - requires setup):** Stores account information and transaction history in an H2 embedded database.
-   **User Authentication (Optional - requires setup):** Adds basic login functionality.

## Getting Started

1.  **Clone the Repository:**

    ```bash
    git clone [repository-url]
    ```

2.  **Open in IDE:**

    Open the project in your preferred Java IDE (IntelliJ IDEA, Eclipse, VS Code).

3.  **Dependencies:**

    * Ensure you have JavaFX SDK configured in your IDE.
    * (Optional) If you want to use the database integration, add the H2 database dependency to your project's build file (e.g., `pom.xml` for Maven, `build.gradle` for Gradle).

4.  **Run the Application:**

    Run the `BankingApplication.java` file.

## Usage

1.  **Enter Amount:** Enter the amount you wish to deposit or withdraw in the "Amount" field.
2.  **Deposit:** Click the "Deposit" button to deposit the entered amount.
3.  **Withdrawal:** Click the "Withdrawal" button to withdraw the entered amount.
4.  **Transaction Log:** View the transaction history and current balance in the "Transaction Log" area.
5.  **Fraud Alerts:** If the system detects potential fraud, an alert dialog will be displayed.

## Optional Features (Requires Setup)

### Database Integration (H2)

1.  **Add Dependency:** Add the H2 database dependency to your project.
2.  **DatabaseManager Class:** Create a `DatabaseManager.java` class (as provided in the code example) to handle database connections and table creation.
3.  **Modify BankingController:** Update the `BankingController.java` to use `DatabaseManager` for data storage and retrieval.

### User Authentication

1.  **Database Setup:** Add a `users` table to your database.
2.  **Login Screen:** Create a login screen using JavaFX.
3.  **Authentication Logic:** Implement login logic in the `BankingController` or a separate authentication controller.
4.  **Session Management:** Implement session management to track logged-in users.

## Fraud Detection

The application implements basic fraud detection rules:

-   **Large Transactions:** Transactions exceeding a certain threshold (e.g., $5000) will trigger an alert.
-   **Random Alerts:** A small percentage of transactions will trigger random fraud alerts.
-   **Rapid Withdrawals (Optional):** If database integration is implemented, rapid multiple withdrawals within a short period will trigger an alert.

## Further Improvements

-   **Advanced Fraud Detection:** Implement machine learning models for more accurate fraud detection.
-   **User Authentication:** Enhance user authentication and security measures.
-   **Transaction History:** Implement a robust transaction history feature with filtering and sorting.
-   **Security:** Implement security measures to protect user data and prevent attacks.
-   **Unit Tests:** Add unit tests to improve code reliability.
-   **GUI Enhancements:** Improve the user interface and user experience.
-   **Multiple Accounts:** Allow users to manage multiple accounts.

## License

[Add your license here]
