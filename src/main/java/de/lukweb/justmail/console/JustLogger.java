package de.lukweb.justmail.console;

import de.lukweb.justmail.JustMail;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JustLogger {

    private static JustLogger instance;

    public static JustLogger getInstance() {
        if (instance == null) instance = new JustLogger();
        return instance;
    }

    public static Logger logger() {
        return getInstance().getLogger();
    }

    private Logger logger;
    private Environment environment;

    private JustLogger() {
        this.environment = checkEnvironment();
        this.logger = setupLogger();
    }

    private Logger setupLogger() {
        Logger logger = Logger.getLogger("JustMail");

        for (Handler iHandler : logger.getParent().getHandlers()) {
            logger.getParent().removeHandler(iHandler);
        }

        logger.addHandler(new StdOutHandler());
        if (JustMail.getInstance().getConfig().isDebug()) {
            logger.setLevel(Level.ALL);
        } else {
            logger.setLevel(Level.CONFIG);
        }

        return logger;
    }

    private Environment checkEnvironment() {
        return System.getenv().containsKey("SHELL") ? Environment.UNIX : Environment.WINDOWS;
    }

    public Logger getLogger() {
        return logger;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
