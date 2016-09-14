package de.lukweb.justmail.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MailSocketServer {

    private int port;
    private ServerSocket server;
    private ExecutorService pool = Executors.newCachedThreadPool();

    public MailSocketServer(int port) {
        this.port = port;
        start();
    }

    private void start() {
        try {
            server = new ServerSocket(port);
            while (!server.isClosed()) pool.execute(new MailSocketHandler(server.accept()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            pool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
    }


}
