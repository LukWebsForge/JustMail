package de.lukweb.justmail;

public class Launcher {

    public static void main(String[] args) {
        JustMail mail = new JustMail();
        Runtime.getRuntime().addShutdownHook(new Thread(mail::stop));
    }

}
