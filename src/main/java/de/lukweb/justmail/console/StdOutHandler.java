package de.lukweb.justmail.console;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class StdOutHandler extends StreamHandler {

    public StdOutHandler() {
        setOutputStream(System.out);
        setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                SimpleDateFormat sdm = new SimpleDateFormat("[HH:mm]");
                return record.getLevel().getName() + sdm.format(new Date()) + ": " + colorByLevel(record.getLevel()) +
                        record.getMessage() + ConsoleColor.RESET + "\n";
            }
        });
        setLevel(Level.ALL);
    }

    private String colorByLevel(Level level) {
        ConsoleColor color;
        if (level.equals(Level.SEVERE)) {
            color = ConsoleColor.RED;
        } else if (level.equals(Level.WARNING)) {
            color = ConsoleColor.YELLOW;
        } else if (level.equals(Level.CONFIG)) {
            color = ConsoleColor.CYAN;
        } else {
            return "";
        }
        return color.toString();
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public synchronized void close() throws SecurityException {
        flush();
    }
}
