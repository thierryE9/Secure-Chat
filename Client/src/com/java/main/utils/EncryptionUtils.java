package com.java.main.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptionUtils {
    public static final String NO_ENCRYPTION = "none";
    public static final String SYMMETRIC = "symmetric";
    public static final String ASYMMETRIC = "asymmetric";

    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    public static SecretKey generateSymmetricKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String encryptSymmetric(SecretKey symmetricKey, String message) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
            byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedMessageBytes);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decryptSymmetric(SecretKey symmetricKey, String encryptedMessage) {
        byte[] decryptedMessageBytes;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, symmetricKey);
            byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
            decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);
        } catch (Exception e){
            return null;
        }
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public static String encryptAsymmetric(PublicKey publicKey, SecretKey symmetricKey) {   //encrypts symmetric using RSA
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.WRAP_MODE, publicKey);
            byte[] wrappedKey = cipher.wrap(symmetricKey);
            return Base64.getEncoder().encodeToString(wrappedKey);

        } catch (Exception e) {
            return null;
        }
    }

    public static SecretKey decryptAsymmetric(PrivateKey privateKey, String encryptedSymmetricKey) {    //decrypts received symmetric key using RSA
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            byte[] decodedKey = Base64.getDecoder().decode(encryptedSymmetricKey);
            SecretKey secretKey = (SecretKey) cipher.unwrap(decodedKey, "AES", Cipher.SECRET_KEY);
            return secretKey;
        } catch (Exception e) {
            System.out.println("exception");
            return null;
        }
    }

    public static String publicKeyToString(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKeyBytes);
        return publicKeyString;
    }

    public static PublicKey stringToPublicKey(String publicKeyString) {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = null;
        try {
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            System.out.println("InvalidKeySpecException: " + e.getMessage());
        }
        return publicKey;
    }

    public static String symmetricKeyToString(SecretKey symmetricKey) {
        byte[] privateKeyBytes = symmetricKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    public static SecretKey stringToSymmetricKey(String symmetricKeyStr) {
        if(symmetricKeyStr == null)
            return null;
        byte[] decodedKey = Base64.getDecoder().decode(symmetricKeyStr.getBytes());
        return new SecretKeySpec(decodedKey, "AES");
    }

}
