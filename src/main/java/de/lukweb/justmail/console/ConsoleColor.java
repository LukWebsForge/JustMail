package de.lukweb.justmail.console;

public enum ConsoleColor {

    RESET(0),
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    WHITE(37);

    private int ansicode;

    ConsoleColor(int ansicode) {
        this.ansicode = ansicode;
    }

    private String getEnvPrefix() {
        return getEnvPrefix(Environment.UNIX);
    }

    private String getEnvPrefix(Environment environment) {
        switch (environment) {
            case UNIX:
                return "\u001B[";
            case WINDOWS:
                return (char)27 + "[";
            default:
                return getEnvPrefix(Environment.UNIX);
        }
    }

    @Override
    public String toString() {
        return getEnvPrefix() + ansicode + "m";
    }
}
