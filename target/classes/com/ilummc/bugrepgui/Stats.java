package com.ilummc.bugrepgui;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ilummc.bugrepgui.stats.Reporter;

public class Stats {
	public static String[] loadTextRep() {
		Map<String, Integer> res = Database.loadStatsRep();
		Set<String> set = res.keySet();
		Iterator<String> it = set.iterator();
		Set<Reporter> rank = new TreeSet<>();
		while (it.hasNext()) {
			String name = it.next();
			Reporter rep = new Reporter(name, res.get(name));
			rank.add(rep);
		}
		String[] msg = new String[rank.size() + 2];
		msg[0] = Storage.getMsg("rep-rank");
		Iterator<Reporter> its = rank.iterator();
		for (int i = 1; i <= rank.size(); i++) {
			Reporter next = its.next();
			msg[i] = next.getName() + " : " + next.getAmount().toString();
		}
		Integer all = rank.size();
		msg[all + 1] = Storage.getMsg("rep-stats").replaceAll("%amount%", all.toString());
		return msg;
	}
	public static String[] loadTextExe() {
		Map<String, Integer> res = Database.loadStatsExe();
		Set<String> set = res.keySet();
		Iterator<String> it = set.iterator();
		Set<Reporter> rank = new TreeSet<>();
		while (it.hasNext()) {
			String name = it.next();
			Reporter rep = new Reporter(name, res.get(name));
			rank.add(rep);
		}
		String[] msg = new String[rank.size() + 2];
		msg[0] = Storage.getMsg("exe-rank");
		Iterator<Reporter> its = rank.iterator();
		for (int i = 1; i <= rank.size(); i++) {
			Reporter next = its.next();
			msg[i] = next.getName() + " : " + next.getAmount().toString();
		}
		Integer all = rank.size();
		msg[all + 1] = Storage.getMsg("rep-stats").replaceAll("%amount%", all.toString());
		return msg;
	}
}
