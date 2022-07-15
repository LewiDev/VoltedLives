package me.lewi.volted;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class LivesListener implements Listener {

    private Lives main;

    public LivesListener(Lives main) {
        this.main = main;
    }

    private UUID uuid;
    private int lives;
    private int newlives;

    private ResultSet rs;


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        this.uuid = p.getUniqueId();
        try {
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("SELECT L FROM player_lives WHERE UUID = ?");
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                lives = rs.getInt("L");
            } else {
                lives = main.getConfig().getInt("defaultlives");
                PreparedStatement statement1 = main.getDatabase().getConnection().prepareStatement("INSERT INTO player_lives (UUID, L) VALUES (" + "'" + uuid.toString() + "'," + lives + ");");
                statement1.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }



        String s = main.getConfig().getString("bannedmessage");
        s=s.replace("{player}", p.getName());
        s=s.replace("{amount}", Integer.toString(lives));
        s=ChatColor.translateAlternateColorCodes('&', s);

        if (lives == 0) {
            p.kickPlayer(s);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent eve) {
        Player p = eve.getEntity().getPlayer();
        this.uuid = p.getUniqueId();





        try {
            PreparedStatement statement1 = main.getDatabase().getConnection().prepareStatement("SELECT L FROM player_lives WHERE UUID = ?");
            statement1.setString(1, uuid.toString());
            rs = statement1.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }





        String s = main.getConfig().getString("bannedmessage");
        s=s.replace("{player}", p.getName());
        s=s.replace("{amount}", Integer.toString(lives));
        s=ChatColor.translateAlternateColorCodes('&', s);

        try {
            if(rs.next()) {
                newlives = rs.getInt("L") - 1;
            }
            PreparedStatement statement2 = main.getDatabase().getConnection().prepareStatement("UPDATE player_lives SET L = " + newlives + " WHERE UUID = '" + uuid.toString() + "';");
            statement2.executeUpdate();
            if(newlives == 0) {
                p.kickPlayer(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }










    }

}
