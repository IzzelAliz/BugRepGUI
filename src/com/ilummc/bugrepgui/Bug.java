package com.ilummc.bugrepgui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Bug {
	/*
	 * SERIAL REPNAME REPTIME REPMSG EXENAME EXETIME EXEMSG EXECUTED BACK METHOD
	 */
	@SuppressWarnings("deprecation")
	OfflinePlayer player = Bukkit.getOfflinePlayer("Steve"), exeplayer = Bukkit.getOfflinePlayer("Steve");
	String repmsg = "", reptime = "", exetime = "", exemsg = "";
	Date date = new Date();
	Locale l;
	Integer serial = 0, method = 0;

	public Bug(Player player) {
		this.player = player;
		repmsg = "";
		date = new Date();
		l = new Locale(Storage.getConfig().getString("lang"), Storage.getConfig().getString("locale"));
		reptime = String.format(l, "%tc", date);
	}

	@SuppressWarnings("deprecation")
	public Bug(int serial, String reptime, String repname, String repmsg) {
		this.repmsg = repmsg;
		this.reptime = reptime;
		this.serial = serial;
		this.player = Bukkit.getOfflinePlayer(repname);

	}

	@SuppressWarnings("deprecation")
	public Bug(int serial, String reptime, String repname, String repmsg, String exetime, String exename, String exemsg,
			Integer method) {
		this.repmsg = repmsg;
		this.reptime = reptime;
		this.serial = serial;
		this.player = Bukkit.getOfflinePlayer(repname);
		this.exeplayer = Bukkit.getOfflinePlayer(exename);
		this.exetime = exetime;
		this.exemsg = exemsg;
		this.method = method;
	}

	public ItemStack toItem() {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta m = (SkullMeta) head.getItemMeta();
		m.setOwner(player.getName());
		m.setDisplayName(serial.toString());
		List<String> l = new ArrayList<>();
		l.add("¡ìb" + player.getName());
		l.add("¡ìe" + reptime + "¡ìf");
		String[] lorem = this.repmsg.split("#");
		for (int i = 0; i < lorem.length; i++) {
			l.add("¡ìe" + lorem[i]);
		}
		m.setLore(l);
		head.setItemMeta(m);
		return head;
	}

	public ItemStack toItemExecuted(){
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1 ,(short)3);
		SkullMeta m =(SkullMeta) head.getItemMeta();
		m.setOwner(player.getName());
		m.setDisplayName("¡ìa#"+serial.toString());
		List<String> l = new ArrayList<>();
		l.add("¡ìb"+player.getName());
		l.add("¡ìe"+reptime+"¡ìf");
		String[] lorem= this.repmsg.split("#");
		for(int i=0;i<lorem.length;i++){
			l.add("¡ìe"+lorem[i]);
		}
		String methodmsg = "";
		if(method == 1) methodmsg = Storage.getMsg("method-manualreply");
		if(method == 2) methodmsg = Storage.getMsg("method-autoreply");
		if(method == 3) methodmsg = Storage.getMsg("method-ignore");
		l.add("¡ì9================");
		l.add(exetime);
		l.add(Storage.getMsg("item-msg").replaceAll("%exename%", exeplayer.getName()).replaceAll("%method%", methodmsg));
		l.add(exemsg);
		m.setLore(l);
		head.setItemMeta(m);
		return head;
	}

	public void append(String msg) {
		if (repmsg.equals("")) {
			this.repmsg = msg;
			return;
		}
		this.repmsg = this.repmsg + "#" + msg;
		return;
	}

	public String getMsg() {
		return this.repmsg;
	}

	public String getName() {
		return this.player.getName();
	}

	public String getRepTime() {
		return this.reptime;
	}

	public String getExeTime() {
		return this.exetime;
	}
	public String getRepUUID(){
		return this.player.getUniqueId().toString();
	}
}