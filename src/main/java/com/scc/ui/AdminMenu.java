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
            System.out.println("============================");
            System.out.println("====== ADMIN DASHBOARD =====");
            System.out.println("============================");
            System.out.println("1. View All User");
            System.out.println("2. Add User");
            System.out.println("3. View Pending User Approvals");
            System.out.println("4. Approve User Registration");
            System.out.println("5. Update User role / Status");
            System.out.println("6. Delete User");
            System.out.println("7. Logout");
            System.out.print("Select: ");

            int select = scanner.nextInt();
            scanner.nextLine(); // Consume newline


        }
    }
}