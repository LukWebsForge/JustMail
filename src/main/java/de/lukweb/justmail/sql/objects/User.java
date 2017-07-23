package de.lukweb.justmail.sql.objects;

import de.lukweb.justmail.sql.interfaces.Unquie;

public class User implements Unquie {

    private int id;
    private String username;
    private Domain domain;
    private String fullEmail;
    private Password passwords;
    private int created;

    public User(String username, Domain domain, char[] password) {
        this.id = -1;
        this.username = username;
        this.domain = domain;
        this.fullEmail = username + "@" + domain.getDomain();
        this.created = (int) (System.currentTimeMillis() / 1000);
        this.passwords = new Password(new byte[]{}, new byte[]{});
        passwords.setUser(this);
        passwords.setPassword(password);
    }

    public User(int id, String username, Domain domain, String fullEmail, Password passwords, int created) {
        this.id = id;
        this.username = username;
        this.domain = domain;
        this.fullEmail = fullEmail;
        this.passwords = passwords;
        passwords.setUser(this);
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

    public Password getPasswords() {
        return passwords;
    }

    public int getCreated() {
        return created;
    }

    public void setId(int id) {
        this.id = id;
    }
}
