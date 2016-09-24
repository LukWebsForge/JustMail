package de.lukweb.justmail.utils;

import de.lukweb.justmail.console.JustLogger;

import java.io.IOException;
import java.net.ServerSocket;

public class NetUtils {

    public static boolean isPortOpen(int port) {
        try {
            ServerSocket socket = new ServerSocket(port);
            socket.close();
            return true;
        } catch (IOException e) {
            JustLogger.logger().severe("An error occurred while binding port " + port + ": " + e.getLocalizedMessage());
            // e.printStackTrace();
        }
        return false;
    }

}
