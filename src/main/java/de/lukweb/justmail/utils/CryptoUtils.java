package de.lukweb.justmail.utils;

import de.lukweb.justmail.JustMail;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
