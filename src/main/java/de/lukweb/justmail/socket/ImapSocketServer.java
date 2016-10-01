package de.lukweb.justmail.socket;

import java.net.Socket;

public class ImapSocketServer extends SocketServer {

    public ImapSocketServer(int port) {
        super(port);
    }

    @Override
    protected Runnable onSocketAccepted(Socket socket) {
        return new ImapSocketHandler(socket);
    }

    @Override
    protected void onServerStop() {
        ImapSocketHandler.stopAll();
    }

}
