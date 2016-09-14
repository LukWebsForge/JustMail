package de.lukweb.justmail.utils;

public class EmailAdress {

    private String all;
    private String adress;
    private String additionalName;

    public EmailAdress(String adress) {
        this.all = adress;
        this.adress = MailUtils.parseMailAdresses(adress);
        this.additionalName = adress.contains("<") ? adress.trim().split("<")[0] : null;
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
