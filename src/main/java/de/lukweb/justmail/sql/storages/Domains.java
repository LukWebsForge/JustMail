package de.lukweb.justmail.sql.storages;

import de.lukweb.justmail.sql.DB;
import de.lukweb.justmail.sql.DBStorage;
import de.lukweb.justmail.sql.objects.Domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Domains extends DBStorage<Domain> {

    public Domains() {
        super(Domain.class, true);
        loadAll();
    }

    private void loadAll() {
        store.clear();
        try {
            ResultSet rs = DB.getSql().querySelect("SELECT * FROM domains");
            while (rs.next()) store.put(rs.getInt("id"), new Domain(rs.getInt("id"), rs.getString("domain"),
                    toBool(rs.getByte("enabled"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Domain> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Domain get(int id) {
        return store.get(id);
    }

    @Override
    protected int insert(Domain object) {
        ResultSet rs = DB.getSql().queryUpdateWithKeys("INSERT INTO domains (domain, enabled) VALUES (?, ?)",
                object.getDomain(), toByte(object.isEnabled()));
        return getFirstKey(rs);
    }

    @Override
    protected void update(Domain object) {
        DB.getSql().queryUpdate("UPDATE domains enabled = ? WHERE id = ?", toByte(object.isEnabled()), object.getId());
    }

    @Override
    public void delete(Domain object) {
        if (object.getId() == -1) return;
        store.remove(object.getId());
        DB.getSql().queryUpdate("DELETE FROM domains WHERE id = ?", object.getId());
    }
}
