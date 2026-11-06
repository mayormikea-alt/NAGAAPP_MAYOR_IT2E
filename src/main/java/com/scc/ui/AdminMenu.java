package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(User admin) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n============================");
            System.out.println("       ADMIN DASHBOARD      ");
            System.out.println("============================");
            System.out.println("1. View All User");
            System.out.println("2. View Pending User Approvals");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Add User");
            System.out.println("5. Update User role / Status");
            System.out.println("6. Delete User");
            System.out.println("7. Logout");

            int select = readIntInput("Select: ");

            switch (select) {
                case 1 -> viewAllUser();
                case 2 -> viewPendingUser();
                case 3 -> approveUser();
                case 4 -> System.out.println("Add User feature coming soon.");
                case 5 -> updateUserRoleAndStatus(admin);
                case 6 -> System.out.println("Delete User feature coming soon.");
                case 7 -> {
                    System.out.println("Logged out from Admin Dashboard.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice. Please select between 1 and 7.");
            }
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid numeric input. Please enter a valid number.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (CITIZEN / GOVERNMENT OFFICIAL / RESPONDER / ADMIN / STUDENT / TEACHER): ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (!role.isEmpty()) {
                return role;
            }
            System.out.println("[ERROR] Role cannot be empty. Please enter a valid role.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED / PENDING): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("APPROVED") || status.equals("PENDING") || status.equals("ACTIVE")) {
                return status;
            }
            System.out.println("[ERROR] Invalid status. Please enter APPROVED or PENDING.");
        }
    }

    private void displayUserTable(List<User> users, String title) {
        System.out.println("\n===================================" + title + "===================================");
        if (users.isEmpty()) {
            System.out.println("-> No Records Found");
            return;
        }

        String border = "+-----+-----------------+-------------------------------------+------------+";
        System.out.println(border);
        System.out.printf("| %-3s | %-15s | %-35s | %-10s |\n", "ID", "Username", "Role", "Status");
        System.out.println(border);
        for (User u : users) {
            System.out.printf("| %-3s | %-15s | %-35s | %-10s |\n",
                    u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println(border);
    }

    private void viewAllUser() {
        displayUserTable(userDao.getAllUsers(), "ALL REGISTERED USER");
    }

    private void viewPendingUser() {
        displayUserTable(userDao.getPendingUser(), "PENDING APPROVALS");
    }

    private void approveUser() {
        List<User> pendingUsers = userDao.getPendingUser();
        displayUserTable(pendingUsers, "PENDING APPROVALS");

        if (pendingUsers.isEmpty()) {
            return;
        }
        int userID = readIntInput("Enter User ID to Approve (or 0 to cancel): ");

        if (userID == 0) {
            System.out.println("-> Operation Cancelled by the User");
            return;
        }
        boolean exists = pendingUsers.stream().anyMatch(u -> u.id() == userID);
        if (!exists) {
            System.out.println("[ERROR] User ID not found.");
            return;
        }
        userDao.approveUser(userID);
    }

    private void updateUserRoleAndStatus(User admin) {
        List<User> users = userDao.getAllUsers();
        displayUserTable(users, "ALL REGISTERED USERS");

        if (users.isEmpty()) {
            return;
        }

        int targetUserId = readIntInput("Enter User ID to Update (or 0 to cancel): ");

        if (targetUserId == 0) {
            System.out.println("-> Operation Cancelled by the User");
            return;
        }

        // Self-Edit Restriction Guardrail
        if (targetUserId == admin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        boolean exists = users.stream().anyMatch(u -> u.id() == targetUserId);
        if (!exists) {
            System.out.println("[ERROR] User ID not found.");
            return;
        }

        String newRole = readValidRole();
        String newStatus = readValidStatus();

        if (userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus)) {
            System.out.println("User ID " + targetUserId + " updated successfully!");
        } else {
            System.out.println("[ERROR] Failed to update user role and status.");
        }
    }
}