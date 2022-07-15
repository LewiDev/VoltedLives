package me.lewi.volted;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public final class Lives extends JavaPlugin {

    private Database database;

    String myTableName = "CREATE TABLE IF NOT EXISTS `player_lives` (`UUID` VARCHAR(36), `L` INT(11))";


    @Override
    public void onEnable() {
        database = new Database(this);
        this.saveDefaultConfig();

        Bukkit.getLogger().log(Level.FINE, ChatColor.GREEN + "Volted Skills V1 by LewiDEV");

        try {
            database.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (database.isConnected()) {
            try {
                Statement statement = database.getConnection().createStatement();
                statement.executeUpdate(myTableName);
                System.out.println("CREATED TABLE");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        this.getServer().getPluginManager().registerEvents(new LivesListener(this), this);
        this.getCommand("lives").setExecutor(new LivesCommand(this));

    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

    public Database getDatabase() {
        return database;
    }
}
