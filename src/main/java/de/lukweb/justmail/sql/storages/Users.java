package de.lukweb.justmail.sql.storages;

import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.sql.DB;
import de.lukweb.justmail.sql.DBStorage;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Password;
import de.lukweb.justmail.sql.objects.User;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Users extends DBStorage<User> {

    public Users() {
        super(User.class, true);
        loadAll();
    }

    private void loadAll() {
        try {
            ResultSet rs = DB.getSql().querySelect("SELECT users.*, passwords.* FROM users LEFT JOIN passwords " +
                    "ON users.id = passwords.userid");
            Domains domains = Storages.get(Domains.class);
            while (rs.next()) {
                int id = rs.getInt("id");
                Password password = new Password(rs.getBytes("password"), rs.getBytes("base64"));
                User user = new User(id, rs.getString("username"), domains.get(rs.getInt("domain")), rs.getString
                        ("fullEmail"), password, rs.getInt("created"));
                store.put(id, user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public User get(int id) {
        return store.get(id);
    }

    public User getByBase64(byte[] base64up) {
        for (User user : store.values()){
            if (Arrays.equals(user.getPasswords().getBase64(), base64up)){
                return user;
            }
        }
        return null;
    }

    public User getByMail(String mail) {
        for (User user : store.values()) if (user.getFullEmail().equalsIgnoreCase(mail.trim())) return user;
        return null;
    }

    public boolean exists(String mail) {
        return getByMail(mail) != null;
    }

    @Override
    protected int insert(User object) {
        ResultSet rs = DB.getSql().queryUpdateWithKeys(
                "INSERT INTO users (username, domain, fullEmail, created) VALUES (?, ?, ?, ?)",
                object.getUsername(), object.getDomain().getId(),
                object.getFullEmail(), object.getCreated());
        int id = getFirstKey(rs);
        DB.getSql().queryUpdate("INSERT INTO passwords (userid, password, base64) VALUES (?, ?, ?)",
                id, object.getPasswords().getPassword(), object.getPasswords().getBase64());
        return id;
    }

    @Override
    protected void update(User object) {
        DB.getSql().queryUpdate("UPDATE users SET username = ?, domain = ?, fullEmail = ? WHERE id = ?",
                object.getUsername(), object.getDomain().getId(), object.getFullEmail(), object.getId());
        DB.getSql().queryUpdate("UPDATE passwords SET password = ?, base64 = ?",
                object.getPasswords().getPassword(), object.getPasswords().getBase64());
    }

    @Override
    public void delete(User object) {
        if (object.getId() == -1) return;
        store.remove(object.getId());
        DB.getSql().queryUpdate("DELETE FROM users WHERE id = ?", object.getId());
    }
}
