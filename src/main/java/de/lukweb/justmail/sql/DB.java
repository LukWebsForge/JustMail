package de.lukweb.justmail.sql;

import de.lukweb.justmail.console.JustLogger;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB {

    private static SqlLite sql;
    private static int latestDbVersion = 1;
    private static int dbVersion;

    static {
        sql = new SqlLite(new File("database.sqlite3"));
        setupTables();
        readVersion();
    }

    private static void setupTables() {
        sql.queryUpdate("CREATE TABLE IF NOT EXISTS \"mails\" ( " +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " `owner` TEXT, " + // The mail adress of the user the mail belongs to
                " `to` TEXT, " +
                " `from` TEXT, " +
                " `sent` INTEGER, " + // Boolean indicating whether the server received or sent this mail
                " `junkLevel` INTEGER, " + // Junk Level from 0 to 10
                " `imapDirectory` INTEGER, " +
                " `date` INTEGER, " + // Timestamp
                " `content` BLOB, " +
                " `read` INTEGER " +
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
        sql.queryUpdate("CREATE TABLE IF NOT EXISTS `version` (" +
                " `db_version` INTEGER PRIMARY KEY " +
                ")");
    }

    private static void readVersion() {
        try {
            ResultSet rs = sql.querySelect("SELECT * FROM version");
            if (rs.next()) {
                dbVersion = rs.getInt("db_version");
                if (dbVersion < latestDbVersion) {
                    // todo migrations
                }
                return;
            }
            sql.queryUpdate("INSERT INTO version (db_version) VALUES (?)", latestDbVersion);
        } catch (SQLException e) {
            JustLogger.logger().warning("Cannot read database version: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        dbVersion = latestDbVersion;
    }

    public static SqlLite getSql() {
        return sql;
    }

}
