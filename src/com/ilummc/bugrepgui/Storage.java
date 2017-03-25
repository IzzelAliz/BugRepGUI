package com.ilummc.bugrepgui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Storage {
	private static FileConfiguration configyml;
	private static File folder;
	static String serial;
	static File lang;
	static boolean debug;
	static String format = "%serial%:\n  serial: '#%serial%'\n  name: '%player%'\n  time: '%time%'\n  file: '%path%'\n  executed: false\n";
	private static FileConfiguration langyml;
	// this is a temporary list for reporters
	public static Map<String, Bug> map = new HashMap<>();
	// this is a temporary list for ops who send back
	public static Map<String, String> back = new HashMap<>();

	public static void init(FileConfiguration config, File thisfolder, String language) {
		configyml = config;
		folder = thisfolder;
		lang = new File(thisfolder, language);
		langyml = YamlConfiguration.loadConfiguration(lang);
		debug = configyml.getBoolean("debug");
		Database.connect();
	}

	public static void reload() {
		init(configyml, folder, "lang_" + configyml.getString("lang", "en") + ".yml");
	}

	public static void putMap(Bug bug) {
		Storage.map.put(bug.getRepUUID(), bug);
	}

	public static File getFolder() {
		return folder;
	}

	public static FileConfiguration getConfig() {
		return configyml;
	}

	public static FileConfiguration getLang() {
		return langyml;
	}

	public static String compile(String str) {
		char[] cha = str.toCharArray();
		for (int i = 0; i < str.length(); i++) {
			if (cha[i] == '&') {
				if ((cha[i + 1] >= '0' && cha[i + 1] <= '9') || (cha[i + 1] >= 'a' && cha[i + 1] <= 'f')) {
					cha[i] = '¡ì';
				}
			}
		}
		return String.valueOf(cha);
	}

	public static String getMsg(String msg) {
		String str = langyml.getString(msg, "¡ìePlease check the lang_" + configyml.getString("lang", "xx") + ".yml!!!");
		return compile(str);
	}

	public static List<String> getMsgs(String msg) {
		List<String> list = langyml.getStringList(msg);
		for (int i = 0; i < list.size(); i++) {
			list.set(i, compile(list.get(i)));
		}
		return list;
	}

	public static String getPrefix() {
		return getMsg("prefix");
	}

	public static void send(Player player, String msg) {
		player.sendMessage(getPrefix() + getMsg(msg));
	}

	public static void send(CommandSender player, String msg) {
		player.sendMessage(getPrefix() + getMsg(msg));
	}

	public static void sends(Player player, String[] msg) {
		for (int i = 0; i < msg.length; i++) {
			if (msg[i] != null && msg[i] != "")
				player.sendMessage(getPrefix() + msg[i]);
		}
	}

	public static void log(String msg) {
		Bukkit.getLogger().info(
				getLang().getString(getPrefix().replaceAll("¡ì", ""), "[BugRepGUI]") + getMsg(msg).replaceAll("¡ì", ""));
	}

	public static void logExcept(Exception e) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BugRepGUI] Err occured: " + e.getMessage());
		if (debug) {
			e.printStackTrace();
		}
	}

	public static void getHelp(Player player) {
		List<String> helpl = langyml.getStringList("help");
		String[] help = new String[helpl.size()];
		for (int i = 0; i < helpl.size(); i++) {
			help[i] = compile(helpl.get(i));
		}
		player.sendMessage(help);
	}

	public static void getHelp(CommandSender player) {
		List<String> helpl = langyml.getStringList("help");
		String[] help = new String[helpl.size()];
		for (int i = 0; i < helpl.size(); i++) {
			help[i] = compile(helpl.get(i));
		}
		player.sendMessage(help);
	}

	public static String transfer(String string) {
		char[] ch = string.toCharArray();
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == ' ')
				out.append("\\s");
			else
				out.append(String.valueOf(ch[i]));
		}
		return out.toString();
	}

}
