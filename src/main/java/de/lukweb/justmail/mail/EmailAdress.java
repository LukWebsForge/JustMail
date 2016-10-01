package de.lukweb.justmail.mail;

public class EmailAdress {

    private String all;
    private String adress;
    private String additionalName;

    public EmailAdress(String adress) {
        this.all = adress;
        this.adress = parseMailAdresses(adress);
        this.additionalName = adress.contains("<") ? adress.trim().split("<")[0] : null;
    }

    private String parseMailAdresses(String adress) {
        adress = adress.trim();
        if (!adress.contains("<") || !adress.contains(">")) return adress;
        adress = adress.split("<")[1].split(">")[0];
        return adress;
    }

    public String getAll() {
        return all;
    }

    public String getAdress() {
        return adress;
    }

    public String getDomain() {
        return adress.split("@")[1];
    }

    public String getAdditionalName() {
        return additionalName;
    }
}
