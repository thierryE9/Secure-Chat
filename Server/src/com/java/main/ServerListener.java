package com.java.main;

import java.net.*;
import java.io.*;
public class ServerListener extends Thread {

    public void listen(){
        this.start();
    }

    public void run(){
        try {
            int SERVER_PORT = 8080;
            ServerSocket listenSocket = new ServerSocket(SERVER_PORT);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                ServerConnection c = new ServerConnection(clientSocket);
            }
        } catch(IOException e){
            System.out.println("Listen: " +e.getMessage());
        }
    }
}
