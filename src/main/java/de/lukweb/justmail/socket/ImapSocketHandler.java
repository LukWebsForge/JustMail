package de.lukweb.justmail.socket;

import java.net.Socket;

public class ImapSocketHandler implements Runnable {

    private Socket socket;

    public ImapSocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }

    public static void stopAll() {

    }
}
