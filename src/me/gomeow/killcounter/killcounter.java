package me.gomeow.killcounter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class killcounter extends JavaPlugin implements Listener{

	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("killcount")) {
			if(args.length == 0) {
					String totalkillcount = Integer.toString(this.getConfig().getInt("totalkillcount"));
					String totalentitykillcount = Integer.toString(this.getConfig().getInt("totalentitykillcount"));
					String totalplayerkillcount = Integer.toString(this.getConfig().getInt("totalplayerkillcount"));
					sender.sendMessage(ChatColor.DARK_RED + "This server has had " + ChatColor.GOLD + totalkillcount + ChatColor.DARK_RED + " kills total with " + ChatColor.GOLD + totalplayerkillcount + ChatColor.DARK_RED + " player kills and " + ChatColor.GOLD + totalentitykillcount + ChatColor.DARK_RED + " mob kills!");
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase(sender.getName())) {
						String target = args[0].toLowerCase();
						Integer specifickillcount = this.getConfig().getInt(target + ".totalkillcount");
						if(specifickillcount == null) {
							specifickillcount = 0;
						}
						Integer specificplayerkillcount = this.getConfig().getInt(target + ".totalplayerkillcount");
						if(specificplayerkillcount == null) {
							specificplayerkillcount = 0;
						}
						Integer specificentitykillcount = this.getConfig().getInt(target + ".totalentitykillcount");
						if(specificentitykillcount == null) {
							specificentitykillcount = 0;
						}
						sender.sendMessage(ChatColor.GOLD + target + ChatColor.DARK_RED + " has had " + ChatColor.GOLD + specifickillcount + ChatColor.DARK_RED + " kills total with " + ChatColor.GOLD + specificplayerkillcount + ChatColor.DARK_RED + " player kills and " + ChatColor.GOLD + specificentitykillcount + ChatColor.DARK_RED + " mob kills!");
				}
				else {
					
						String target = args[0].toLowerCase();
						Integer specifickillcount = this.getConfig().getInt(target + ".totalkillcount");
						if(specifickillcount == null) {
							specifickillcount = 0;
						}
						Integer specificplayerkillcount = this.getConfig().getInt(target + ".totalplayerkillcount");
						if(specificplayerkillcount == null) {
							specificplayerkillcount = 0;
						}
						Integer specificentitykillcount = this.getConfig().getInt(target + ".totalentitykillcount");
						if(specificentitykillcount == null) {
							specificentitykillcount = 0;
						}
						sender.sendMessage(ChatColor.GOLD + target + ChatColor.DARK_RED + " has had " + ChatColor.GOLD + specifickillcount + ChatColor.DARK_RED + " kills total with " + ChatColor.GOLD + specificplayerkillcount + ChatColor.DARK_RED + " player kills and " + ChatColor.GOLD + specificentitykillcount + ChatColor.DARK_RED + " mob kills!");
					
					
				}
			}
			if(args.length >= 2) {
				sender.sendMessage(ChatColor.RED + "Too many arguments!");
			}
		}
		if(label.equalsIgnoreCase("passivecount")) {
				if(this.getConfig().getBoolean("count-passive-mobs") == true) {
					this.getConfig().set("count-passive-mobs", false);
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This plugin will no longer count passive mobs in the killcount!");
				}
				else {
					this.getConfig().set("count-passive-mobs", true);
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This plugin will now count passive mobs in the killcount!");
				}
			
		}
		return false;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
			Entity killer = event.getEntity().getKiller();
			if(killer instanceof Player) {
				String killername = event.getEntity().getKiller().getName().toLowerCase();
				if(event.getEntity() instanceof Player) {
					
					int totalkillcount = this.getConfig().getInt("totalkillcount");
					totalkillcount++;
					this.getConfig().set("totalkillcount", totalkillcount);
					
					int totalplayerkillcount = this.getConfig().getInt("totalplayerkillcount");
					totalplayerkillcount++;
					this.getConfig().set("totalplayerkillcount", totalplayerkillcount);
					
					Integer specifickillcount = this.getConfig().getInt(killername + ".totalkillcount");
					if(specifickillcount == null) {
						this.getConfig().set(killername + ".totalkillcount", 1);
					}
					else {
						specifickillcount++;
						this.getConfig().set(killername + ".totalkillcount", specifickillcount);
					}
					
					Integer specificplayerkillcount = this.getConfig().getInt(killername + ".totalplayerkillcount");
					if(specificplayerkillcount == null) {
						this.getConfig().set(killername + ".totalplayerkillcount", 1);
					}
					else {
						specificplayerkillcount++;
						this.getConfig().set(killername + ".totalplayerkillcount", specificplayerkillcount);
					}
					saveConfig();
				}
				else {
					if(this.getConfig().getBoolean("count-passive-mobs") == false) {
						if((event.getEntity() instanceof Pig) 
								|| (event.getEntity() instanceof Sheep) 
								|| (event.getEntity() instanceof Chicken) 
								|| (event.getEntity() instanceof Cow) 
								|| (event.getEntity() instanceof Villager)
								|| (event.getEntity() instanceof Ocelot)
								|| (event.getEntity() instanceof Squid)
								|| (event.getEntity() instanceof MushroomCow)) {}
						else {
							int totalkillcount = this.getConfig().getInt("totalkillcount");
							totalkillcount++;
							this.getConfig().set("totalkillcount", totalkillcount);
							
							int totalentitykillcount = this.getConfig().getInt("totalentitykillcount");
							totalentitykillcount++;
							this.getConfig().set("totalentitykillcount", totalentitykillcount);
							
							Integer specifickillcount = this.getConfig().getInt(killername + ".totalkillcount");
							if(specifickillcount == null) {
								this.getConfig().set(killername + ".totalkillcount", 1);
							}
							else {
								specifickillcount++;
								this.getConfig().set(killername + ".totalkillcount", specifickillcount);
							}
							
							Integer specificentitykillcount = this.getConfig().getInt(killername + ".totalentitykillcount");
							if(specificentitykillcount == null) {
								this.getConfig().set(killername + ".totalentitykillcount", 1);
							}
							else {
								specificentitykillcount++;
								this.getConfig().set(killername + ".totalentitykillcount", specificentitykillcount);
							}
							saveConfig();
						}
					}
					else {
						int totalkillcount = this.getConfig().getInt("totalkillcount");
						totalkillcount++;
						this.getConfig().set("totalkillcount", totalkillcount);
						
						int totalentitykillcount = this.getConfig().getInt("totalentitykillcount");
						totalentitykillcount++;
						this.getConfig().set("totalentitykillcount", totalentitykillcount);
						
						Integer specifickillcount = this.getConfig().getInt(killername + ".totalkillcount");
						if(specifickillcount == null) {
							this.getConfig().set(killername + ".totalkillcount", 1);
						}
						else {
							specifickillcount++;
							this.getConfig().set(killername + ".totalkillcount", specifickillcount);
						}
						
						Integer specificentitykillcount = this.getConfig().getInt(killername + ".totalentitykillcount");
						if(specificentitykillcount == null) {
							this.getConfig().set(killername + ".totalentitykillcount", 1);
						}
						else {
							specificentitykillcount++;
							this.getConfig().set(killername + ".totalentitykillcount", specificentitykillcount);
						}
						saveConfig();
					}
				}
			}
		}
	}
}