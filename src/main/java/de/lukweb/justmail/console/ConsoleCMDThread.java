package de.lukweb.justmail.console;

import de.lukweb.justmail.console.commands.objects.ConsoleCommand;
import de.lukweb.justmail.console.commands.objects.ConsoleCommands;
import de.lukweb.justmail.utils.interfaces.CatchStreamCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
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
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                line = bufferReader.readLine();
            } catch (IOException e) {
                return;
            }
            if (line == null) {
                return;
            }
            if (callback != null) {
                callback.callback(line);
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
