package de.lukweb.justmail.crypto;

import de.lukweb.justmail.console.JustLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyManager {

    private boolean loaded;
    private KeyStore store;

    public KeyManager(File keystore, String password) {
        try {
            store = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return;
        }
        loaded = load(keystore, password);
    }

    private boolean load(File keystore, String password) {
        if (keystore == null) {
            JustLogger.logger().severe("The keystore file isn't set in the config!");
            return false;
        }
        if (!keystore.exists()) {
            JustLogger.logger().severe("The keystore file doesn't exists!");
            return false;
        }
        if (!keystore.isFile()) {
            JustLogger.logger().severe("The keystore file isn't a file!");
            return false;
        }
        if (password == null) password = "";
        try {
            store.load(new FileInputStream(keystore), password.toCharArray());
            return true;
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            JustLogger.logger().severe("Error while loading certificate: " + e.getLocalizedMessage());
        }
        return false;
    }

    public KeyStore getStore() {
        return store;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
