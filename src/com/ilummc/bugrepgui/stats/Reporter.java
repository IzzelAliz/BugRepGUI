package com.ilummc.bugrepgui.stats;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Reporter implements Comparable<Reporter> {
	OfflinePlayer player;
	Integer amount = 0;

	@SuppressWarnings("deprecation")
	public Reporter(String player, Integer amount) {
		this.player = Bukkit.getOfflinePlayer(player);
		this.amount = amount;
	}

	public Integer getAmount() {
		return this.amount;
	}
	public String getName(){
		return this.player.getName();
	}

	@Override
	public int compareTo(Reporter other) {
		return this.amount - other.getAmount();
	}

}
