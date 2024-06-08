//package com.java.main;
//
//import com.java.main.utils.EncryptionUtils;
//
//import java.security.NoSuchAlgorithmException;
//import java.security.PublicKey;
//
//public class PublicKeyManager {
//
//    public static void storePublicKey(String token, PublicKey publicKey) {
//        String username;
//        username = UserFileManager.onlineUserMap.get(token);
//        if (username != null) {
//            User user = UserFileManager.userMap.get(username);
//            user.setPublicKey(publicKey);
//            UserFileManager.saveToFile();
//        }
//    }
//
//    public static PublicKey getPublicKey(String token) {
//        String username;
//        PublicKey publicKey;
//        username = UserFileManager.onlineUserMap.get(token);
//        if (username != null) {
//            User user = UserFileManager.userMap.get(username);
//            publicKey = user.getPublicKey();
//            return publicKey;
//        }
//        return null;
//    }
//}