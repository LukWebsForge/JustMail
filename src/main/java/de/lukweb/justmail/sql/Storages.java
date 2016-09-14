package de.lukweb.justmail.sql;

import de.lukweb.justmail.sql.storages.Domains;
import de.lukweb.justmail.sql.storages.Mails;
import de.lukweb.justmail.sql.storages.Users;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Storages {

    private static HashMap<Class<?>, DBStorage> storages = new HashMap<>();
    private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

    static {
        new Domains();
        new Mails();
        new Users();
    }

    static void register(Class<?> clazz, DBStorage storage) {
        storages.put(clazz, storage);
    }

    static ScheduledExecutorService getPool() {
        return pool;
    }

    @SuppressWarnings("unchecked")
    public static <T extends DBStorage> T get(Class<T> clazz) {
        return (T) storages.get(clazz);
    }

}
