package de.lukweb.justmail.sql;

import java.io.File;
import java.sql.*;

public class SqlLite {

    private File file;
    private Connection connection;

    public SqlLite(String file) {
        this(new File(file));
    }

    public SqlLite(File file) {
        this.file = file;
        connect();
    }

    private boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) return true;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath().replaceAll("\\\\", "/"));
            return isConnected();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void queryUpdate(String query, Object... arguments) {
        queryUpdateWithKeys(query, arguments);
    }

    public ResultSet queryUpdateWithKeys(String query, Object... arguments) {
        try {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < arguments.length; i++) statement.setObject(i + 1, arguments[i]);
            statement.executeUpdate();
            return statement.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet querySelect(String query, Object... arguments) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < arguments.length; i++) statement.setObject(i + 1, arguments[i]);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
