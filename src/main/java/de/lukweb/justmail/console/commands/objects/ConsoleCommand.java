package de.lukweb.justmail.console.commands.objects;

public interface ConsoleCommand {

    String getCommand();
    String getDescription();
    String getSyntax();

    void execute(String[] args);

}
