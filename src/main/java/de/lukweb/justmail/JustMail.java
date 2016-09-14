package de.lukweb.justmail;

import de.lukweb.justmail.config.Config;
import de.lukweb.justmail.console.ConsoleCMDThread;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.crypto.KeyManager;
import de.lukweb.justmail.socket.MailSocketServer;

import java.io.File;

public class JustMail {

    private static JustMail instance;
    private KeyManager key;
    private Config config;
    private MailSocketServer smtpServer;

    public JustMail() {
        instance = this;
        start();
    }

    private void start() {
        config = new Config(new File("config.ini"));
        if (!config.isValid()) return;
        key = new KeyManager(new File(config.getKeystore()), config.getKeyPassword());
        if (!key.isLoaded()) return;
        new Thread(() -> smtpServer = new MailSocketServer(config.getSmtpPort())).start();
        new Thread(new ConsoleCMDThread()).start();
        JustLogger.logger().info("Started the JustMail server successfully!");
    }

    public void stop() {
        JustLogger.logger().info("Stopping the mail server...");
        if (smtpServer == null) return;
        smtpServer.stop();
    }

    public static JustMail getInstance() {
        return instance;
    }

    public Config getConfig() {
        return config;
    }

    public MailSocketServer getSmtpServer() {
        return smtpServer;
    }
}
