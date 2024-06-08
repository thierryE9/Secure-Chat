package com.java.main;


import com.java.main.utils.EncryptionUtils;
import com.java.main.utils.Constants;
import org.json.JSONObject;
import com.java.main.utils.Constants;

import javax.crypto.SecretKey;
import java.util.ArrayList;


public class RequestHandler extends Thread {

    public static String handleRequest(JSONObject input) {
        String response = "";
        switch (input.getInt("requestID")) {
            case Constants.RECIEVEMESSAGEID:  //receive message
                handleReceivedMessage(input);

        }
        return response;
    }

    private static void handleReceivedMessage(JSONObject input) {
        String encryptedMessage = input.getString("message");
        String sourceUsername = input.getString("sourceUsername");
        String encryptionType = input.getString("encryptionType");
        String destinationUserName = input.getString("destinationUserName");
        String message = "";


        switch (encryptionType) {
            case EncryptionUtils.NO_ENCRYPTION:
                message = encryptedMessage;
                break;
            case EncryptionUtils.SYMMETRIC:

                String formattedDateTime = input.getString("dateTime");
                SecretKey symmetricKey = EncryptionUtils.stringToSymmetricKey(ChatHandler.symmetricKeys.get(sourceUsername));
                String plainMessage = EncryptionUtils.decryptSymmetric(symmetricKey, encryptedMessage);
                String formattedMessage = "[" + formattedDateTime + "] " + sourceUsername + ": " + plainMessage;
                if (UserInterface.selectedUser.equals(sourceUsername))
                    System.out.println(formattedMessage);
                ArrayList<String> conversation = ChatHandler.conversations.get(sourceUsername);
                if (conversation == null) {
                    conversation = new ArrayList<String>();
                }
                conversation.add(formattedMessage);
                ChatHandler.conversations.put(sourceUsername, conversation);
                break;
            case EncryptionUtils.ASYMMETRIC:
                SecretKey secretKey = EncryptionUtils.decryptAsymmetric(EncryptionUtils.privateKey, encryptedMessage);
                ChatHandler.symmetricKeys.put(sourceUsername, EncryptionUtils.symmetricKeyToString(secretKey));

                break;

        }

//        if(UserInterface.selectedUser.equals(input.getString("sourceUsername")) && message.length() > 0){
//            System.out.println("received:\n" +input.getString("message"));
//        }

    }


}
