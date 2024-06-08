package com.java.main;

import java.net.*;
import java.io.*;

public class Server {
    String message = "Hello World";
    Socket s = null;

    public void testMessage() {
        try {
            int CLIENT_PORT = 12346;
            s = new Socket("localhost", CLIENT_PORT);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(message);
            String dataIn = in.readUTF();
            System.out.println(dataIn);
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
