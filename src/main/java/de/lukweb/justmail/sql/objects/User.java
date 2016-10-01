package de.lukweb.justmail.sql.objects;

import de.lukweb.justmail.crypto.CryptoUtils;
import de.lukweb.justmail.sql.interfaces.Unquie;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class User implements Unquie {

    private int id;
    private String username;
    private Domain domain;
    private String fullEmail;
    private String password; // encrypted SHA1 password
    private String base64UsernamePassword;
    private int created;

    public User(String username, Domain domain, String password) {
        this.id = -1;
        this.username = username;
        this.domain = domain;
        this.fullEmail = username + "@" + domain.getDomain();
        this.created = (int) (System.currentTimeMillis() / 1000);
        setPassword(password);
    }

    public User(int id, String username, Domain domain, String fullEmail, String password,
                byte[] base64UsernamePassword, int created) {
        this.id = id;
        this.username = username;
        this.domain = domain;
        this.fullEmail = fullEmail;
        this.password = password;
        this.base64UsernamePassword = CryptoUtils.decryptAES(base64UsernamePassword);
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Domain getDomain() {
        return domain;
    }

    public String getFullEmail() {
        return fullEmail;
    }

    public String getHashedPassword() {
        return password;
    }

    /**
     * @param password The password SHOULDN'T be encrypted
     */
    public void setPassword(String password) {
        this.password = CryptoUtils.generateSHA512Password(password);
        this.base64UsernamePassword = Base64.getEncoder().encodeToString(("\0" + fullEmail + "\0" + password)
                .getBytes(StandardCharsets.UTF_8));
    }

    public int getCreated() {
        return created;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBase64UsernamePassword() {
        return base64UsernamePassword;
    }

    public byte[] getEncryptedBase64UP() {
        return CryptoUtils.encryptAES(base64UsernamePassword);
    }
}
