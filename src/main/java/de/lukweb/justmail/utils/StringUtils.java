package de.lukweb.justmail.utils;

public class StringUtils {

    public static String removeNewLine(String string) {
        return string.replaceAll("\\n", "").replaceAll("\\r", "");
    }

}
