package com.ilummc.bugrepgui;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Notify {

	public static void notifyt(Player player) {
		OfflinePlayer[] list = Bukkit.getOfflinePlayers();
		for (int i = 0; i < list.length; i++) {
			if (list[i].isOnline()) {
				if ((list[i]).getPlayer().hasPermission("bugrepgui.notify")) {
					(list[i]).getPlayer()
							.sendMessage(Storage.getPrefix()
									+ Storage.getMsg("notify-op").replaceAll("%player%", player.getName())
											.replaceAll("%serial%", Database.getSerial(player)));
				}
			}
		}
	}

	public static void notifyb(String serial, String msg, Player exename) {
		Player player = Database.getRepPlayer(serial);
		if (player.isOnline()) {
			String str = Database.format.replaceAll("%serial%", serial.toString())
					.replaceAll("%exename%", exename.getDisplayName()).replaceAll("%reply%", msg);
			String str2 = Database.format2.replaceAll("%serial%", serial.toString())
					.replaceAll("%exename%", exename.getDisplayName()).replaceAll("%reply%", msg);
			String msgs[] = { str, Storage.compile(str2) };
			player.sendMessage(msgs);
			Database.setback(serial);
		}
	}
}
