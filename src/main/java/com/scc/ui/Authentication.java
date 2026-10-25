package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {

    private final Scanner scanner;
    private final UserDao userDao;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n==================================================");
            System.out.println("   Naga App: Geo-Tagged Community Issue Mapping");
            System.out.println("==================================================");
            System.out.println("[1] Register Account");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> handleLogin();
                case "3" -> {
                    System.out.println("Thank you for using Naga App. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Register Account ---");
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Latitude (GPS): ");
        double lat = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Longitude (GPS): ");
        double lon = Double.parseDouble(scanner.nextLine().trim());

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("Fields cannot be empty.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("Username already taken. Try another one.");
            return;
        }

        String role = chooseRole();
        if (role == null) {
            System.out.println("Invalid role. Registration cancelled.");
            return;
        }

        if (userDao.register(name, username, password, role, lat, lon)) {
            System.out.println("Account registered successfully! Role: " + role);
        }
    }

    private String chooseRole() {
        System.out.println("Select role:");
        System.out.println("[1] CITIZEN");
        System.out.println("[2] GOVERNMENT OFFICIAL / RESPONDER");
        System.out.println("[3] SYSTEM ADMINISTRATOR");
        System.out.print("Choose: ");
        return switch (scanner.nextLine().trim()) {
            case "1" -> "CITIZEN";
            case "2" -> "GOVERNMENT OFFICIAL / RESPONDER";
            case "3" -> "ADMIN";
            default -> null;
        };
    }

    private void handleLogin() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        User user = userDao.login(username, password).orElse(null);

        if (user != null) {
            System.out.println(" > Welcome " + user.username() + " | ROLE: " + user.roles());

            switch (user.roles()) {
                case "ADMIN" -> new AdminMenu(userDao, scanner).display(user);
                case "CITIZEN", "GOVERNMENT OFFICIAL / RESPONDER" -> new UserMenu(scanner, user).show();
                default -> System.out.println("Account role not recognized!");
            }
        } else {
            System.out.println("Login failed: Invalid username or password.");
        }
    }
}