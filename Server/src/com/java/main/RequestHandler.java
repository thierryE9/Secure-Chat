package com.java.main;

import com.java.main.utils.SessionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.java.main.utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;

public class RequestHandler {

    final static int SUCCESS = Constants.SUCCESS;
    final static int UNAUTHORIZED = Constants.UNAUTHORIZED;
    final static int USER_OFFLINE = Constants.USER_OFFLINE;
    final static int DATA_CONFLICT = Constants.DATA_CONFLICT;
    final static int SERVICE_UNAVAILABLE = Constants.SERVICE_UNAVAILABLE;

    public static String handleRequest(JSONObject input) {
        String response = "";
        switch (input.getInt("requestID")) {
            case 0:  //register user
                response = register(input);
                break;
            case 1:  //authenticate user
                response = authenticate(input);
                break;
            case 2: //update password
                response = updatePassword(input);
                break;
            case 3:  //get online users
                response = getOnlineUsers(input);
                break;
            case 4:  //forward message
                response = forwardMessage(input);
                System.out.println(response);
                break;
            case 5: //sign out
                response = userSignOut(input);
            default:
                //invalid request
        }
        return response;
    }

    private static String register(JSONObject input) {
        String username = input.getString("username");
        String password = input.getString("password");
        JSONObject response = new JSONObject();
        if (UserManager.registerUser(username, password) == null) {
            response.put("code", DATA_CONFLICT);
            response.put("message", "Username already exists");
        } else {
            response.put("code", SUCCESS);
            response.put("message", "User registered successfully");
        }
        return response.toString();
    }

    private static String authenticate(JSONObject input) {
        String username = input.getString("username");
        String password = input.getString("password");
        String publicKey = input.getString("publicKey");
        String ipAddress = input.getString("ipAddress");
        int port = input.getInt("port");
        JSONObject response = new JSONObject();
        User user = UserManager.authenticateUser(username, password, publicKey, ipAddress, port);
        if (user == null) {
            response.put("code", UNAUTHORIZED);
            response.put("message", "Authentication failed, username or password incorrect");
        } else {
            String token = UserFileManager.onlineUserMap.get(username).getToken();
            response.put("code", SUCCESS);
            response.put("token", token);
            response.put("message", "Successfully logged in as " + username);
        }
        return response.toString();
    }

    private static String updatePassword(JSONObject input) {
        String username = input.getString("username");
        String password = input.getString("password");
        String newPassword = input.getString("newPassword");
        JSONObject response = new JSONObject();
        boolean passwordChanged = false;
        passwordChanged = UserManager.updatePassword(username, password, newPassword);

        if (!passwordChanged) {
            response.put("code", DATA_CONFLICT);
            response.put("message", "Username or password incorrect");
        } else {
            response.put("code", 200);
            response.put("message", "password changed successfully");
        }
        return response.toString();
    }

    private static String getOnlineUsers(JSONObject input) {
        String username = input.getString("username");
        String token = input.getString("token");
        JSONObject response = new JSONObject();
        if (!SessionUtils.validateToken(username, token)) {
            response.put("code", UNAUTHORIZED);
            response.put("message", "Invalid token");
        } else {
            JSONArray onlineUsers = UserFileManager.getOnlineUsers();
            response.put("code", SUCCESS);
            response.put("onlineUsers", onlineUsers);
        }
        return response.toString();
    }

    public static String forwardMessage(JSONObject input) {
        String sourceUsername = input.getString("username");
        String sourceToken = input.getString("token");
        String message = input.getString("message");
        String encryptionType = input.getString("encryptionType");
        String destinationUserName = input.getString("destinationUsername");
        String dateTime = input.getString("dateTime");
        JSONObject response = new JSONObject();
        if (!SessionUtils.validateToken(sourceUsername, sourceToken)) {
            response.put("code", UNAUTHORIZED);
            response.put("message", "Invalid token");
        } else {
            if (!UserFileManager.isUserOnline(destinationUserName)) { //if user offline
                MessageFileManager.saveMessageToForwardWhenOnline(input, destinationUserName);
                response.put("code", USER_OFFLINE);
            } else {
                JSONObject messageSent = new JSONObject();
                messageSent.put("requestID", 0);
                messageSent.put("sourceUsername", sourceUsername);
                messageSent.put("destinationUserName", destinationUserName);
                messageSent.put("message", message);
                messageSent.put("encryptionType", encryptionType);
                messageSent.put("dateTime", dateTime);
                Socket s = null;
                try {
                    String destinationIp = UserFileManager.onlineUserMap.get(destinationUserName).getIpAddress();
                    int destinationPort = UserFileManager.onlineUserMap.get(destinationUserName).getPort();

                    s = new Socket(destinationIp, destinationPort);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeUTF(messageSent.toString());
                    response.put("code", SUCCESS);
                } catch (Exception e) {
                    response.put("code", USER_OFFLINE);
                    UserFileManager.removeOnlineUser(destinationUserName);
                    System.out.println("e: " + e.getMessage());
                } finally {
                    if (s != null) {
                        try {
                            s.close();
                        } catch (IOException e) {
                            System.out.println("close failed");
                        }
                    }
                }
            }
        }
        return response.toString();

    }

    public static String userSignOut(JSONObject input) {
        String username = input.getString("username");
        String token = input.getString("token");
        JSONObject response = new JSONObject();

        if (!SessionUtils.validateToken(username, token)) {
            response.put("code", UNAUTHORIZED);
            response.put("message", "Error: Unauthorized");
        }
        else {
            response.put("code", SUCCESS);
            response.put("message", "Sign out successful");
            UserFileManager.removeOnlineUser(username);

        }
        return response.toString();
    }

}