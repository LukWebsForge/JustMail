package de.lukweb.justmail.sql.storages;

import de.lukweb.justmail.sql.DB;
import de.lukweb.justmail.sql.DBStorage;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Users extends DBStorage<User> {

    public Users() {
        super(User.class, true);
        loadAll();
    }

    private void loadAll() {
        try {
            ResultSet rs = DB.getSql().querySelect("SELECT * FROM users");
            Domains domains = Storages.get(Domains.class);
            while (rs.next()) store.put(rs.getInt("id"), new User(rs.getInt("id"), rs.getString("username"),
                    domains.get(rs.getInt("domain")), rs.getString("fullEmail"), rs.getString("password"),
                    rs.getBytes("base64up"), rs.getInt("created")));
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

    public User getByBase64(String base64up) {
        for (User user : store.values()) if (user.getBase64UsernamePassword().trim().equals(base64up)) return user;
        return null;
    }

    @Override
    protected int insert(User object) {
        ResultSet rs = DB.getSql().queryUpdateWithKeys("INSERT INTO users (username, domain, fullEmail, password, " +
                        "base64up, created) VALUES (?, ?, ?, ?, ?)", object.getUsername(), object.getDomain().getId(),
                object.getFullEmail(), object.getHashedPassword(), object.getEncryptedBase64UP(),
                object.getCreated());
        return getFirstKey(rs);
    }

    @Override
    protected void update(User object) {
        DB.getSql().queryUpdate("UPDATE users username = ?, domain = ?, fullEmail = ?, password = ?, base64up = ? " +
                        "WHERE id = ?",
                object.getUsername(), object.getDomain().getId(), object.getFullEmail(), object.getHashedPassword(),
                object.getEncryptedBase64UP(), object.getId());
    }

    @Override
    public void delete(User object) {
        if (object.getId() == -1) return;
        store.remove(object.getId());
    }
}
