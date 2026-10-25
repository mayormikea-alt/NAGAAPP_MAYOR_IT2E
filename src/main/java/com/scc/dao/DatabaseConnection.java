package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:NagaAppSystemUser.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        return conn;
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Users Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'ACTIVE',
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL
                )
            """);

            // Reports Table (Geo-mapped issues with distance and responder)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    assigned_responder_id INTEGER,
                    image_path TEXT NOT NULL,
                    description TEXT NOT NULL,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    status TEXT NOT NULL DEFAULT 'Pending',
                    date_posted TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(assigned_responder_id) REFERENCES users(id)
                )
            """);

            // Report Updates Table (Responder feedback notes)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS report_updates (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    report_id INTEGER,
                    responder_id INTEGER,
                    comment_text TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    FOREIGN KEY(report_id) REFERENCES reports(id),
                    FOREIGN KEY(responder_id) REFERENCES users(id)
                )
            """);

            // Audit Logs Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS audit_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    action_made TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id)
                )
            """);

        } catch (SQLException e) {
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }
}