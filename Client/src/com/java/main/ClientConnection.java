package com.java.main;

import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class ClientConnection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public ClientConnection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection: " + e.getMessage());
        }
    }

    public void run() {
        try {
            String inputStr = in.readUTF();
            JSONObject data = new JSONObject(inputStr);
            String response = RequestHandler.handleRequest(data);
            out.writeUTF(response);
        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("close failed");
            }
        }
    }
}
