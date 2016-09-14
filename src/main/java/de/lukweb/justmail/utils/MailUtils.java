package de.lukweb.justmail.utils;

public class MailUtils {

    public static String parseMailAdresses(String adress) {
        adress = adress.trim();
        if (!adress.contains("<") || !adress.contains(">")) return null;
        adress = adress.split("<")[1].split(">")[0];
        return adress;
    }

}
