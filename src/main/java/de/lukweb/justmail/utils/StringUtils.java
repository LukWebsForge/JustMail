package de.lukweb.justmail.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class StringUtils {

    public static String removeNewLine(String string) {
        return string.replaceAll("\\n", "").replaceAll("\\r", "");
    }

    public static byte[] toBytes(char[] string) {
        CharBuffer charBuffer = CharBuffer.wrap(string);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000');
        Arrays.fill(byteBuffer.array(), (byte) 0);
        return bytes;
    }

}
