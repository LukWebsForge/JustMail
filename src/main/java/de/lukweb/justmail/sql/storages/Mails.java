package de.lukweb.justmail.sql.storages;

import de.lukweb.justmail.sql.DB;
import de.lukweb.justmail.sql.DBStorage;
import de.lukweb.justmail.sql.objects.Mail;

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

    @Override
    protected int insert(Mail object) {
        ResultSet rs = DB.getSql().queryUpdateWithKeys("INSERT INTO mails (to, from, sent, junkLevel, imapDirectory, " +
                        "date, content) VALUES (?, ?, ?, ?, ?, ?, ?)",
                object.getTo(), object.getFrom(), toByte(object.isSent()), object.getJunkLevel(),
                object.getImapDirectory(), object.getDate(), object.getContent());
        int newid = getFirstKey(rs);
        if (newid != -1) setUsed(newid);
        return newid;
    }

    @Override
    protected void update(Mail object) {
        setUsed(object.getId());
        DB.getSql().queryUpdate("UPDATE mails SET sent = ?, junkLevel = ?, imapDirectory = ?, date =?, content = ? " +
                        "WHERE id = ?",
                toByte(object.isSent()), object.getJunkLevel(), object.getDate(), object.getContent(), object.getId());
    }

    @Override
    public void delete(Mail object) {
        store.remove(object.getId());
        DB.getSql().queryUpdate("DELETE FROM mails WHERE id = ?", object.getId());
    }

    private Mail mapResultSet(ResultSet rs) throws SQLException {
        return new Mail(rs.getInt("id"), rs.getString("from"), rs.getString("to"), toBool(rs.getByte("sent")),
                rs.getInt("junkLevel"), rs.getString("imapDirectory"), rs.getInt("date"), rs.getBytes("content"));

    }
}
