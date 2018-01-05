package com.ilummc.bugrepgui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class Database {
	/*
	 * SERIAL REPNAME REPTIME REPMSG EXENAME EXETIME EXEMSG EXECUTED BACK
	 * EXECUTED 1:MANUALREPLY 2:AUTOREPLY 3:IGNORE
	 */
	private static String dbtype = Storage.getConfig().getString("database", "sqlite");
	private static Connection c = null;
	private static Statement state = null;
	private static String autoback = Storage.getConfig().getString("auto-sendback-msg");
	public static String format = Storage.getMsg("join-notify");
	public static String format2 = Storage.getMsg("join-notify2");

	public static void test(){
		connect();
		String sql = "INSERT INTO br_bug (REPNAME, REPTIME, REPMSG, EXENAME, EXETIME, EXEMSG, EXECUTED, BACK)"
				+ " VALUES ('a','b','c','d','g','h',1,1);";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
	}
	public static Map<String, Integer> loadStatsRep() {
		connect();
		String sql = "SELECT REPNAME FROM br_bug;";
		Map<String, Integer> reprank = new TreeMap<>();
		Integer repmax = 1;
		try {
			ResultSet res = state.executeQuery(sql);
			while (res.next()) {
				String repname = res.getString("REPNAME");
				if (reprank.containsKey(repname)) {
					reprank.replace(repname, reprank.get(repname) + 1);
					if (repmax < reprank.get(repname))
						repmax++;
				} else {
					reprank.put(repname, 1);
				}
			}
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		return reprank;
	}

	public static Map<String, Integer> loadStatsExe() {
		connect();
		String sql = "SELECT EXENAME,EXECUTED FROM br_bug;";
		Map<String, Integer> exerank = new TreeMap<>();
		Integer exemax = 1;
		try {
			ResultSet res = state.executeQuery(sql);
			while (res.next()) {
				String exename = res.getString("EXENAME");
				if (exerank.containsKey(exename)) {
					exerank.replace(exename, exerank.get(exename) + 1);
					if (exemax < exerank.get(exename))
						exemax++;
				} else {
					exerank.put(exename, 1);
				}
			}
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		return exerank;
	}

	@SuppressWarnings("deprecation")
	public static Player getRepPlayer(String serial) {
		connect();
		String sql = "SELECT REPNAME FROM br_bug WHERE SERIAL =" + serial + ";";
		String name = "Steve";
		try {
			ResultSet res = state.executeQuery(sql);
			if (res.next()) {
				name = res.getString("REPNAME");
				res.close();
				res = null;
			}
		} catch (SQLException e) {
			Storage.logExcept(e);
		} finally {
			close();
		}
		return Bukkit.getPlayer(name);
	}

	public static void check(Player player) {
		connect();
		String sql = "SELECT SERIAL,EXEMSG,EXENAME FROM br_bug WHERE REPNAME = '" + player.getName()
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
			Storage.logExcept(e);
		}
		Storage.sends(player, send);
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
				Storage.logExcept(e);
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
			Storage.logExcept(e);
		}
		close();
	}

	public static void qback(String serial, Player player) {
		// auto reply method
		connect();
		Date date = new Date();
		String exetime = String.format("%tc", date);
		String sql = "UPDATE br_bug SET EXETIME = '" + exetime + "', EXENAME = '" + player.getName()
				+ "' ,EXECUTED = 2, EXEMSG = '" + autoback + "' WHERE SERIAL = " + serial + ";";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		close();
	}

	public static void back(String serial, String msg, Player player) {
		// manual reply method
		connect();
		Date date = new Date();
		String exetime = String.format("%tc", date);
		String sql = "UPDATE br_bug SET EXETIME = '" + exetime + "', EXENAME = '" + player.getName()
				+ "', EXECUTED = 1, EXEMSG = '" + msg + "' WHERE SERIAL = " + serial + ";";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		close();
	}

	public static List<Bug> list() {
		connect();
		List<Bug> set = new ArrayList<>();
		String sql = "SELECT SERIAL,REPTIME,REPNAME,REPMSG FROM br_bug WHERE EXECUTED = 0;";
		try {
			ResultSet res = state.executeQuery(sql);
			while (res.next()) {
				Bug bug = new Bug(res.getInt("SERIAL"), res.getString("REPTIME"), res.getString("REPNAME"),
						res.getString("REPMSG"));
				set.add(bug);
			}
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		close();
		return set;
	}

	public static List<Bug> listHis() {
		connect();
		List<Bug> set = new ArrayList<>();
		String sql = "SELECT SERIAL,REPTIME,REPNAME,REPMSG,EXENAME,EXETIME,EXEMSG,EXECUTED FROM br_bug WHERE BACK = 1";
		try {
			ResultSet res = state.executeQuery(sql);
			while (res.next()) {
				Bug bug = new Bug(res.getInt("SERIAL"), res.getString("REPTIME"), res.getString("REPNAME"),
						res.getString("REPMSG"), res.getString("EXETIME"), res.getString("EXENAME"),
						res.getString("EXEMSG"), res.getInt("EXECUTED"));
				set.add(bug);
			}
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		close();
		return set;
	}

	public static void insert(Bug bug) {
		connect();
		String sql = "INSERT INTO br_bug (REPTIME,REPNAME,REPMSG,EXECUTED,BACK)" + " VALUES ('" + bug.getRepTime()
				+ "','" + bug.getName() + "','" + bug.getMsg() + "',0,0);";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		close();
	}

	public static void ignore(String serial, Player player) {
		connect();
		Date date = new Date();
		String exetime = String.format("tc", date);
		String sql = "UPDATE br_bug SET EXETIME = '" + exetime + "', EXENAME = '" + player.getName()
				+ "', EXECUTED = 3 WHERE SERIAL = " + serial + ";";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		close();
	}

	public static String getSerial(Player player) {
		connect();
		String sql = "SELECT SERIAL FROM br_bug WHERE REPNAME = '" + player.getName() + "' ORDER BY SERIAL DESC;";
		Integer i = 0;
		try {
			ResultSet res = state.executeQuery(sql);
			if (res.next()) {
				i = res.getInt("SERIAL");
			}
			res.close();
			res = null;
		} catch (SQLException e1) {
			Storage.logExcept(e1);
		} finally {
			close();
		}
		return i.toString();
	}

	public static void connect() {
		if (dbtype.equalsIgnoreCase("sqlite")) {
			conSQLite();
		}
		if (dbtype.equalsIgnoreCase("mysql")) {
			if (!conMySQL()) {
				Bukkit.getLogger().warning("[BugRepGUI] MySQL connect failed!");
				Bukkit.getLogger().warning("[BugRepGUI] Now using SQLite!");
				conSQLite();
			}
		}
	}

	public static void close() {
		try {
			state.close();
			state = null;
			c.close();
		} catch (Exception e) {
			Storage.logExcept(e);
		}
	}

	public static void conSQLite() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Storage.getFolder().getAbsolutePath() + "/bug.db");
			state = c.createStatement();
		} catch (SQLException e) {
			Storage.logExcept(e);
		} catch (ClassNotFoundException e) {
			Storage.logExcept(e);
		}
		String sql = "CREATE TABLE IF NOT EXISTS br_bug " + "(SERIAL INTEGER PRIMARY KEY   AUTOINCREMENT  NOT NULL,"
				+ " REPNAME           TEXT    NOT NULL, " + " REPTIME           TEXT     NOT NULL, "
				+ " REPMSG        TEXT  NOT NULL, " + " EXENAME         TEXT, " + " EXETIME         TEXT, "
				+ " EXEMSG         TEXT, " + " EXECUTED         INTEGER, " + " BACK         INTEGER);";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Storage.logExcept(e);
		}

	}

	public static boolean conMySQL() {
		String url = "jdbc:mysql://" + Storage.getConfig().getString("mysql-url") + ":"
				+ Storage.getConfig().getString("mysql-port") + "/" + Storage.getConfig().getString("mysql-db");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(url, Storage.getConfig().getString("mysql-username"),
					Storage.getConfig().getString("mysql-password"));
			state = c.createStatement();
		} catch (SQLException | ClassNotFoundException e) {
			Storage.logExcept(e);
			return false;
		}
		String sql = "CREATE TABLE IF NOT EXISTS br_bug " + "(SERIAL INT PRIMARY KEY  AUTO_INCREMENT   NOT NULL,"
				+ " REPNAME           TEXT    NOT NULL, " + " REPTIME           TEXT     NOT NULL, "
				+ " REPMSG        TEXT  NOT NULL, " + " EXENAME         TEXT, " + " EXETIME         TEXT, "
				+ " EXEMSG         TEXT, " + " EXECUTED         INT, " + " BACK         INT);";
		try {
			state.executeUpdate(sql);
		} catch (SQLException e) {
			Storage.logExcept(e);
		}
		return true;
	}
}
