package info.gomeow.killcounter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KillCounter extends JavaPlugin implements Listener {

    Connection connection = null;
    boolean usesql = true;
    String hostname = "";
    String port = "";
    String database = "";
    String user = "";
    String password = "";

    public Connection open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
            return connection;
        } catch(SQLException e) {
            System.out.println("Could not connect to MySQL server! because: " + e.getMessage());
        } catch(ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
        }
        return connection;
    }

    public boolean isEmpty(ResultSet res) {
        boolean empty = true;
        try {
            while(res.next()) {
                empty = false;
            }
            try {
                res.beforeFirst();
            } catch(SQLException e) {
                e.printStackTrace();
            }
            res.next();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return empty;
    }

    public ResultSet getPlayerInfo(String name) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet res = s.executeQuery("SELECT * FROM `Players` WHERE `Name` = '" + name + "' LIMIT 1");
        if(isEmpty(res)) {
            return null;
        }
        return res;
    }

    public void print(Object o) {
        System.out.println(o);
    }

    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        if(usesql = getConfig().getBoolean("sql.enable", false)) {
            getLogger().info("Starting KillCounter using MySQL.");
            print(this.hostname = getConfig().getString("sql.ip"));
            print(this.port = getConfig().getString("sql.port"));
            print(this.database = getConfig().getString("sql.database"));
            print(this.user = getConfig().getString("sql.username"));
            print(this.password = getConfig().getString("sql.password"));
            print(this.connection = open());
            try {
                DatabaseMetaData dm = this.connection.getMetaData();
                ResultSet tables = dm.getTables(null, null, "Global", null);
                if(!tables.next()) {
                    Statement s = this.connection.createStatement();
                    s.executeUpdate("CREATE TABLE `Global` (`Total` int, `Entities` int, `Players` int)");
                    s.executeUpdate("INSERT INTO `Global` (`Total`, `Entities`, `Players`) VALUES ('0', '0', '0')");
                }
                tables = dm.getTables(null, null, "Players", null);
                if(!tables.next()) {
                    Statement s = this.connection.createStatement();
                    s.executeUpdate("CREATE TABLE `Players` (`Name` varchar(32), `Total` int, `Players` int, `Entities` int)");
                }
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println(ChatColor.RED + "KillCounter has experienced a fatal error. Please check your SQL setup and/or config.yml");
                getLogger().severe("KillCounter has experienced a fatal error. Please check your SQL setup and/or config.yml");
                setEnabled(false);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity killerEnt = event.getEntity().getKiller();
        LivingEntity ent = event.getEntity();
        if((killerEnt instanceof Player)) {
            Player player = (Player) killerEnt;
            if(this.usesql) {
                try {
                    Statement s = this.connection.createStatement();
                    ResultSet res = s.executeQuery("SELECT * FROM `Global` WHERE 1 LIMIT 1");
                    res.next();
                    int total = res.getInt("Total");
                    int players = res.getInt("Players");
                    int entities = res.getInt("Entities");
                    String sqlUpdate = "UPDATE `Global` SET `Total` = " + String.valueOf(total + 1) + ", `Players` = " + String.valueOf((ent instanceof Player) ? players + 1 : players) + ", `Entities` = " + String.valueOf((ent instanceof Player) ? entities : entities + 1) + " WHERE 1";
                    s.executeUpdate(sqlUpdate);

                    String killer = player.getName();
                    ResultSet info = getPlayerInfo(killer);
                    if(info == null) {
                        String sql = "INSERT INTO `Players`(`Name`, `Total`, `Entities`, `Players`) VALUES ('" + killer + "', 1, " + ((ent instanceof Player) ? 0 : 1) + "," + ((ent instanceof Player) ? 1 : 0) + ")";
                        s.executeUpdate(sql);
                        return;
                    }
                    total = info.getInt("Total") + 1;
                    entities = info.getInt("Entities");
                    players = info.getInt("Players");
                    String sql = "UPDATE `Players` SET `Total` = " + String.valueOf(total) + ", `Players` = " + ((ent instanceof Player) ? players + 1 : players) + ", `Entities` = " + ((ent instanceof Player) ? entities : entities + 1) + " WHERE `Name` = '" + killer + "'";
                    s.executeUpdate(sql);
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            } else {
                String killer = player.getName();
                getConfig().set("totalkillcount", Integer.valueOf(getConfig().getInt("totalkillcount", 0) + 1));
                getConfig().set(killer + ".totalkillcount", Integer.valueOf(getConfig().getInt(killer + ".totalkillcount", 0) + 1));
                if((ent instanceof Player)) {
                    getConfig().set("totalplayerkillcount", Integer.valueOf(getConfig().getInt("totalplayerkillcount", 0) + 1));
                    getConfig().set(killer + ".totalplayerkillcount", Integer.valueOf(getConfig().getInt(killer + ".totalplayerkillcount", 0) + 1));
                }
                saveConfig();
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("killcount")) {
            ChatColor main = ChatColor.GOLD;
            ChatColor other = ChatColor.DARK_RED;
            if(args.length == 0) {
                if(usesql) {
                    try {
                        Statement s = this.connection.createStatement();
                        ResultSet res = s.executeQuery("SELECT * FROM `Global` WHERE 1 LIMIT 1");
                        res.next();
                        int totalkillcount = res.getInt("Total");
                        int totalplayerkillcount = res.getInt("Players");
                        int totalentitykillcount = res.getInt("Entities");
                        sender.sendMessage(main + "This server has had " + other + totalkillcount + main + " kills total with " + other + totalplayerkillcount + main + " player kills and " + other + totalentitykillcount + main + " mob kills!");
                    } catch(SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    String totalkillcount = Integer.toString(getConfig().getInt("totalkillcount", 0));
                    String totalentitykillcount = Integer.toString(getConfig().getInt("totalentitykillcount"));
                    String totalplayerkillcount = Integer.toString(getConfig().getInt("totalplayerkillcount"));
                    sender.sendMessage(main + "This server has had " + other + totalkillcount + main + " kills total with " + other + totalplayerkillcount + main + " player kills and " + other + totalentitykillcount + main + " mob kills!");
                }
            } else if(args.length == 1) {
                String target = args[0].toLowerCase();
                if(usesql) {
                    try {
                        ResultSet info = getPlayerInfo(target);
                        if(info == null) {
                            sender.sendMessage(other + target + main + " has had " + other + "0" + main + " kills total with " + other + "0" + main + " player kills and " + other + "0" + main + " mob kills!");
                            return true;
                        }
                        int specifickillcount = info.getInt("Total") + 1;
                        int specificentitykillcount = info.getInt("Entities");
                        int specificplayerkillcount = info.getInt("Players");
                        sender.sendMessage(other + target + main + " has had " + other + specifickillcount + main + " kills total with " + other + specificplayerkillcount + main + " player kills and " + other + specificentitykillcount + main + " mob kills!");
                    } catch(SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Integer specifickillcount = Integer.valueOf(getConfig().getInt(target + ".totalkillcount", 0));
                    Integer specificplayerkillcount = Integer.valueOf(getConfig().getInt(target + ".totalplayerkillcount", 0));
                    Integer specificentitykillcount = Integer.valueOf(getConfig().getInt(target + ".totalentitykillcount", 0));
                    sender.sendMessage(other + target + main + " has had " + other + specifickillcount + main + " kills total with " + other + specificplayerkillcount + main + " player kills and " + other + specificentitykillcount + main + " mob kills!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Too many arguments!");
            }
        }
        return true;
    }
}