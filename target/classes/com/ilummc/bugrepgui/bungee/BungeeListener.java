package com.ilummc.bugrepgui.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeListener implements Listener {
	@EventHandler
	public void onChat(ChatEvent evt) {
		if (!(evt.getSender() instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer p = (ProxiedPlayer) evt.getSender();
		if (Main.config.getString("stop-sign").equals(evt.getMessage())) {
			GlobalNotify.notifyt(p);
		}
	}
}
