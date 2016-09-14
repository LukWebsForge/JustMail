package de.lukweb.justmail.console;

public enum Environment {
    UNIX,
    WINDOWS;

    public static Environment get() {
        return JustLogger.getInstance().getEnvironment();
    }
}
