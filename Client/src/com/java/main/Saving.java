package com.java.main;


import com.java.main.utils.EncryptionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Saving {

    public static boolean isSchedulerRunning = false;

    // Loads the conversations of the specific user into the conversation map
    public static void loadConversationsFromFile(String username) {
        String folderName = "conversations";
        String fileName = folderName + File.separator + username + ".txt";
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentConversation = null;
            ArrayList<String> messages = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Conversation: ")) {
                    if (currentConversation != null) {
                        ChatHandler.conversations.put(currentConversation, messages);
                    }
                    currentConversation = line.substring("Conversation: ".length());
                    messages = new ArrayList<>();
                } else if(line.startsWith("SymmetricKey: ") && currentConversation != null){
                    String privateKeyStr = line.substring("SymmetricKey: ".length());
                    ChatHandler.symmetricKeys.put(currentConversation, privateKeyStr);
                } else if (!line.isEmpty()) {
                    messages.add(line);
                }
            }

            if (currentConversation != null) {
                ChatHandler.conversations.put(currentConversation, messages);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Starts a scheduler to save the conversation in a file every minute
    public static void saveScheduler(String username, int timeSeconds) {
        // Schedule the saveConversationsToFile method to be executed every specific time
        isSchedulerRunning = true;

        new Thread(() -> {
            while(isSchedulerRunning){
                saveConversationsToFile(username);
                try {
                    Thread.sleep(timeSeconds * 1000);
                } catch (InterruptedException e) {
                    System.out.println("save thread interrupted");
                }
            }
        }).start();
    }

    // save conversation hashmap in a text file in conversations/"userName".txt
    public static synchronized void saveConversationsToFile(String username) {
        String folderName = "conversations";
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }

        String fileName = folderName + File.separator + username + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, ArrayList<String>> entry : ChatHandler.conversations.entrySet()) {

                writer.write("Conversation: " + entry.getKey() + "\n");
                writer.write("SymmetricKey: " + ChatHandler.symmetricKeys.get(entry.getKey()) + "\n");
                for (String message : entry.getValue()) {
                    writer.write(message + "\n");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
