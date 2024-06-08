package com.java.main.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.Random;

public class IPUtils {
    // Get the IP address that is most likely connected to a gateway
    public static String getIPAddress() {

        String address = "localhost";

        try{
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    if (inetAddress.isLoopbackAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isMulticastAddress()) {
                        continue;
                    }

                    // Check if the address is IPv4
                    if (inetAddress.getAddress().length == 4) {
                        address = inetAddress.getHostAddress();
                        return address;
                    }
                }
            }
            return address;
        } catch (Exception e ){
            return address;
        }
    }

    public static int getNewPort(){
        int port;
        boolean portAvailable;
        do {
            // Generate a random port number between 1024 and 65535
            port = (new Random()).nextInt(64512) + 1024;
            portAvailable = false;

            try {
                // Check if a port number is available
                ServerSocket socket = new ServerSocket(port);
                socket.close();
                portAvailable = true;
            } catch (Exception e) {}
        } while (!portAvailable); // End if a port number is available

        return port;
    }

}
