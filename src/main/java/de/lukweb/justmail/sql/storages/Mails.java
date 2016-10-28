package de.lukweb.justmail.sql.storages;

import de.lukweb.justmail.sql.DB;
import de.lukweb.justmail.sql.DBStorage;
import de.lukweb.justmail.sql.objects.Mail;
import de.lukweb.justmail.sql.objects.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mails extends DBStorage<Mail> {

    public Mails() {
        super(Mail.class, false);
    }

    @Override
    public List<Mail> getAll() {
        ArrayList<Mail> all = new ArrayList<>();
        try {
            ResultSet rs = DB.getSql().querySelect("SELECT * FROM mails");
            while (rs.next()) all.add(mapResultSet(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public Mail get(int id) {
        if (store.containsKey(id)) {
            setUsed(id);
            return store.get(id);
        }
        try {
            ResultSet rs = DB.getSql().querySelect("SELECT * FROM mails WHERE id = ?", id);
            if (!rs.first()) return null;
            Mail mail = mapResultSet(rs);
            store.put(rs.getInt("id"), mail);
            setUsed(id);
            return mail;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Mail> getAll(User user) {
        ArrayList<Mail> all = new ArrayList<>();
        ResultSet rs = DB.getSql().querySelect("SELECT id FROM mails WHERE owner = ?", user.getFullEmail());
        try {
            while (rs.next()) all.add(get(rs.getInt("id")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return all;
    }

    public int getMaxUID() {
        ResultSet rs = DB.getSql().querySelect("SELECT MAX(id) as `max` FROM mails");
        try {
            if (!rs.first()) return 0;
            return rs.getInt("max");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected int insert(Mail object) {
        ResultSet rs = DB.getSql().queryUpdateWithKeys("INSERT INTO mails (owner, `to`, `from`, sent, junkLevel, " +
                        "imapDirectory, `date`, content, `read`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                object.getOwner(), object.getTo(), object.getFrom(), toByte(object.isSent()), object.getJunkLevel(),
                object.getImapDirectory(), object.getDate(), object.getContent(), toByte(object.isRead()));
        int newid = getFirstKey(rs);
        if (newid != -1) setUsed(newid);
        return newid;
    }

    @Override
    protected void update(Mail object) {
        setUsed(object.getId());
        DB.getSql().queryUpdate("UPDATE mails SET sent = ?, junkLevel = ?, imapDirectory = ?, `date` =?," +
                        " content = ?, `read` = ? WHERE id = ?",
                toByte(object.isSent()), object.getJunkLevel(), object.getDate(), object.getContent(),
                toByte(object.isRead()), object.getId());
    }

    @Override
    public void delete(Mail object) {
        store.remove(object.getId());
        DB.getSql().queryUpdate("DELETE FROM mails WHERE id = ?", object.getId());
    }

    private Mail mapResultSet(ResultSet rs) throws SQLException {
        return new Mail(rs.getInt("id"), rs.getString("owner"), rs.getString("from"), rs.getString("to"),
                toBool(rs.getByte("sent")), rs.getInt("junkLevel"), rs.getString("imapDirectory"), rs.getInt("date"),
                rs.getBytes("content"), toBool(rs.getByte("read")));
    }


}
