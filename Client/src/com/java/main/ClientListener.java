package com.java.main;

import com.java.main.utils.IPUtils;

import java.io.IOException;
import java.net.*;
public class ClientListener extends Thread {
    private static int port;
    public void listen(){
        this.start();
    }
    public void run(){
        try {
            port = IPUtils.getNewPort();
            ServerSocket listenSocket = new ServerSocket(port);
            while (true) {
                Socket serverSocket = listenSocket.accept();
                ClientConnection c = new ClientConnection(serverSocket);
            }
        } catch(IOException e){
            System.out.println("Listen: " +e.getMessage());
        }
    }

    public static int getPort() {
        return port;
    }
}
