package com.java.main;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UserFileManager.loadFromFile();
        ServerListener server = new ServerListener();
        server.listen();
        MessageFileManager.start();

        while (true) {
            scanner.next();
            System.out.println(UserFileManager.userMap +"\n\n\n-------------\n\n\n");
            System.out.println(UserFileManager.onlineUserMap);
        }

    }
}