package com.java.main;

import com.java.main.utils.StringUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class UserInterface {
    static Scanner scanner = new Scanner(System.in);
    public static String selectedUser = "";

    public static void displayMenu() {

        boolean running = true;
        String input;
        while (running) {
            System.out.println("Choose an option:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Update password");
            System.out.println("4. Quit");
            input = scanner.next();
            switch (input) {
                case "1":
                    registerInterface();
                    break;

                case "2":
                    authenticateInterface();
                    break;

                case "3":
                    updatePasswordInterface();
                    break;
                case "4":
                    System.exit(0);
                    break;
            }
        }
    }

    private static void registerInterface() {
        boolean registered = false;
        System.out.print("Enter username: ");
        String regUsername = scanner.next();
        if (!StringUtils.isValidUsername(regUsername)) {
            System.out.println("Invalid username. Only letters, numbers, and underscores are allowed.");
            return;
        }
        System.out.print("Enter password: ");
        String regPassword = scanner.next();

        registered = RequestService.registerUser(regUsername, regPassword);
        if (!registered) {
            System.out.println("Username already exists");
            return;
        } else {
            System.out.println("User " + regUsername + " registered successfully");
            return;
        }

    }

    private static void authenticateInterface() {   //TODO does not say user already signed in
        System.out.print("Enter username: ");
        String username = scanner.next();
        if (!StringUtils.isValidUsername(username)) {
            System.out.println("Invalid username. Only letters, numbers, and underscores are allowed.");
            return;
        }
        System.out.print("Enter password: ");
        String password = scanner.next();

        User user = RequestService.authenticateUser(username, password);

        if (user == null) {
            System.out.println("Sign in failed: wrong username or password");
        } else {
            System.out.println("Successfully signed in as " + user.getUsername());
            Saving.loadConversationsFromFile(user.getUsername());
            Saving.saveScheduler(user.getUsername(), 30);
            selectChatInterface(user);
        }
        Saving.isSchedulerRunning = false;
    }

    public static void updatePasswordInterface(){
        System.out.print("Enter username: ");
        String username = scanner.next();
        if (!StringUtils.isValidUsername(username)) {
            System.out.println("Invalid username. Only letters, numbers, and underscores are allowed.");
            return;
        }
        System.out.print("Enter old password: ");
        String password = scanner.next();
        System.out.println("enter new password");
        String newPasswrod = scanner.next();
        boolean passWordChanged = RequestService.changePassword(username, password, newPasswrod);

        if(!passWordChanged){
            System.out.println("Error changing password: invalid username or old password");
            return;
        }else {
            System.out.println("Successfully changed password");
        }

    }

    private static void selectChatInterface(User user) {
        boolean authenticated = true;
        do {
            int onlineUsers = viewOnlineUsers(user);
            int refreshNumber = onlineUsers + 1;
            int singOutNumber = onlineUsers + 2;
            if (onlineUsers == 0) { //minimum online users is 1
                RequestService.signOut(user);
                System.out.println("Something went wrong. Signing out.");
                return;
            }

            String input;
            int inputInt;
            System.out.println(onlineUsers + 1 + ". Refresh");
            System.out.println(onlineUsers + 2 + ". Sign out");
            input = scanner.next();

            try {
                inputInt = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Input format error");
                continue;
            }
            if (inputInt < 1 || inputInt > singOutNumber) {
                System.out.println("Invalid input");
                continue;
            } else if (inputInt == singOutNumber) {
                boolean singedOut = RequestService.signOut(user);
                if(!singedOut)
                    System.out.println("Error signing out");
                else
                    Saving.saveConversationsToFile(user.getUsername());
                    System.out.println("Signed out successfully");
                return;
            } else if (inputInt == refreshNumber) {

            } else {
                chatInterface(user, inputInt - 1);
            }

        } while (authenticated);
    }

    public static int viewOnlineUsers(User user) {
        if (!ChatHandler.getOnlineUsers(user.getUsername(), user.getToken())) {
            System.out.println("An error has occurred getting online users.");
            return 0;
        } else {
            int count = 0;
            String userList = "";
            count = 0;
            for (HashMap.Entry<String, PublicKey> entry : ChatHandler.onlineUserMap.entrySet()) {
                count++;
                userList = userList + "\n" + count + ". " + entry.getKey();
            }
            System.out.print("Select a user to chat with:");
            System.out.println(userList);
            return count;
        }
    }

    public static void chatInterface(User user, int userNumber) {

        String friendUsername = ChatHandler.onlineUserList.get(userNumber);
        String input;
        System.out.println("You are chatting with " + friendUsername + ". exit1 to go back");
        selectedUser = friendUsername;
        displayMessages(ChatHandler.conversations.get(friendUsername));
        while(true){
            input = scanner.nextLine();

            if(input.equals("")) continue;

            if (input.equals("exit1")){
                selectedUser = "";
                return;
            }
            boolean messageSent = RequestService.sendMessage(user, input, friendUsername);
            if(!messageSent)
                System.out.println("ERROR SENDING MESSAGE");
        }
    }

    public static void displayMessages(ArrayList<String> messages){
        if(messages == null) messages = new ArrayList<String>();

        for(int i = 0; i < messages.size(); i++) {
            System.out.println(messages.get(i));
        }
    }

}