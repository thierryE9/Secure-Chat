package com.java.main;

import com.java.main.utils.SerializableJSONObject;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageFileManager {
    private static HashMap<String, ArrayList<SerializableJSONObject>> savedMessages = new HashMap<>();

    public static void start() {
        loadMessagesFromFile();
        forwardSavedMessages(1000);
        saveMessagesToFile(30 * 1000);
    }

    public static void saveMessagesToFile(int timeMilliSeconds) {
        new Thread(() -> {
            while (true) {
                try {
                    FileOutputStream fileOut = new FileOutputStream("savedMessages.ser");
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(savedMessages);
                    out.close();
                    fileOut.close();
                    System.out.println("Saved messages HashMap has been serialized and saved to savedMessages.ser");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(timeMilliSeconds);
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    public static void loadMessagesFromFile() {

        try {
            FileInputStream fileIn = new FileInputStream("savedMessages.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            savedMessages = (HashMap<String, ArrayList<SerializableJSONObject>>) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Saved messages HashMap has been deserialized from savedMessages.ser");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Saved messages HashMap class not found");
            e.printStackTrace();
        }
    }

    public static void saveMessageToForwardWhenOnline(JSONObject in, String destinationUser){
        if(!savedMessages.containsKey(destinationUser)){
            savedMessages.put(destinationUser, new ArrayList<SerializableJSONObject>());
        }
        savedMessages.get(destinationUser).add(new SerializableJSONObject(in.toString()));
    }

    public static void forwardSavedMessages(int timeMilliSeconds){
            new Thread(() -> {
                while(true){
                    for (Map.Entry<String, ArrayList<SerializableJSONObject>> entry : savedMessages.entrySet()) {
                        String username = entry.getKey();

                        Iterator<SerializableJSONObject> iterator = entry.getValue().iterator();
                        while (iterator.hasNext()) {
                            JSONObject message = iterator.next();
                            if (UserFileManager.isUserOnline(username)) {
                                iterator.remove();
                                try {
                                    RequestHandler.forwardMessage(message);
                                } catch (Exception e) {
                                    // Error in forward message
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(timeMilliSeconds);
                    } catch (InterruptedException e) {}
                }
            }).start();
    }
}
