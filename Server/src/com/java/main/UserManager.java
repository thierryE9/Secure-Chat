package com.java.main;

import com.java.main.utils.EncryptionUtils;
import com.java.main.utils.HashingUtils;
import com.java.main.utils.SessionUtils;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;


public class UserManager {
    public static User registerUser(String username, String password) {
        username = username.toLowerCase(); // Convert username to lowercase

        if (UserFileManager.loadUser(username) != null) {
            return null;
        }

        String salt = HashingUtils.generateSalt();
        String hashedPassword = HashingUtils.generateHash(password, salt);
        User newUser = new User(username, hashedPassword, salt);
        UserFileManager.saveUser(newUser);
        return newUser;
    }

    public static User authenticateUser(String username, String password, String publicKey, String ipAddress, int port) {
       //TODO already signed in w map
        User user = UserFileManager.loadUser(username);
        String token;
        if (publicKey == null)  //will not proceed if no public key
            return null;
        if (user == null) {
            return null;
        }
        if(UserFileManager.onlineUserMap.containsKey(username)){
            return null;
        }
        String hashedPassword = HashingUtils.generateHash(password, user.getSalt());
        if (hashedPassword.equals(user.getHashedPassword())) {
            do{
                token = SessionUtils.generateToken(username);
            }while(UserFileManager.onlineUserMap.containsValue(token)); //prevent duplicate tokens
            OnlineUserInfo onlineUserInfo = new OnlineUserInfo(token, publicKey, ipAddress, port);
            UserFileManager.addOnlineUser(username, onlineUserInfo);
            return user;
        }
        return null;
    }

    public static boolean updatePassword(String username, String oldPassword, String newPassword) {
        username = username.toLowerCase();

        User user = UserFileManager.loadUser(username);
        if (user == null) {
            return false; // User not found
        }

        String salt = user.getSalt();
        String storedHashedPassword = user.getHashedPassword();
        boolean isOldPasswordCorrect = HashingUtils.verifyPassword(oldPassword, salt, storedHashedPassword);

        if (!isOldPasswordCorrect) {
            return false; // Old password is incorrect
        }

        String newSalt = HashingUtils.generateSalt();
        String newHashedPassword = HashingUtils.generateHash(newPassword, newSalt);
        user.setSalt(newSalt);
        user.setHashedPassword(newHashedPassword);
        UserFileManager.updateUser(user);
        return true;
    }

    public static User getUser(String username) {
        username = username.toLowerCase(); // Convert username to lowercase

        return UserFileManager.loadUser(username);
    }
}

