package me.lewi.volted;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LivesCommand implements CommandExecutor {

    private Lives main;

    public LivesCommand(Lives main) {
        this.main = main;
    }

    private int newlives;

    private UUID uuid;

    private ResultSet rs;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t != null) {
                this.uuid = t.getUniqueId();

                try {
                    PreparedStatement statement1 = main.getDatabase().getConnection().prepareStatement("SELECT L FROM player_lives WHERE UUID = ?");
                    statement1.setString(1, uuid.toString());
                    rs = statement1.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String s = main.getConfig().getString("livesplayer");
                s=s.replace("{player}", t.getName());
                try {
                    if(rs.next()) {
                        s=s.replace("{lives}", Integer.toString(rs.getInt("L")));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid Usage: /Lives <Player>");
            }
        } else if (args.length == 0) {
            if(!(sender instanceof Player)) {sender.sendMessage("CONSOLE CANNOT USE THIS COMMAND");}
            Player p = (Player) sender;
            this.uuid = p.getUniqueId();

            try {
                PreparedStatement statement1 = main.getDatabase().getConnection().prepareStatement("SELECT L FROM player_lives WHERE UUID = ?");
                statement1.setString(1, uuid.toString());
                rs = statement1.executeQuery();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            String s = main.getConfig().getString("livesmessage");
            s=s.replace("{player}", p.getName());
            try {
                if(rs.next()) {
                    s=s.replace("{lives}", Integer.toString(rs.getInt("L")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));

        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                if(sender.hasPermission("lives.give")) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
                    if (t != null ) {
                        int amount = Integer.parseInt(args[2]);
                        UUID uuid = t.getUniqueId();
                        try {
                            PreparedStatement statement1 = main.getDatabase().getConnection().prepareStatement("SELECT L FROM player_lives WHERE UUID = ?");
                            statement1.setString(1, uuid.toString());
                            rs = statement1.executeQuery();
                            if(rs.next()) {
                                newlives = rs.getInt("L");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }



                        try {
                            PreparedStatement statement2 = main.getDatabase().getConnection().prepareStatement("UPDATE player_lives SET L = " + (newlives + amount) + " WHERE UUID = '" + uuid.toString() + "';");
                            statement2.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        sender.sendMessage(ChatColor.GREEN + "Updated " + t.getName() + "'s lives to " + (amount + newlives));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid Usage: /lives give <Player> <amount>");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                }
            }
        }
        return false;
    }
}
