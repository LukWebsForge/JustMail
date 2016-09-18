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
                JustLogger.logger().info("The domain " + domain.getDomain() + " with ID " + domain.getId() + " was " +
                        "added!");
                break;
            }
            case "list": {
                String list = "\n <<< Domains >>> \n";
                for (Domain domain : domains.getAll())
                    list += "Domain: " + domain.getDomain() + "; " +
                            "ID: " + domain.getId() + "; Enabled: " + domain.isEnabled() + "\n";
                JustLogger.logger().info(list);
                break;
            }
            case "status": {
                if (args.length < 2) {
                    sendTooLessArguments();
                    return;
                }
                Domain domain = domains.get(args[1]);
                if (domain == null) {
                    JustLogger.logger().warning("A domain with the name \"" + args[0] + "\" can't be found!");
                    return;
                }
                String text = "\n <<< Domain Status >>> \n";
                text += "Domain: " + domain.getDomain() + "\n";
                text += "ID: " + domain.getId() + "\n";
                text += "Enabled: " + domain.isEnabled() + "\n";
                JustLogger.logger().info(text);
                break;
            }
            case "enable": {
                if (args.length < 2) {
                    sendTooLessArguments();
                    return;
                }
                Domain domain = domains.get(args[1]);
                if (domain == null) {
                    JustLogger.logger().warning("A domain with the name \"" + args[0] + "\" can't be found!");
                    return;
                }
                domain.setEnabled(true);
                break;
            }
            case "disable": {
                if (args.length < 2) {
                    sendTooLessArguments();
                    return;
                }
                Domain domain = domains.get(args[1]);
                if (domain == null) {
                    JustLogger.logger().warning("A domain with the name \"" + args[0] + "\" can't be found!");
                    return;
                }
                domain.setEnabled(false);
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    sendTooLessArguments();
                    return;
                }
                Domain domain = domains.get(args[1]);
                if (domain == null) {
                    JustLogger.logger().warning("A domain with the name \"" + args[0] + "\" can't be found!");
                    return;
                }
                boolean force = (args.length > 2) && args[2].equalsIgnoreCase("force");
                if (!force) {
                    JustLogger.logger().warning("If you realy want to delete the domain \"" + domain.getDomain() +
                            "\". All mail account with this domain will be deleteted also. Run this command with " +
                            "the argument 'force' at the end!");
                    return;
                }
                domains.delete(domain);
                JustLogger.logger().info("The domain \"" + domain.getDomain() + "\" was deleted!");
                break;
            }
        }
    }

    private void sendTooLessArguments() {
        JustLogger.logger().warning("You have to pass arguments. For more information see [help domain]!");
    }

}
