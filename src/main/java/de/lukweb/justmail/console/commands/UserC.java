package de.lukweb.justmail.console.commands;

import de.lukweb.justmail.console.commands.objects.ConsoleCommand;

public class UserC implements ConsoleCommand {

    @Override
    public String getCommand() {
        return "user";
    }

    @Override
    public String getDescription() {
        return "A command for editing email adresses on this server";
    }

    @Override
    public String getSyntax() {
        return "user [add <email> <password> | list | changepw <email> <newpassword> | remove <email>]";
    }

    @Override
    public void execute(String[] args) {

    }

}
