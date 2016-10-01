package de.lukweb.justmail.socket;

import de.lukweb.justmail.console.JustLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class SocketServer {

    private int port;
    private ServerSocket server;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private boolean stop;

    public SocketServer(int port) {
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
                pool.execute(onSocketAccepted(server.accept()));
            }
        } catch (IOException e) {
            if (!stop)
                JustLogger.logger().severe("An error occurred while binding port " + port + ": " + e.getLocalizedMessage());
            // e.printStackTrace();
        }
    }

    protected abstract Runnable onSocketAccepted(Socket socket);

    public void stop() {
        stop = true;
        try {
            server.close();
        } catch (IOException e) {
        }

        onServerStop();

        pool.shutdown();
        try {
            if (!pool.awaitTermination(1, TimeUnit.SECONDS)) pool.shutdownNow();
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
    }

    protected abstract void onServerStop();

}
