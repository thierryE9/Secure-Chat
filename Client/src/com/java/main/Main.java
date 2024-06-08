package com.java.main;

import com.java.main.utils.EncryptionUtils;
import com.java.main.utils.IPUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ClientListener client = new ClientListener();
        client.listen();
        UserInterface.displayMenu();


//        Scanner scanner = new Scanner(System.in);
//        KeyPair keys = EncryptionUtils.generateKeyPair();
//        PrivateKey privateKey = keys.getPrivate();
//        PublicKey publicKey = keys.getPublic();
////        String publicKeyStr = EncryptionUtils.publicKeyToString(publicKey);
////        PublicKey publicKey1 = EncryptionUtils.stringToPublicKey(publicKeyStr);
//
//
//
//        SecretKey key = EncryptionUtils.generateSymmetricKey();
//
//        String keyStr = EncryptionUtils.encryptAsymmetric(publicKey, key);
//        SecretKey key1 = EncryptionUtils.decryptAsymmetric(privateKey, keyStr);
//
////        String keyStr = EncryptionUtils.privateKeyToString(key);
////        SecretKey key1 = EncryptionUtils.stringToPrivateKey(keyStr);
////
//        scanner.next();

//        JSONObject send = new JSONObject();
//        send.put("requestID", 4);
//        send.put("username", "test1");
//        send.put("message", "hello");
//        send.put("destination", "test1");
//
//        ClientListener client = new ClientListener();
//        client.listen();
//
//        Client.testMessage(send.toString());


    }
}