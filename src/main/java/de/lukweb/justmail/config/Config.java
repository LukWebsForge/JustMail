package de.lukweb.justmail.config;

import de.lukweb.justmail.console.JustLogger;
import org.ini4j.Ini;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static org.ini4j.Profile.Section;

public class Config {

    private File file;
    private boolean valid;

    private int smtpPort;
    private int imapPort;

    private String host;

    private String salt;
    private String keystore;
    private String keyPassword;

    private boolean forceSSL;
    private long maxMailSize;

    private boolean debug;

    public Config(File file) {
        this.file = file;
        createSilent();
        this.valid = parse();
    }

    private void createSilent() {
        if (file != null && file.exists() && file.isFile()) return;
        if (file == null) return;
        try {
            file.createNewFile();
            Ini ini = new Ini(file);

            ini.setComment("JustMail configuration");

            Section smtp = ini.add("smtp");
            smtp.add("port", 25);
            smtp.add("host", "example.com");
            smtp.putComment("host", "Add other domains using the command line interface");

            Section imap = ini.add("imap");
            imap.add("port", 143);

            Section database = ini.add("database");
            database.add("salt", "ChangeMeOrTheApplicationIsVeryUnsafe");
            database.putComment("salt", "Don't change the salt after the first user was generated, otherwise users " +
                    "can't login!");
            database.add("max_mail_size", "10485760");
            database.putComment("max_mail_size", "This is the maxium size for a email, here it's 10MB.");

            Section keys = ini.add("keys");
            keys.putComment("keys", "You have to use a PKCS12 certificate, because Java has no option to load a X509 " +
                    "keypair. laltestit");

            ini.store();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            boolean created = file.createNewFile();
            PrintWriter writer = new PrintWriter(new FileOutputStream(file), true);

            writer.println(";JustMail configuration\n\n" +
                    "[smtp]\n" +
                    "port=25\n" +
                    "host=example.com\n" +
                    ";Add other domains using the command line\n" +
                    "[imap]\n" +
                    "port=143\n\n" +
                    "[database]\n" +
                    ";Don't change the salt after the first user was generated, otherwise users can't login!\n" +
                    "salt=ChangeMeOrTheApplicationIsVeryUnsafe!\n" +
                    ";This is the maxium size for a email, here it's 10MB.\n" +
                    "max_mail_size=10485760\n" +
                    "[keys]\n" +
                    ";You have to use a PKCS12 certificate, because Java has no option to load a X509 keypair.\n" +
                    ";You can export the certificate using openssl: openssl pkcs12 -export -name mailservercert -in " +
                    "public.pem -inkey private.pem -out keystore.p12\n" +
                    "keystore=keystore.p12\n" +
                    "password=1234\n" +
                    "forceSsl=false\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean parse() {
        Ini ini = new Ini();
        try {
            ini.load(file);

            Section smtp = ini.get("smtp");
            smtpPort = Integer.parseInt(smtp.get("port"));
            host = smtp.get("host");

            Section imap = ini.get("imap");
            imapPort = Integer.parseInt(imap.get("port"));

            Section db = ini.get("database");
            salt = db.get("salt");
            maxMailSize = Long.parseLong(db.get("max_mail_size"));

            Section keys = ini.get("keys");
            keystore = keys.get("keystore");
            keyPassword = keys.get("password");
            forceSSL = Boolean.parseBoolean(keys.get("forcessl"));

            Section debug = ini.get("debug");
            if (debug != null) {
                this.debug = Boolean.parseBoolean(debug.get("messages"));
            }

            return true;

        } catch (Exception e) {
            JustLogger.logger().severe("Error while reading config: " + e.getLocalizedMessage());
        }
        return false;
    }

    public boolean isValid() {
        return valid;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public boolean isForceSSL() {
        return forceSSL;
    }

    public int getImapPort() {
        return imapPort;
    }

    public String getSalt() {
        return salt;
    }

    public String getKeystore() {
        return keystore;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public long getMaxMailSize() {
        return maxMailSize;
    }

    public String getHost() {
        return host;
    }

    public boolean isDebug() {
        return debug;
    }
}
