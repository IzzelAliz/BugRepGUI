package com.ilummc.bugrepgui;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.*;

import com.ilummc.bugrepgui.util.UpdateChecker;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		String lang = this.getConfig().getString("lang");
		String alias = this.getConfig().getString("alias");
		this.saveResource("lang_" + lang + ".yml", false);
		Storage.init(this.getConfig(), this.getDataFolder(), "lang_" + lang + ".yml");
		getServer().getPluginManager().registerEvents(new EventListener().setAlias(getConfig().getString("alias")),
				this);
		getCommand("bug").setExecutor(new cmdExe());
		getCommand("bugrepgui").setExecutor(new cmdExe());
		this.getCommand(alias).setExecutor(new cmdExe());
		if (this.getConfig().getBoolean("check-update"))
			UpdateChecker.check(this.getDescription().getVersion(), this.getDescription().getWebsite());
	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().info("[BugRepGUI] Thanks for chosing this plugin!");
	}
}
