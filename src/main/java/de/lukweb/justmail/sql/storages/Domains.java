package de.lukweb.justmail.sql.storages;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.sql.DB;
import de.lukweb.justmail.sql.DBStorage;
import de.lukweb.justmail.sql.Storages;
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
        String host = JustMail.getInstance().getConfig().getHost();
        if (get(host) == null) {
            insert(new Domain(-1, host, true));
            JustLogger.logger().info("Created a domin for the host domain.");
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

    public Domain get(String domainName) {
        for (Domain domain : store.values()) if (domain.getDomain().equalsIgnoreCase(domainName)) return domain;
        return null;
    }

    public boolean isAllowed(String domainStr) {
        for (Domain domain : store.values()) if (domain.getDomain().equalsIgnoreCase(domainStr)) return true;
        return false;
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

        Users users = Storages.get(Users.class);
        users.getAll().stream()
                .filter(user -> user.getDomain().equals(object))
                .forEach(users::removeFromCache);

        store.remove(object.getId());
        DB.getSql().queryUpdate("DELETE FROM domains WHERE id = ?", object.getId());
    }
}
