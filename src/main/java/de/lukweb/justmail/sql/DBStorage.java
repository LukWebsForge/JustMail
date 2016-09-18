package de.lukweb.justmail.sql;

import de.lukweb.justmail.sql.interfaces.Unquie;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class DBStorage<T extends Unquie> {

    private Class clazz;
    private boolean keepAllObjects;
    protected HashMap<Integer, T> store = new HashMap<>();
    private HashMap<Integer, Integer> garbageCollector = new HashMap<>(); // ID, Timestamp

    protected DBStorage(Class clazz, boolean keepAllObjects) {
        this.clazz = clazz;
        this.keepAllObjects = keepAllObjects;
        Storages.register(getClass(), this);
        if (!keepAllObjects) Storages.getPool().scheduleAtFixedRate(() -> {
            if (store.size() < 100) return;
            garbageCollector.forEach((id, time) -> {
                if (time + 5 * 60 > System.currentTimeMillis() / 1000) return;
                store.remove(id);
            });
            // todo gc
        }, 30, 30, TimeUnit.SECONDS);
    }

    protected Class getClazz() {
        return clazz;
    }

    protected void setUsed(int id) {
        if (!keepAllObjects) garbageCollector.put(id, (int) (System.currentTimeMillis() / 1000));
    }

    ////>

    public void removeFromCache(T object) {
        if (object == null) return;
        store.remove(object.getId());
    }

    ////>

    public abstract List<T> getAll();

    public abstract T get(int id);

    public void save(T object) {
        if (object.getId() == -1) {
            int newid = insert(object);
            if (newid == -1) return;
            object.setId(newid);
            store.put(newid, object);
            if (!keepAllObjects) garbageCollector.put(newid, (int) (System.currentTimeMillis() / 1000));
        } else update(object);
    }

    protected abstract int insert(T object);

    protected abstract void update(T object);

    public abstract void delete(T object);

    ////>

    protected byte toByte(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    protected boolean toBool(byte byt) {
        return byt == 1;
    }

    protected int getFirstKey(ResultSet rs) {
        try {
            if (!rs.next()) return -1;
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
