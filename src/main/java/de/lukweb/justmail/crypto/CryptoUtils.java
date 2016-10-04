package de.lukweb.justmail.crypto;

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
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.function.Consumer;

public class CryptoUtils {

    public static String generateSHA512Password(String password) {
        return generateSHA512Password(password, JustMail.getInstance().getConfig().getSalt().getBytes
                (StandardCharsets.UTF_8));
    }

    public static String generateSHA512Password(String password, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private static Cipher aes;
    private static SecretKeySpec key;

    static {
        try {
            aes = Cipher.getInstance("AES");

            byte[] keyBytes = JustMail.getInstance().getConfig().getSalt().getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            keyBytes = sha.digest(keyBytes);
            keyBytes = Arrays.copyOf(keyBytes, 16);

            key = new SecretKeySpec(keyBytes, "AES");

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            JustLogger.logger().warning("Cannot get cipher of AES: " + e.getMessage());
        }
    }

    public static byte[] encryptAES(String string) {
        byte[] data = string.getBytes();
        try {
            aes.init(Cipher.ENCRYPT_MODE, key);
            return aes.doFinal(data);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptAES(byte[] data) {
        try {
            aes.init(Cipher.DECRYPT_MODE, key);
            return new String(aes.doFinal(data), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SSLSocket upgradeConnection(Socket socket, boolean server, Consumer<SSLSocket> callback) {
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(JustMail.getInstance().getKey().getStore(), JustMail.getInstance().getConfig().getKeyPassword()
                    .toCharArray());
            sc.init(kmf.getKeyManagers(), new TrustManager[]{
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
            sslSocket.setEnableSessionCreation(true);
            sslSocket.setUseClientMode(!server);

            sslSocket.setEnabledCipherSuites(sc.getServerSocketFactory().getSupportedCipherSuites());
            sslSocket.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"});

            sslSocket.addHandshakeCompletedListener(handshakeCompletedEvent ->
                    callback.accept(handshakeCompletedEvent.getSocket()));

            sslSocket.startHandshake();

        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            JustLogger.logger().warning("Cannot upgrade connection from " + socket.getInetAddress().getHostAddress() + " to " +
                    "TLS: " + e.getMessage());
        } catch (UnrecoverableKeyException | KeyStoreException e) {
            JustLogger.logger().warning("Cannot load key: " + e.getLocalizedMessage());
        }
        return null;
    }

}
