package com.scc.dao;

import java.sql.*;

public class ReportDao {

    public boolean createReport(int userId, int assignedResponderId, String imagePath, String description, double lat, double lon, String datePosted) {
        String sql = "INSERT INTO reports (user_id, assigned_responder_id, image_path, description, latitude, longitude, status, date_posted) VALUES (?, ?, ?, ?, ?, ?, 'Pending', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, assignedResponderId);
            ps.setString(3, imagePath);
            ps.setString(4, description);
            ps.setDouble(5, lat);
            ps.setDouble(6, lon);
            ps.setString(7, datePosted);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Create report failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateReportStatusAndFeedback(int reportId, int responderId, String status, String comment, String timestamp) {
        String updateReportSql = "UPDATE reports SET status = ? WHERE id = ?";
        String insertUpdateSql = "INSERT INTO report_updates (report_id, responder_id, comment_text, timestamp) VALUES (?, ?, ?, ?)";
        String logSql = "INSERT INTO audit_logs (user_id, action_made, timestamp) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(updateReportSql);
                 PreparedStatement ps2 = conn.prepareStatement(insertUpdateSql);
                 PreparedStatement ps3 = conn.prepareStatement(logSql)) {

                ps1.setString(1, status);
                ps1.setInt(2, reportId);
                ps1.executeUpdate();

                ps2.setInt(1, reportId);
                ps2.setInt(2, responderId);
                ps2.setString(3, comment);
                ps2.setString(4, timestamp);
                ps2.executeUpdate();

                ps3.setInt(1, responderId);
                ps3.setString(2, "Updated report #" + reportId + " status to " + status);
                ps3.setString(3, timestamp);
                ps3.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction failed: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}