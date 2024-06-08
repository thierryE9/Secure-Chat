package com.java.main.utils;

import com.java.main.UserFileManager;

import java.security.Key;
public class SessionUtils {
    public static String generateToken(String username) {
        String salt = HashingUtils.generateSalt();
        String token = HashingUtils.generateHash(username, salt);
        return token;
    }

    public static boolean validateToken(String username, String token) {
        if(!UserFileManager.isUserOnline(username))
            return false;
        if(UserFileManager.onlineUserMap.get(username).getToken().equals(token))
            return true;
        return false;
    }
}
