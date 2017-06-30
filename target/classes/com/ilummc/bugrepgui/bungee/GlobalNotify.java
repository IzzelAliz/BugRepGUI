package com.ilummc.bugrepgui.bungee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import com.ilummc.bugrepgui.Storage;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GlobalNotify {
	private static Connection c = null;
	private static Statement state = null;
	public static String format = Main.lang.getString("join-notify");
	public static String format2 = Main.lang.getString("join-notify2");

	public static void init() {
		Thread post = new Thread(new Runnable() {
			long spd = Main.config.getInt("check-speed");
			@Override
			public void run() {
				while (true) {
					Collection<ProxiedPlayer> l = Main.server.getPlayers();
					Iterator<ProxiedPlayer> it = l.iterator();
					while(it.hasNext()){
						ProxiedPlayer p = it.next();
						check(p.getName());
						try {
							Thread.sleep(spd);
						} catch (InterruptedException e) {
							Main.printException(e.getMessage());
						}
					}
				}
			}
		});
		post.start();
	}

	@SuppressWarnings("deprecation")
	public static void notifyt(ProxiedPlayer player) {
		Collection<ProxiedPlayer> l = Main.server.getPlayers();
		Iterator<ProxiedPlayer> it = l.iterator();
		while(it.hasNext()){
			ProxiedPlayer p = it.next();
			if(p.hasPermission("bugrepgui.notify")){
				p.sendMessage(Main.lang.getString("notify-op").replaceAll("%player%", player.getName()));
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void sends(String player, String[] msg){
		ProxiedPlayer p = Main.server.getPlayer(player);
		for (int i = 0; i < msg.length; i++) {
			if (msg[i] != null && msg[i] != "")
				p.sendMessage(getPrefix() + msg[i]);
		}
	}
	public static String getPrefix(){
		return Storage.compile(Main.lang.getString("prefix"));
	}
	public static void check(String player) {
		connect();
		String sql = "SELECT SERIAL,EXEMSG,EXENAME FROM br_bug WHERE REPNAME = '" + player
				+ "' AND (EXECUTED = 1 OR EXECUTED = 2) AND BACK = 0 ORDER BY SERIAL ASC;";
		String send[] = new String[12];
		Integer[] ser = new Integer[6];
		try {
			ResultSet res = state.executeQuery(sql);
			int limit = 0;
			while (((limit += 2) < 12) && res.next()) {
				Integer serial = res.getInt("SERIAL");
				String msg = res.getString("EXEMSG");
				String exename = res.getString("EXENAME");
				String str = format.replaceAll("%serial%", serial.toString()).replaceAll("%exename%", exename)
						.replaceAll("%reply%", msg);
				String str2 = format2.replaceAll("%serial%", serial.toString()).replaceAll("%exename%", exename)
						.replaceAll("%reply%", msg);
				send[limit - 2] = Storage.compile(str);
				send[limit - 1] = Storage.compile(str2);
				ser[limit / 2] = serial;
			}
			res.close();
		} catch (SQLException e) {
			Main.printException(e.getMessage());
		}
		sends(player, send);
		close();
		setback(ser);
	}

	public static void setback(Integer[] ser) {
		connect();
		for (int i = 1; i < (ser.length) && (ser[i] != null); i++) {
			String sql2 = "UPDATE br_bug SET BACK = 1 WHERE SERIAL = " + ser[i].toString() + ";";
			try {
				state.executeUpdate(sql2);
			} catch (SQLException e) {
				Main.printException(e.getMessage());
			}
		}
		close();
	}

	public static void setback(String ser) {
		connect();
		String sql = "UPDATE br_bug SET BACK = 1 WHERE SERIAL = " + ser + ";";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Main.printException(e.getMessage());
		}
		close();
	}

	public static void connect() {
			if (!conMySQL()) {
				Main.log.warning("[BugRepGUI] MySQL connect failed!");
				Main.log.warning("**************************************************");
				Main.log.warning("[BugRepGUI] Must check the MySQL server settings!!");
				Main.log.warning("[BugRepGUI] Must check the MySQL server settings!!");
				Main.log.warning("**************************************************");
			}
		
	}

	public static void close() {
		try {
			state.close();
			state = null;
			c.close();
		} catch (Exception e) {
			Main.printException(e.getMessage());
		}
	}
	public static boolean conMySQL() {
		String url = "jdbc:mysql://" + Main.config.getString("mysql-url") + ":"
				+ Main.config.getString("mysql-port") + "/" +Main.config.getString("mysql-db");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(url, Main.config.getString("mysql-username"),
					Main.config.getString("mysql-password"));
			state = c.createStatement();
		} catch (SQLException e) {
			Main.printException(e.getMessage());
			return false;
		} catch (ClassNotFoundException e) {
			Main.printException(e.getMessage());
			return false;
		}
		String sql = "CREATE TABLE IF NOT EXISTS br_bug " + "(SERIAL INT PRIMARY KEY  AUTO_INCREMENT   NOT NULL,"
				+ " REPNAME           TEXT    NOT NULL, " + " REPTIME           TEXT     NOT NULL, "
				+ " REPMSG        TEXT  NOT NULL, " + " EXENAME         TEXT, " + " EXETIME         TEXT, "
				+ " EXEMSG         TEXT, " + " EXECUTED         INT, " + " BACK         INT);";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Main.printException(e.getMessage());
		}
		return true;
	}
}
