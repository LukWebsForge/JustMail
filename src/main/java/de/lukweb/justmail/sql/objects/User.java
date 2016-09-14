package de.lukweb.justmail.sql.objects;

import de.lukweb.justmail.sql.interfaces.Unquie;

public class User implements Unquie {

    private int id;
    private String username;
    private Domain domain;
    private String fullEmail;
    private String password;
    private int created;

    public User(int id, String username, Domain domain, String fullEmail, String password, int created) {
        this.id = id;
        this.username = username;
        this.domain = domain;
        this.fullEmail = fullEmail;
        this.password = password;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCreated() {
        return created;
    }

    public void setId(int id) {
        this.id = id;
    }
}
