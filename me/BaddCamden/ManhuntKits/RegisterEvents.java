package me.BaddCamden.ManhuntKits;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class RegisterEvents implements Listener, CommandExecutor, TabCompleter{
	Main mainPlugin;
	public FileConfiguration database;
	public File datafile;
	public boolean hasStarted = false;
	
	public RegisterEvents(Main main) {
		mainPlugin = main;
		database = main.getDataBase();
		datafile = main.getDataFile();
        boolean foundW = false;
		for(String key : database.getKeys(false)) {
			
			if(key.equalsIgnoreCase("Game")) {
				foundW = true;
			}
		}
		if(!foundW) {
			database.createSection("Game");
			database.set("Game.started", false);
			database.set("Game.time", 0);
			database.set("Game.kits", 1);
			mainPlugin.saveDataBase();
		}
		boolean foundK = false;
		for(String key : database.getKeys(false)) {
			
			if(key.equalsIgnoreCase("Kits")) {
				foundK = true;
			}
		}
		if(!foundK) {
			database.createSection("Kits");
			mainPlugin.saveDataBase();
		}
		Timer();
	}
	
	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event) {
		for(String s : database.getConfigurationSection("Kits").getKeys(false)) {
			if(event.getItemInHand().getItemMeta().getLocalizedName().equals("kit"+s) && event.getBlock().getState() instanceof Chest) {
				Chest chest = (Chest)event.getBlock().getState();
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				List l = (database.getList("Kits."+s+".items"));
				items.addAll(l);
				for(ItemStack i : items) {
					chest.getBlockInventory().addItem(i);
				}
				break;
			}
		}
	}
	
	
	public void Timer() {
		BukkitScheduler scheduler = mainPlugin.getServer().getScheduler();
	    scheduler.scheduleSyncDelayedTask(mainPlugin, new Runnable() {
		    public void run() {
		    	if(database.getBoolean("Game.started")) {
		    		
		    		if(database.getInt("Game.time") < 1){
		    			 
						for(String h : mainPlugin.getMP().speedRunners) {
							for(Player p : Bukkit.getOnlinePlayers()) {
								if(Bukkit.getPlayer(h).equals(p)) {
									int highestPoint = 0;
									for(String s : database.getConfigurationSection("Kits").getKeys(false)) {
										if(highestPoint < Integer.parseInt(s)) highestPoint = Integer.parseInt(s);
									}
									while(database.getConfigurationSection("Kits."+database.getInt("Game.kits")) == null && highestPoint > database.getInt("Game.kits")) {
										database.set("Game.kits", database.getInt("Game.kits") + 1);
									}
									ItemStack chest = new ItemStack(Material.CHEST);
									
									//chest.(((ItemStack[])database.get("Kits."+database.getInt("Game.kits")+".items"));
									ItemMeta meta = chest.getItemMeta();
									meta.setLocalizedName("kit"+database.getInt("Game.kits"));
									chest.setItemMeta(meta);
									p.getInventory().addItem(chest);
									if(highestPoint > database.getInt("Game.kits")) {
										database.set("Game.kits", database.getInt("Game.kits") + 1);
									}
									
									database.set("Game.time", 600);
									
								}
							}
						}
		    		} else {
		    			database.set("Game.time", database.getInt("Game.time") - 1);
		    		}
		    		mainPlugin.saveDataBase();
		    		
		    		
		    	}
		    	Timer();	
		    }
	    },20);
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(arg0.isOp()) {
			if(arg2.equals("mkstart")) {
				arg0.sendMessage(ChatColor.AQUA+"Manhunt Kits should now start whenever you start the game!");
				hasStarted = true;
				database.set("Game.started", true);
				database.set("Game.time", 600);
				database.set("Game.kits", 1);
				mainPlugin.saveDataBase();

				
			} else  if(arg2.equals("mkstop")) {
				arg0.sendMessage(ChatColor.AQUA+"Manhunt Kits is now stopped!");
				hasStarted = false;
				database.set("Game.started", false);
				database.set("Game.kits", 1);
				mainPlugin.saveDataBase();
			} else  if(arg2.equals("mkkits")) {
				if(arg3[0].equals("set")) {
					if(Integer.parseInt(arg3[1]) != 0) {
						Location loc = ((Player)arg0).getLocation();
						loc.setY(loc.getY() - 0.75);

						Block b = loc.getWorld().getBlockAt(loc);
						if(b.getType().equals(Material.CHEST)) {
							Chest c = (Chest)b.getState();
							database.set("Kits."+arg3[1]+".items", c.getBlockInventory().getStorageContents());
							mainPlugin.saveDataBase();
						} else {
							arg0.sendMessage(ChatColor.DARK_RED+"You must be standing ontop of a chest!");
						}
					} else {
						arg0.sendMessage(ChatColor.DARK_RED+"You must put an integer number!");
					}

				}
			}
		}

		return false;
	}
	
	
	@Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> list = new ArrayList<String>();
        List<String> results = new ArrayList<String>();
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("mkkits")) {
                if (args.length == 0) {
                    list.add("set");
                    Collections.sort(list);
                    return list;
                } else if (args.length == 1) {
                	list.add("set");
                    for (String s : list){
                        if (s.toLowerCase().startsWith(args[0].toLowerCase())){
                        	results.add(s);
                        }
                    }
                    Collections.sort(results);
                    return results;
                }
                	
                	
                
                
            }
        }
        return list;
    }
	
}
