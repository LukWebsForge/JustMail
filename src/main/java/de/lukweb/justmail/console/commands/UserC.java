package de.lukweb.justmail.console.commands;

import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.console.commands.objects.ConsoleCommand;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Domain;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.sql.storages.Domains;
import de.lukweb.justmail.sql.storages.Users;

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
        return "user [add <email> <password> | list | status <username> | changepw <email> <newpassword> | remove " +
                "<email>]";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            sendTooLessArguments();
            return;
        }
        Users users = Storages.get(Users.class);
        switch (args[0].toLowerCase()) {
            case "add": {
                if (args.length < 3) {
                    sendTooLessArguments();
                    break;
                }
                String domainStr = args[1].split("@")[1];
                Domain domain = Storages.get(Domains.class).get(domainStr);
                if (domain == null) {
                    JustLogger.logger().warning("Cannot find the domain \"" + domainStr + "\"");
                    break;
                }
                User user = new User(args[1].split("@")[0], domain, args[2]);
                users.save(user);
                JustLogger.logger().info("Created user \"" + args[1] + "\" with ID " + user.getId());
                break;
            }
            case "list": {

                break;
            }
            case "status": {

                break;
            }
            case "changepw": {

                break;
            }
            case "remove": {

                break;
            }
        }
    }

    private void sendTooLessArguments() {
        JustLogger.logger().warning("You have to pass arguments. For more information see [help user]!");
    }

}
