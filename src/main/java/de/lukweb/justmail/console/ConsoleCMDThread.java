package de.lukweb.justmail.console;

import de.lukweb.justmail.console.commands.objects.ConsoleCommand;
import de.lukweb.justmail.console.commands.objects.ConsoleCommands;
import de.lukweb.justmail.utils.interfaces.CatchStreamCallback;

import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

public class ConsoleCMDThread implements Runnable {

    private static ConsoleCMDThread instance;

    public static ConsoleCMDThread getInstance() {
        return instance;
    }

    private CatchStreamCallback callback;

    public ConsoleCMDThread() {
        instance = this;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted() && scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line == null) {
                return;
            }
            if (callback != null) {
                if (callback.callback(line)) setCallback(null);
                continue;
            }
            if (line.trim().isEmpty()) continue;
            String[] args = line.split(" ");
            String commandStr = args[0];

            ConsoleCommand command = ConsoleCommands.get(commandStr);

            if (command == null) {
                out("Use [help] to show a list of available commands!");
                continue;
            }

            command.execute(Arrays.copyOfRange(args, 1, args.length));
        }
    }

    private void out(String string) {
        out(string, Level.INFO);
    }

    private void out(String string, Level level) {
        JustLogger.logger().log(level, string);
    }

    public CatchStreamCallback getCallback() {
        return callback;
    }

    public void setCallback(CatchStreamCallback callback) {
        this.callback = callback;
    }
}
