package de.lukweb.justmail.console.commands;

import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.console.commands.objects.ConsoleCommand;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Domain;
import de.lukweb.justmail.sql.storages.Domains;

public class DomainC implements ConsoleCommand {

    @Override
    public String getCommand() {
        return "domain";
    }

    @Override
    public String getDescription() {
        return "A command for editing the allowed domains on this server";
    }

    @Override
    public String getSyntax() {
        return "domain [add <domain> | list | status <domain> | enable <domain> | disable <domain> | remove <domain>]";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            sendTooLessArguments();
            return;
        }
        Domains domains = Storages.get(Domains.class);
        switch (args[0].toLowerCase()) {
            case "add": {
                if (args.length < 2) {
                    sendTooLessArguments();
                    return;
                }
                Domain domain = new Domain(-1, args[1], true);
                domains.save(domain);
                break;
            }
            case "list": {

                break;
            }
            case "enable": {

                break;
            }
            case "disable": {

                break;
            }
            case "remove": {

                break;
            }
        }
    }

    private void sendTooLessArguments() {
        JustLogger.logger().warning("You have to pass arguments. For more information see [help domain]!");
    }

}
