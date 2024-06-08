package com.java.main;

import java.net.*;
import java.io.*;

public class Client {
   // String message = "Hello World";

    public static void testMessage(String message) {
        Socket s = null;
        try {
            int SERVER_PORT = 10000;
            s = new Socket("localhost", SERVER_PORT);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(message);
            String dataIn = in.readUTF();
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException: " + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
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
