package com.java.main;

import com.java.main.utils.EncryptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.*;

import static com.java.main.utils.Constants.SUCCESS;
import static com.java.main.utils.Constants.UNAUTHORIZED;

public class ChatHandler {

    public static Map<String, PublicKey> onlineUserMap = new HashMap<>();
    public static ArrayList<String> onlineUserList = new ArrayList<>();
    public static Map<String, ArrayList<String>> conversations = Collections.synchronizedMap(new HashMap<String, ArrayList<String>>());
    public static Map<String, String> symmetricKeys = new HashMap<>();
    public static void addOnlineUser(String username, PublicKey publicKey) {
        onlineUserMap.put(username, publicKey);
    }



    public static boolean getOnlineUsers(String username, String token) {
        JSONObject getUsersRequest = new JSONObject();
        getUsersRequest.put("requestID", 3);
        getUsersRequest.put("username", username);
        getUsersRequest.put("token", token);

        String serverResponseStr = ServerCommunicator.sendRequest(getUsersRequest.toString());
        JSONObject serverResponse = new JSONObject(serverResponseStr);
        int code = serverResponse.getInt("code");

        onlineUserMap.clear();
        onlineUserList.clear();
        switch (code) {
            case UNAUTHORIZED:
                return false;

            case SUCCESS:
                JSONArray onlineUsers = serverResponse.getJSONArray("onlineUsers");
                JSONObject onlineUser;
                for (int i = 0; i < onlineUsers.length(); i++) {
                    onlineUser = onlineUsers.getJSONObject(i);
                    String onlineUsername = onlineUser.getString("username");
                    String publickeyStr = onlineUser.getString("publicKey");
                    PublicKey publicKey = EncryptionUtils.stringToPublicKey(publickeyStr);
                    onlineUserMap.put(onlineUsername, publicKey);
                    onlineUserList.add(i, onlineUsername);
                }
                return true;

            default:
                return false;
        }

    }

    public static void addMessage(String username, String sourceUsername, String message){
        ArrayList<String> conversation = ChatHandler.conversations.get(sourceUsername);
        if (conversation == null) {
            conversation = new ArrayList<String>();
        }
        conversation.add(message);
        ChatHandler.conversations.put(sourceUsername, conversation);
    }

}
