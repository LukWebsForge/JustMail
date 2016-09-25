package de.lukweb.justmail.console.commands;

import de.lukweb.justmail.console.ConsoleCMDThread;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.console.commands.objects.ConsoleCommand;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Domain;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.sql.storages.Domains;
import de.lukweb.justmail.sql.storages.Users;

import java.text.SimpleDateFormat;
import java.util.Date;

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
                String list = "\n <<< Users >>> \n";
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                for (User user : users.getAll())
                    list += "User: " + user.getFullEmail() + "; ID: " + user.getId() + "; Created: " +
                            sdf.format(new Date(user.getCreated())) + "\n";
                JustLogger.logger().info(list);
                break;
            }
            case "status": {
                if (args.length < 2) {
                    sendTooLessArguments();
                    return;
                }
                User user = users.getByMail(args[1]);
                if (user == null) {
                    JustLogger.logger().warning("A user with the email \"" + args[0] + "\" can't be found!");
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MMM-yyyy");
                String text = "\n <<< User Status >>> \n";
                text += "EMail adress: " + user.getFullEmail() + "\n";
                text += "ID: " + user.getId() + "\n";
                text += "Created: " + sdf.format(new Date(user.getCreated())) + "\n";
                JustLogger.logger().info(text);
                break;
            }
            case "changepw": {
                if (args.length < 3) {
                    sendTooLessArguments();
                    break;
                }
                User user = users.getByMail(args[1]);
                if (user == null) {
                    JustLogger.logger().warning("Cannot find user with e-mail-adress \"" + args[1] + "\"");
                    break;
                }
                user.setPassword(args[2]);
                JustLogger.logger().info("The password for the user " + user.getFullEmail() + " was changed " +
                        "successfully!");
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    sendTooLessArguments();
                    break;
                }
                User user = users.getByMail(args[1]);
                if (user == null) {
                    JustLogger.logger().warning("Cannot find user with e-mail-adress \"" + args[1] + "\"");
                    break;
                }
                String email = user.getFullEmail();
                System.out.println("Do you really want to remove the user \"" + email + "\"? (yes/no) -" + " ");
                ConsoleCMDThread.getInstance().setCallback(answer -> {
                    if (!(answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y") ||
                            answer.equalsIgnoreCase("true"))) {
                        JustLogger.logger().info("You cannled the deletion!");
                        return true;
                    }
                    users.delete(user);
                    JustLogger.logger().info("The user " + email + " was deleted successfully!");
                    return true;
                });
                break;
            }
            default: {
                sendTooLessArguments();
                break;
            }
        }
    }

    private void sendTooLessArguments() {
        JustLogger.logger().warning("You have to pass arguments. For more information see [help user]!");
    }

}
