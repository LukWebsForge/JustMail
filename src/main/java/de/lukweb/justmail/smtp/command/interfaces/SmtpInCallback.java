package de.lukweb.justmail.smtp.command.interfaces;

public interface SmtpInCallback {

    /**
     * There can be only one active callback at the same time
     *
     * @return false if you want to contine the loop, true when you're finished
     */
    boolean callback(String cache);

}
