package com.ilummc.bugrepgui.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;

import com.ilummc.bugrepgui.Storage;

public class UpdateChecker {
	public static void check(String version, String web) {
		final String ver = version;
		final String webn = web;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Storage.log("checking-update");
				try {
					HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php")
							.openConnection();
					c.setDoOutput(true);
					c.setRequestMethod("POST");
					c.getOutputStream()
							.write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=35119")
									.getBytes("UTF-8"));
					String oldVersion = ver;
					String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine()
							.replaceAll("[a-zA-Z ]", "");
					if (newVersion.equals(oldVersion)) {
						Storage.log("no-new-version");
					} else {
						String[] msg = Storage.getMsg("new-version").replaceAll("%version%", newVersion)
								.replaceAll("%website%", webn).split("\\\\n");
						for (int i = 0; i < msg.length; i++) {
							msg[i] = Storage.getPrefix() + Storage.compile(msg[i]);
						}
						Bukkit.getConsoleSender().sendMessage(msg);
					}
				} catch (Exception e) {
					Storage.logExcept(e);
				}
			}
		});
		t.start();
	}
}
