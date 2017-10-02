package de.lukweb.justmail.sql.storages;

import de.lukweb.justmail.sql.DB;
import de.lukweb.justmail.sql.DBStorage;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.objects.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mailboxes extends DBStorage<Mailbox> {

    public Mailboxes() {
        super(Mailbox.class, false);
    }

    @Override
    public List<Mailbox> getAll() {
        List<Mailbox> mailboxes = new ArrayList<>();
        ResultSet rs = DB.getSql().querySelect("SELECT * FROM mailboxes");
        try {
            while (rs.next()) mailboxes.add(mapResultSet(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mailboxes;
    }

    @Override
    public Mailbox get(int id) {
        if (store.containsKey(id)) {
            setUsed(id);
            return store.get(id);
        }
        ResultSet rs = DB.getSql().querySelect("SELECT * FROM mailboxes WHERE owner = ?", id);
        try {
            if (!rs.first()) return null;
            Mailbox mailbox = mapResultSet(rs);
            store.put(mailbox.getId(), mailbox);
            setUsed(mailbox.getId());
            return mailbox;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Mailbox> get(User user) {
        List<Mailbox> mailboxes = new ArrayList<>();
        ResultSet rs = DB.getSql().querySelect("SELECT * FROM mailboxes WHERE owner = ?", user.getId());
        try {
            while (rs.next()) {
                Mailbox mailbox = mapResultSet(rs);
                if (!store.containsKey(mailbox.getId())) store.put(mailbox.getId(), mailbox);
                setUsed(mailbox.getId());
                mailboxes.add(mailbox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mailboxes;
    }

    public Mailbox get(User user, String name) {
        List<Mailbox> mailboxes = get(user);
        for (Mailbox mailbox : mailboxes) if (mailbox.getName().equalsIgnoreCase(name)) return mailbox;
        return null;
    }

    @Override
    protected int insert(Mailbox object) {
        ResultSet rs = DB.getSql().queryUpdateWithKeys("INSERT INTO mailboxes (`name`, `owner`) VALUES (?, ?)",
                object.getName(), object.getOwner().getId());
        int newid = getFirstKey(rs);
        if (newid != -1) setUsed(newid);
        return newid;
    }

    @Override
    protected void update(Mailbox object) {
        setUsed(object.getId());
        DB.getSql().queryUpdate("UPDATE mailboxes SET `name` = ? WHERE id = ?", object.getName(), object.getId());
    }

    @Override
    public void delete(Mailbox object) {
        store.remove(object.getId());
        DB.getSql().queryUpdate("DELETE FROM mailboxes WHERE id = ?", object.getId());
    }

    private Mailbox mapResultSet(ResultSet rs) throws SQLException {
        Users users = Storages.get(Users.class);
        return new Mailbox(rs.getInt("id"), rs.getString("name"), users.get(rs.getInt("owner")));
    }
}
