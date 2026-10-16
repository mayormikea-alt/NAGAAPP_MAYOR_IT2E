package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Optional;
import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

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
                case "2" -> login();
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
            case "3" -> "ADMINISTRATOR";
            default -> null;
        };
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Optional<User> result = userDao.login(username, password);
        if (result.isPresent()) {
            new UserMenu(scanner, result.get()).show();
        } else {
            System.out.println("Invalid username or password.");
        }
    }
}