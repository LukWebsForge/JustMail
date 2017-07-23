package de.lukweb.justmail.sql.objects;

import de.lukweb.justmail.sql.interfaces.Unquie;

public class Mailbox implements Unquie {

    private int id;
    private String name;
    private User owner;

    public Mailbox(int id, String name, User owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

}
