package de.lukweb.justmail.sql.objects;

import de.lukweb.justmail.sql.interfaces.Unquie;

public class Domain implements Unquie {

    private int id;
    private String domain;
    private boolean enabled;

    public Domain(int id, String domain, boolean enabled) {
        this.id = id;
        this.domain = domain;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
