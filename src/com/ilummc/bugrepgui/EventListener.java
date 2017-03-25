package com.ilummc.bugrepgui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class EventListener implements org.bukkit.event.Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onChat(AsyncPlayerChatEvent evt) {
		String regex2 = "[^']+";
		String regex = Storage.getConfig().getString("stop-sign");
		String regex3 = Storage.transfer(regex);
		Pattern pattern2 = Pattern.compile(regex2);
		Matcher m2 = pattern2.matcher(evt.getMessage());
		if (Storage.map.containsKey(evt.getPlayer().getUniqueId().toString())) {
			evt.setCancelled(true);
			if (m2.matches() && (!evt.getMessage().matches(regex3))) {
				Bug bug = Storage.map.get(evt.getPlayer().getUniqueId().toString());
				bug.append(evt.getMessage());
				evt.getPlayer().sendMessage(Storage.getMsg("continue-input").replaceAll("%stopsign%", regex));
				return;
			} else if (!evt.getMessage().matches(regex3)) {
				Storage.send(evt.getPlayer(), "illegal-char");
				return;
			}
			if (evt.getMessage().matches(regex3)) {
				Bug bug = Storage.map.get(evt.getPlayer().getUniqueId().toString());
				Database.insert(bug);
				evt.getPlayer().sendMessage(Storage.getPrefix()
						+ Storage.getMsg("rep-suc").replaceAll("%serial%", Database.getSerial(evt.getPlayer())));
				if (!Storage.getConfig().getBoolean("use-bungee")) {
					Notify.notifyt(evt.getPlayer());
				}
				Storage.map.remove(evt.getPlayer().getUniqueId().toString());
				return;
			}
		}
		if (Storage.back.containsKey(evt.getPlayer().getUniqueId().toString())) {
			evt.setCancelled(true);
			Database.back(Storage.back.get(evt.getPlayer().getUniqueId().toString()), evt.getMessage(),
					evt.getPlayer());
			if (!Storage.getConfig().getBoolean("use-bungee")) {
				Notify.notifyb(Storage.back.get(evt.getPlayer().getUniqueId().toString()), evt.getMessage(),
						evt.getPlayer());
			}
			Storage.back.remove(evt.getPlayer().getUniqueId().toString());
			Storage.send(evt.getPlayer(), "send-back-success");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onMove(PlayerMoveEvent evt) {
		if (Storage.map.containsKey(evt.getPlayer().getUniqueId().toString())) {
			evt.setCancelled(true);
			evt.getPlayer().sendMessage(Storage.getMsg("not-complete").replaceAll("%stopsign%",
					Storage.getConfig().getString("stop-sign")));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onOffline(PlayerQuitEvent evt) {
		if (Storage.map.containsKey(evt.getPlayer().getUniqueId().toString())) {
			Storage.map.remove(evt.getPlayer().getUniqueId().toString());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onClick(InventoryClickEvent evt) {
		String regex = "[¡ì[0-9a-fA-f]]*\\u005b\\s[0-9]*\\s/\\s[0-9]*\\s\\u005d\\s*[\\d\\D]*";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(evt.getInventory().getTitle());
		String regex2 = "[¡ì[0-9a-fA-f]]*\\u0028\\s[0-9]*\\s/\\s[0-9]*\\s\\u0029\\s*[\\d\\D]*";
		Pattern pattern2 = Pattern.compile(regex2);
		Matcher m2 = pattern2.matcher(evt.getInventory().getTitle());
		if (m2.matches()) {
			evt.setCancelled(true);
			Inventory inv = evt.getInventory();
			Integer id = evt.getRawSlot();
			String arr = evt.getInventory().getTitle();
			String pagestr = "";
			for (int i = 4;; i++) {
				if (arr.charAt(i) >= '0' && arr.charAt(i) <= '9') {
					pagestr = pagestr + arr.charAt(i);
				} else {
					break;
				}
			}
			Integer page = Integer.parseInt(pagestr);
			GUI.clickHis(inv, page, id, evt.getClick(), (Player) evt.getWhoClicked());
		}
		if (m.matches()) {
			evt.setCancelled(true);
			Inventory inv = evt.getInventory();
			Integer id = evt.getRawSlot();
			String arr = evt.getInventory().getTitle();
			String pagestr = "";
			for (int i = 4;; i++) {
				if (arr.charAt(i) >= '0' && arr.charAt(i) <= '9') {
					pagestr = pagestr + arr.charAt(i);
				} else {
					break;
				}
			}
			Integer page = Integer.parseInt(pagestr);
			GUI.click(inv, page, id, evt.getClick(), (Player) evt.getWhoClicked());
			return;
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onJoin(PlayerJoinEvent evt) {
		Database.check(evt.getPlayer());
	}
}
