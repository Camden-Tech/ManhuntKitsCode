package me.BaddCamden.ManhuntKits;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



public class Main extends JavaPlugin{
	public File datafile;
	public FileConfiguration database;
	me.phoenix.manhuntplus.Main MP;
		@Override
		public void onEnable() {
			datafile = new File(this.getDataFolder()+"/database.yml");
			database = YamlConfiguration.loadConfiguration(datafile);
			PluginManager pm = getServer().getPluginManager();
			MP = (me.phoenix.manhuntplus.Main) pm.getPlugin("ManhuntPlus");
			if (!datafile.exists()) {
				datafile.getParentFile().mkdirs();
	        try {
	                datafile.createNewFile();
	        } catch (IOException ex) {
	                ex.printStackTrace();
	        }
	        	loadDatabase();
		    }
			
			RegisterEvents listener = new RegisterEvents(this);
			pm.registerEvents(listener, this );
			this.getCommand("mkstart").setExecutor(listener);
			this.getCommand("mkstop").setExecutor(listener);
			this.getCommand("mkkits").setExecutor(listener);


	    }
		@Override
		public void onDisable() {
			saveDataBase();
		}

	    public void loadDatabase() {
	        try {
	            database.load(datafile);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
		
		public FileConfiguration getDataBase() {
			return database;
		}
		
		public File getDataFile() {
			return datafile;
		}
		
		 public void saveDataBase() {
			 try {
				 database.save(datafile);
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		public me.phoenix.manhuntplus.Main getMP(){
			return MP;
		}
}
