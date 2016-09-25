package de.lukweb.justmail.socket;

import de.lukweb.justmail.console.JustLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MailSocketServer {

    private int port;
    private ServerSocket server;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private boolean stop;

    public MailSocketServer(int port) {
        this.port = port;
    }

    public void start() {
        stop = false;
        try {
            server = new ServerSocket(port);
            while (!server.isClosed()) {
                if (Thread.currentThread().isInterrupted()) {
                    stop();
                    return;
                }
                pool.execute(new MailSocketHandler(server.accept()));
            }
        } catch (IOException e) {
            if (!stop)
                JustLogger.logger().severe("An error occurred while binding port " + port + ": " + e.getLocalizedMessage());
            // e.printStackTrace();
        }
    }

    public void stop() {
        stop = true;
        try {
            server.close();
        } catch (IOException e) {
        }

        MailSocketHandler.stopAll();

        pool.shutdown();
        try {
            if (!pool.awaitTermination(1, TimeUnit.SECONDS)) pool.shutdownNow();
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
    }

}
