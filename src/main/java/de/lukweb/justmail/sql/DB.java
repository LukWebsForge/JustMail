package de.lukweb.justmail.sql;

import java.io.File;

public class DB {

    private static SqlLite sql;

    static {
        sql = new SqlLite(new File("database.sqlite3"));
        setupTables();
    }

    private static void setupTables() {
        sql.queryUpdate("CREATE TABLE IF NOT EXISTS \"mails\" ( " +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " `to` TEXT, " + // Reference to id @ users
                " `from` TEXT, " +
                " `sent` INTEGER, " + // Timestamp
                " `junkLevel` INTEGER, " + // Junk Level from 0 to 10
                " `imapDirectory` INTEGER, " +
                " `date` INTEGER, " + //
                " `content` BLOB " +
                ")");
        sql.queryUpdate("CREATE TABLE IF NOT EXISTS `domains` ( " +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " `domain` TEXT, " +
                " `enabled` INTEGER DEFAULT 1 " +
                ");");
        sql.queryUpdate("CREATE TABLE IF NOT EXISTS \"users\" ( " +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " `username` TEXT UNIQUE, " +
                " `domain` INTEGER, " + // Reference to id @ domains
                " `fullEmail` INTEGER, " +
                " `password` TEXT, " +
                " `base64up` BLOB, " +
                " `created` INTEGER " +
                ")");
    }

    public static SqlLite getSql() {
        return sql;
    }

}
