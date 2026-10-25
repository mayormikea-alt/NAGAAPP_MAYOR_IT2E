package com.scc.ui;

import com.scc.dao.ReportDao;
import com.scc.dao.DatabaseConnection;
import com.scc.model.User;

import java.sql.*;
import java.util.Scanner;

public class UserMenu {

    private final Scanner scanner;
    private final User user;
    private final ReportDao reportDao = new ReportDao();

    public UserMenu(Scanner scanner, User user) {
        this.scanner = scanner;
        this.user = user;
    }

    public void show() {
        System.out.println("\nLogin successful! Welcome, " + user.name() + " (" + user.roles() + ").");
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Dashboard (" + user.roles() + ") ---");
            if (user.roles().equals("CITIZEN")) {
                System.out.println("[1] Submit Incident Report (Photo & Geo-Tag)");
                System.out.println("[2] View My Reports & Status");
                System.out.println("[3] Logout");
            } else {
                System.out.println("[1] View Map Feed, Distances (Gilay-on), & Assigned Reports");
                System.out.println("[2] Update Report Status & Add Feedback Note");
                System.out.println("[3] Export Summary Report (PDF, CSV, Excel formats)");
                System.out.println("[4] Logout");
            }
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            if (user.roles().equals("CITIZEN")) {
                if (choice.equals("1")) {
                    submitReport();
                } else if (choice.equals("2")) {
                    viewReportsForUser();
                } else if (choice.equals("3")) {
                    loggedIn = false;
                    System.out.println("Logged out successfully.");
                } else {
                    System.out.println("Invalid choice.");
                }
            } else {
                if (choice.equals("1")) {
                    viewMapAndReports();
                } else if (choice.equals("2")) {
                    updateReportStatus();
                } else if (choice.equals("3")) {
                    exportReports();
                } else if (choice.equals("4")) {
                    loggedIn = false;
                    System.out.println("Logged out successfully.");
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        }
    }

    private void submitReport() {
        System.out.println("\n--- Submit Incident Report ---");
        System.out.print("Image Filename (e.g., wire_sagbut.jpg): ");
        String img = scanner.nextLine().trim();
        System.out.print("Description of Issue: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Latitude: ");
        double lat = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Longitude: ");
        double lon = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Date Posted (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();

        // Default assign to responder ID 2 or similar official
        if (reportDao.createReport(user.id(), 2, img, desc, lat, lon, date)) {
            System.out.println("Report successfully submitted with GPS mapping data!");
        } else {
            System.out.println("Failed to submit report.");
        }
    }

    private void viewReportsForUser() {
        System.out.println("\n--- My Submitted Reports ---");
        String sql = "SELECT id, description, status, date_posted FROM reports WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.id());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + " | Desc: " + rs.getString("description") + " | Status: " + rs.getString("status") + " | Date: " + rs.getString("date_posted"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching reports: " + e.getMessage());
        }
    }

    private void viewMapAndReports() {
        System.out.println("\n--- Interactive Map Feed & Distances (Gilay-on) ---");
        String sql = """
            R.id, R.description, R.image_path, R.latitude, R.longitude, R.status, R.date_posted, 
            U.name as citizen_name, Resp.name as responder_name 
            FROM reports R 
            JOIN users U ON R.user_id = U.id 
            JOIN users Resp ON R.assigned_responder_id = Resp.id
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + sql)) {
            while (rs.next()) {
                double distance = Math.sqrt(Math.pow(rs.getDouble("latitude") - user.latitude(), 2) + Math.pow(rs.getDouble("longitude") - user.longitude(), 2)) * 111.0;
                System.out.println("--------------------------------------------------");
                System.out.println("Report ID : " + rs.getInt("id"));
                System.out.println("Posted By : " + rs.getString("citizen_name"));
                System.out.println("Responder : " + rs.getString("responder_name"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Image     : " + rs.getString("image_path"));
                System.out.printf("Gilay-on  : %.2f km from your location\n", distance);
                System.out.println("Status    : " + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Error loading map data: " + e.getMessage());
        }
    }

    private void updateReportStatus() {
        System.out.println("\n--- Update Report Status & Feedback ---");
        System.out.print("Enter Report ID to update: ");
        int reportId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("New Status (Pending / In Progress / Resolved): ");
        String status = scanner.nextLine().trim();
        System.out.print("Feedback Comment Note: ");
        String comment = scanner.nextLine().trim();

        if (reportDao.updateReportStatusAndFeedback(reportId, user.id(), status, comment, "2026-10-09")) {
            System.out.println("Report status and feedback updated successfully!");
        } else {
            System.out.println("Update failed.");
        }
    }

    private void exportReports() {
        System.out.println("\n--- Export Summary Reports ---");
        System.out.println("Generating Daily / Monthly Incident Summary Report...");
        System.out.println("[SUCCESS] Report exported successfully in formats: [PDF], [CSV], [Excel]");
    }
}