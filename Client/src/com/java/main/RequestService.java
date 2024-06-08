package com.java.main;

import com.java.main.utils.Constants;
import com.java.main.utils.EncryptionUtils;
import com.java.main.utils.IPUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.java.main.utils.Constants.*;
import static com.java.main.utils.EncryptionUtils.ASYMMETRIC;
import static com.java.main.utils.EncryptionUtils.SYMMETRIC;

public class RequestService {

    public static boolean registerUser(String username, String password) {

        JSONObject registerRequest = new JSONObject();
        registerRequest.put("requestID", REGISTER_REQUEST);
        registerRequest.put("username", username);
        registerRequest.put("password", password);

        String serverResponseStr = ServerCommunicator.sendRequest(registerRequest.toString());
        JSONObject serverResponse = new JSONObject(serverResponseStr);
        int code = serverResponse.getInt("code");

        switch (code) {
            case DATA_CONFLICT: {
                return false;
            }
            case SUCCESS: {
                return true;
            }
        }
        return false;

    }

    public static User authenticateUser(String username, String password) {

        JSONObject authenticateRequest = new JSONObject();
        KeyPair keys = EncryptionUtils.generateKeyPair();
        PublicKey publicKey = keys.getPublic();
        String publicKeyStr = EncryptionUtils.publicKeyToString(publicKey);
        PrivateKey privateKey = keys.getPrivate();
        String ipAddress = IPUtils.getIPAddress();
        int port = ClientListener.getPort();

        authenticateRequest.put("requestID", 1);
        authenticateRequest.put("username", username);
        authenticateRequest.put("password", password);
        authenticateRequest.put("publicKey", publicKeyStr);
        authenticateRequest.put("ipAddress", ipAddress);
        authenticateRequest.put("port", port);
        authenticateRequest.put("dateTime", getFormattedDateAndTime());


        String serverResponseStr = ServerCommunicator.sendRequest(authenticateRequest.toString());
        JSONObject serverResponse = new JSONObject(serverResponseStr);
        int code = serverResponse.getInt("code");

        switch (code) {
            case UNAUTHORIZED:
                return null;
            case SUCCESS:
                String token = serverResponse.getString("token");
                User user = new User(username, token, ipAddress, port);
                EncryptionUtils.privateKey = privateKey;
                EncryptionUtils.publicKey = publicKey;
                return user;
            default:
                return null;
        }

    }

    public static boolean changePassword(String username, String oldPassword, String newPassword) {
        JSONObject changePasswordRequest = new JSONObject();
        changePasswordRequest.put("requestID", 2);
        changePasswordRequest.put("username", username);
        changePasswordRequest.put("password", oldPassword);
        changePasswordRequest.put("newPassword", newPassword);

        String serverResponseStr = ServerCommunicator.sendRequest(changePasswordRequest.toString());
        JSONObject serverResponse = new JSONObject(serverResponseStr);

        int code = serverResponse.getInt("code");

        switch (code) {
            case DATA_CONFLICT:
                return false;
            case SUCCESS:
                return true;
            default:
                return false;
        }

    }

    public static boolean sendMessage(User user, String plainMessage, String destinationUsername) {
        JSONObject sendMessageRequest = new JSONObject();

        String formattedDateTime = getFormattedDateAndTime();

        sendMessageRequest.put("requestID", SENDMESSAGEREQUESTID);
        sendMessageRequest.put("username", user.getUsername());
        sendMessageRequest.put("token", user.getToken());
        sendMessageRequest.put("dateTime", formattedDateTime);
        sendMessageRequest.put("destinationUsername", destinationUsername);


        SecretKey symmetricKey = EncryptionUtils.stringToSymmetricKey(ChatHandler.symmetricKeys.get(destinationUsername));

        if (symmetricKey == null) {
            symmetricKey = RequestService.sendSymmetricKey(user, destinationUsername);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

        String encryptedMessage;

        if (symmetricKey == null)
            return false;

        encryptedMessage = EncryptionUtils.encryptSymmetric(symmetricKey, plainMessage);
        if (encryptedMessage == null)
            return false;

        sendMessageRequest.put("encryptionType", SYMMETRIC);
        sendMessageRequest.put("message", encryptedMessage);

        String serverResponseStr = ServerCommunicator.sendRequest(sendMessageRequest.toString());
        JSONObject serverResponse = new JSONObject(serverResponseStr);
        int code = serverResponse.getInt("code");
        String formattedMessage = "[" + formattedDateTime + "] " + user.getUsername() + ": " + plainMessage;

        switch (code) {
            case UNAUTHORIZED:
                return false;

            case SUCCESS:
                System.out.println(formattedMessage);
                ChatHandler.addMessage(user.getUsername(), destinationUsername, formattedMessage);
                return true;
            case USER_OFFLINE:
                System.out.println("User Offline " + formattedMessage);
                ChatHandler.addMessage(user.getUsername(), destinationUsername, formattedMessage);
                return true;

            default:
                return false;
        }
    }

    public static boolean signOut(User user) {
        JSONObject signOutRequest = new JSONObject();

        signOutRequest.put("requestID", SIGNOUTREQUESTID);
        signOutRequest.put("username", user.getUsername());
        signOutRequest.put("token", user.getToken());
        signOutRequest.put("dateTime", getFormattedDateAndTime());

        String serverResponseStr = ServerCommunicator.sendRequest(signOutRequest.toString());
        JSONObject serverResponse = new JSONObject(serverResponseStr);
        int code = serverResponse.getInt("code");

        switch (code) {
            case UNAUTHORIZED:
                return false;

            case SUCCESS:
                return true;

            default:
                return false;
        }
    }

    public static SecretKey sendSymmetricKey(User user, String destinationUsername) {
        JSONObject sendSymmetricRequest = new JSONObject();
        PublicKey destinationPublicKey = ChatHandler.onlineUserMap.get(destinationUsername);
        SecretKey symmetricKey = EncryptionUtils.generateSymmetricKey();
        String encryptedSymmetricKey = EncryptionUtils.encryptAsymmetric(destinationPublicKey, symmetricKey);

        ChatHandler.symmetricKeys.put(destinationUsername, EncryptionUtils.symmetricKeyToString(symmetricKey));

        sendSymmetricRequest.put("requestID", SENDMESSAGEREQUESTID);
        sendSymmetricRequest.put("username", user.getUsername());
        sendSymmetricRequest.put("token", user.getToken());
        sendSymmetricRequest.put("message", encryptedSymmetricKey);
        sendSymmetricRequest.put("encryptionType", ASYMMETRIC);
        sendSymmetricRequest.put("destinationUsername", destinationUsername);
        sendSymmetricRequest.put("dateTime", getFormattedDateAndTime());


        String serverResponseStr = ServerCommunicator.sendRequest(sendSymmetricRequest.toString());
        JSONObject serverResponse = new JSONObject(serverResponseStr);
        int code = serverResponse.getInt("code");

        switch (code) {
            case SERVICE_UNAVAILABLE:
                return null;

            case UNAUTHORIZED:
                return null;

            case SUCCESS:
                return symmetricKey;

            default:
                return null;

        }
    }

    public static String getFormattedDateAndTime() {
        // Get date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

        return new String(now.format(formatter).toString());
    }

}
