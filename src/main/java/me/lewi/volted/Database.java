package me.lewi.volted;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private Lives plugin;

    public Database(Lives plugin) {
        this.plugin = plugin;
    }

    private Connection connection;

    public void connect() throws SQLException {
        final String HOST = plugin.getConfig().getString("HOST");
        final int PORT = plugin.getConfig().getInt("PORT");
        final String DATABASE = plugin.getConfig().getString("DATABASE");
        final String USERNAME = plugin.getConfig().getString("USERNAME");
        final String PASSWORD = plugin.getConfig().getString("PASSWORD");

        connection = DriverManager.getConnection(
                "jdbc:mysql://"+HOST+":"+PORT+"/"+DATABASE + "?allowPublicKeyRetrieval=true&useSSL=false",
                USERNAME,
                PASSWORD);
    }

    public boolean isConnected() { return connection != null; }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
