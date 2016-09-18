package de.lukweb.justmail.utils;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.console.JustLogger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CryptoUtils {

    public static String generateSHA512Password(String password) {
        return generateSHA512Password(password, JustMail.getInstance().getConfig().getSalt().getBytes());
    }

    public static String generateSHA512Password(String password, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private static Cipher aes;

    static {
        try {
            aes = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            JustLogger.logger().warning("Cannot get cipher of AES: " + e.getMessage());
        }
    }

    public static byte[] encryptAES(String string, byte[] salt) {
        byte[] data = string.getBytes();
        try {
            SecretKeySpec key = new SecretKeySpec(salt, "AES");
            aes.init(Cipher.ENCRYPT_MODE, key);
            return aes.doFinal(data);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptAES(byte[] data, byte[] salt) {
        SecretKeySpec key = new SecretKeySpec(salt, "AES");
        try {
            aes.init(Cipher.DECRYPT_MODE, key);
            return new String(aes.doFinal(data));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SSLSocket upgradeConnection(Socket socket) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                                throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            }, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(
                    socket,
                    socket.getInetAddress().getHostAddress(),
                    socket.getPort(),
                    true);
            sslSocket.startHandshake();
            return sslSocket;
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            System.err.println("Cannot upgrade connection from " + socket.getInetAddress().getHostAddress() + " to " +
                    " SSL: " + e.getMessage());
        }
        return null;
    }

}
