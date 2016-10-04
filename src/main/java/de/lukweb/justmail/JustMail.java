package de.lukweb.justmail;

import de.lukweb.justmail.config.Config;
import de.lukweb.justmail.console.ConsoleCMDThread;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.crypto.KeyManager;
import de.lukweb.justmail.socket.ImapSocketServer;
import de.lukweb.justmail.socket.MailSocketServer;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.storages.Domains;
import de.lukweb.justmail.utils.NetUtils;

import java.io.File;

public class JustMail {

    private static JustMail instance;

    private KeyManager key;
    private Config config;

    private MailSocketServer smtpServer;
    private ImapSocketServer imapServer;
    private Thread consoleThread;

    private boolean stopped;

    public JustMail() {
        instance = this;
        start();
    }

    private void start() {
        System.out.println("Starting JustMail...");

        config = new Config(new File("config.ini"));
        if (!config.isValid()) return;

        key = new KeyManager(new File(config.getKeystore()), config.getKeyPassword());
        if (!key.isLoaded()) return;

        if (!NetUtils.isPortOpen(config.getSmtpPort())) return;

        smtpServer = new MailSocketServer(config.getSmtpPort());
        Thread smtpThread = new Thread(() -> smtpServer.start());
        smtpThread.setName("SMTP Server");
        smtpThread.start();

        imapServer = new ImapSocketServer(config.getImapPort());
        Thread imapThread = new Thread(() -> imapServer.start());
        imapThread.setName("IMAP Server");
        imapThread.start();

        consoleThread = new Thread(new ConsoleCMDThread());
        consoleThread.setName("Console Thread");
        consoleThread.start();

        Storages.get(Domains.class);

        JustLogger.logger().info("Started the JustMail server successfully!");
    }

    public void stop() {
        if (stopped) return;
        stopped = true;
        JustLogger.logger().info("Stopping the mail server...");
        Storages.shutdown();
        if (smtpServer != null) smtpServer.stop();
        if (imapServer != null) imapServer.stop();
        if (consoleThread != null) consoleThread.interrupt();
        JustLogger.logger().info("See you next time!");
    }

    public static JustMail getInstance() {
        return instance;
    }

    public Config getConfig() {
        return config;
    }

    public KeyManager getKey() {
        return key;
    }
}
