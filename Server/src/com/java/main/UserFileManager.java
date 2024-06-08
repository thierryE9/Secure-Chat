package com.java.main;

import com.java.main.utils.EncryptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserFileManager {
    public static Map<String, User> userMap = new HashMap<>();
    public static Map<String, OnlineUserInfo> onlineUserMap = new HashMap<>();
    private static final String filePath = "user_data.ser";

    public UserFileManager() {
    }

    public static void addOnlineUser(String username, OnlineUserInfo user) {
        onlineUserMap.put(username, user);
    }

    public static void removeOnlineUser(String username) {
        if (onlineUserMap.containsKey(username)) {
            onlineUserMap.remove(username);
        }
    }

    public static boolean isUserOnline(String username) {
        if(!onlineUserMap.containsKey(username))
            return false;
        return true;
    }

    public static JSONArray getOnlineUsers() {
        JSONArray onlineUsers = new JSONArray();
        for (HashMap.Entry<String, OnlineUserInfo> entry : onlineUserMap.entrySet()) {
            OnlineUserInfo onlineUserObj = onlineUserMap.get(entry.getKey());
            JSONObject onlineUser = new JSONObject();
            onlineUser.put("username", entry.getKey());
            onlineUser.put("publicKey", onlineUserObj.getPublicKey());
            onlineUsers.put(onlineUser);
        }
        return onlineUsers;
    }

    public static void saveUser(User user) {
        userMap.put(user.getUsername(), user);
        saveToFile();
    }

    public static User loadUser(String username) {
        return userMap.get(username);
    }

    public static void updateUser(User user) {
        userMap.replace(user.getUsername(), user);
        saveToFile();
    }

    public static void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            userMap = (HashMap<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load user data from file. Starting with an empty user storage.");
            e.printStackTrace();
        }
    }

    public static void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(userMap);
        } catch (IOException e) {
            System.err.println("Failed to save user data to file.");
            e.printStackTrace();
        }
    }
}
