// DatabaseManager.java
package com.example.bankingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.BiConsumer;
import com.example.bankingsystem.model.SavedReportData;
import com.example.bankingsystem.model.CheckData;
import com.example.bankingsystem.model.SavedReportData;
import com.example.bankingsystem.model.User;
/** Handles connection & schema setup for the SQLite database. */
public final class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:banking.db";

    private DatabaseManager() {}

    /** Opens a connection with foreign‑key support enabled. */
    private static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Initializes tables and reports the progress by calling the provided function. 
     */
    public static void initializeDatabase(BiConsumer<Integer, Integer> progress) throws SQLException {
        final int total = 4;
        int step = 0;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            progress.accept(++step, total);   // 1/4 opened connection

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    created_at TEXT NOT NULL
                );
            """);
            progress.accept(++step, total);   // 2/4 users table

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS checks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    check_number TEXT NOT NULL,
                    payee TEXT NOT NULL,
                    amount REAL NOT NULL,
                    date TEXT NOT NULL,
                    memo TEXT,
                    status TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
            """);
            progress.accept(++step, total);   // 3/4 checks table

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS saved_reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    report_id TEXT NOT NULL UNIQUE,
                    report_type TEXT NOT NULL,
                    start_date TEXT NOT NULL,
                    end_date TEXT NOT NULL,
                    status_filter TEXT,
                    generation_timestamp TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
            """);
            progress.accept(++step, total);   // 4/4 saved_reports table
        }
    }

    public static java.util.List<SavedReportData> getSavedReportsForUser(String username) throws SQLException {
        java.util.List<com.example.bankingsystem.model.SavedReportData> reports = new java.util.ArrayList<>();
        String sql = """
            SELECT sr.report_id, sr.report_type, sr.start_date, sr.end_date, sr.status_filter, sr.generation_timestamp, u.username
            FROM saved_reports sr
            JOIN users u ON sr.user_id = u.id
            WHERE u.username = ?;
        """;

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            java.sql.ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String reportId = rs.getString("report_id");
                String reportType = rs.getString("report_type");
                java.time.LocalDate startDate = java.time.LocalDate.parse(rs.getString("start_date"));
                java.time.LocalDate endDate = java.time.LocalDate.parse(rs.getString("end_date"));
                String statusFilter = rs.getString("status_filter");
                java.time.LocalDateTime generationTimestamp = java.time.LocalDateTime.parse(rs.getString("generation_timestamp"));
                String generatedByUsername = rs.getString("username"); // This is u.username

                reports.add(new com.example.bankingsystem.model.SavedReportData(
                        reportId, reportType, startDate, endDate, statusFilter, generationTimestamp, generatedByUsername
                ));
            }
        }
        return reports;
    }

    public static boolean deleteSavedReport(String reportId, String username) throws SQLException {
        String sql = """
            DELETE FROM saved_reports
            WHERE report_id = ? AND user_id = (SELECT id FROM users WHERE username = ?);
        """;

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reportId);
            pstmt.setString(2, username);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static java.util.List<com.example.bankingsystem.model.CheckData> getChecksForReportCriteria(
            String username, java.time.LocalDate startDate, java.time.LocalDate endDate, String statusFilter) throws SQLException {
        java.util.List<com.example.bankingsystem.model.CheckData> checks = new java.util.ArrayList<>();
        
        // Base query
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT c.check_number, c.payee, c.amount, c.date, c.memo, c.status, u.username as check_username, c.timestamp " +
            "FROM checks c " +
            "JOIN users u ON c.user_id = u.id " +
            "WHERE u.username = ? AND c.date BETWEEN ? AND ?"
        );

        // Append status filter if provided and not 'All Statuses' (or similar generic term)
        boolean useStatusFilter = statusFilter != null && !statusFilter.isEmpty() && !"All Statuses".equalsIgnoreCase(statusFilter);
        if (useStatusFilter) {
            sqlBuilder.append(" AND c.status = ?");
        }
        sqlBuilder.append(" ORDER BY c.date DESC, c.timestamp DESC;");

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            int paramIndex = 1;
            pstmt.setString(paramIndex++, username);
            pstmt.setString(paramIndex++, startDate.toString());
            pstmt.setString(paramIndex++, endDate.toString());

            if (useStatusFilter) {
                pstmt.setString(paramIndex++, statusFilter);
            }

            java.sql.ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                checks.add(new com.example.bankingsystem.model.CheckData(
                    rs.getString("check_number"),
                    rs.getString("payee"),
                    rs.getDouble("amount"),
                    java.time.LocalDate.parse(rs.getString("date")),
                    rs.getString("memo"),
                    rs.getString("status"),
                    rs.getString("check_username"), // username from the users table alias
                    java.time.LocalDateTime.parse(rs.getString("timestamp"))
                ));
            }
        }
        return checks;
    }

    public static java.util.List<CheckData> getRecentChecksForUser(String username, int limit) throws SQLException {
        java.util.List<com.example.bankingsystem.model.CheckData> recentChecks = new java.util.ArrayList<>();
        String sql = """
            SELECT c.check_number, c.payee, c.amount, c.date, c.memo, c.status, u.username as check_username, c.timestamp
            FROM checks c
            JOIN users u ON c.user_id = u.id
            WHERE u.username = ?
            ORDER BY c.timestamp DESC, c.id DESC -- Order by timestamp then by id for stable sort if timestamps are identical
            LIMIT ?;
        """;

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, limit);
            java.sql.ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                recentChecks.add(new com.example.bankingsystem.model.CheckData(
                    rs.getString("check_number"),
                    rs.getString("payee"),
                    rs.getDouble("amount"),
                    java.time.LocalDate.parse(rs.getString("date")),
                    rs.getString("memo"),
                    rs.getString("status"),
                    rs.getString("check_username"),
                    java.time.LocalDateTime.parse(rs.getString("timestamp"))
                ));
            }
        }
        return recentChecks;
    }

    public static void saveCheck(CheckData checkData) throws SQLException {
        String sql = """
            INSERT INTO checks (user_id, check_number, payee, amount, date, memo, status, timestamp)
            SELECT id, ?, ?, ?, ?, ?, ?, ?
            FROM users
            WHERE username = ?;
        """;

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, checkData.checkNumber());
            pstmt.setString(2, checkData.payee());
            pstmt.setDouble(3, checkData.amount());
            pstmt.setString(4, checkData.date().toString());
            pstmt.setString(5, checkData.memo());
            pstmt.setString(6, checkData.status());
            pstmt.setString(7, checkData.timestamp().toString());
            pstmt.setString(8, checkData.username()); // For the subquery to find user_id

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating check failed, no rows affected (user not found or other issue).");
            }
        }
    }

    public static void saveReportMetadata(String reportType, java.time.LocalDate startDate, java.time.LocalDate endDate, 
                                          String statusFilter, String username) throws SQLException {
        String reportId = java.util.UUID.randomUUID().toString();
        String generationTimestamp = java.time.LocalDateTime.now().toString();

        String sql = """
            INSERT INTO saved_reports (user_id, report_id, report_type, start_date, end_date, status_filter, generation_timestamp)
            SELECT id, ?, ?, ?, ?, ?, ? 
            FROM users 
            WHERE username = ?;
        """;

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reportId);
            pstmt.setString(2, reportType);
            pstmt.setString(3, startDate.toString());
            pstmt.setString(4, endDate.toString());
            pstmt.setString(5, statusFilter);
            pstmt.setString(6, generationTimestamp);
            pstmt.setString(7, username); // For the subquery to find user_id

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating saved report failed, no rows affected (user not found or other issue).");
            }
        }
    }

    public static boolean deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static User authenticateUser(String username, String plainTextPassword) throws SQLException {
        String sql = "SELECT password_hash, created_at FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPasswordHash = rs.getString("password_hash");
                    String createdAtStr = rs.getString("created_at");
                    java.time.LocalDateTime createdAt = java.time.LocalDateTime.parse(createdAtStr);

                    // --- Placeholder for password hashing --- 
                    // In a real application, use a strong hashing algorithm (e.g., BCrypt)
                    // to hash plainTextPassword and compare with storedPasswordHash.
                    String simulatedInputHash = plainTextPassword + "_hashed"; // Simulate hashing

                    if (simulatedInputHash.equals(storedPasswordHash)) {
                        return new User(username, storedPasswordHash, createdAt);
                    }
                }
            }
        }
        return null; // User not found or password incorrect
    }

    public static User registerUser(String username, String plainTextPassword) throws SQLException {
        // --- Placeholder for password hashing ---
        // In a real application, use a strong hashing algorithm (e.g., BCrypt)
        String hashedPassword = plainTextPassword + "_hashed"; // Simulate hashing
        String createdAt = java.time.LocalDateTime.now().toString();

        String sql = "INSERT INTO users (username, password_hash, created_at) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, createdAt);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return new User(username, hashedPassword, java.time.LocalDateTime.parse(createdAt));
            }
        } catch (SQLException e) {
            // Check if the error is due to a unique constraint violation (username already exists)
            if (e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed: users.username")) {
                // This specific error code 19 and message is for SQLite. 
                // For other databases, the error code and message might differ.
                return null; // Indicate username already exists
            }
            throw e; // Re-throw other SQLExceptions
        }
        return null; // Should not be reached if insert succeeds or known error is handled
    }
}
