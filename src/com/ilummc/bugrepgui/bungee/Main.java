package com.ilummc.bugrepgui.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class Main extends Plugin {
    static Logger log;
    static File folder;
    static Configuration config;
    static Configuration lang;
    static File cf;
    static File lf;
    static ProxyServer server;

    public void onEnable() {
        this.init(this.getDataFolder(), this.getLogger());
        this.getLogger().info("BungeeCord mode is on!");
        this.getProxy().getPluginManager().registerListener(this, new BungeeListener());
    }

    @Override
    public void onDisable() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, cf);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(lang, lf);
        } catch (Exception e) {
            printException(e.getMessage());
        }
        this.getLogger().info("BugRepGUI disabled! Thanks for chosing us!");
    }

    public void init(File f, Logger log) {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        Main.log = log;
        Main.folder = f;
        Main.server = this.getProxy();
        cf = new File(f + "/config.yml");
        try {
            if (!cf.exists()) {
                Files.copy(getResourceAsStream("config.yml"), cf.toPath());
            }
            reloadConfig();
            lf = new File(f + "/lang_" + config.getString("lang") + ".yml");
            if (!lf.exists()) {
                Files.copy(getResourceAsStream("lang_" + config.getString("lang") + ".yml"), lf.toPath()
                );
            }
            reloadLang();
        } catch (Exception e) {
            printException(e.getMessage());
        }

    }

    private void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(cf);
        } catch (IOException e) {
            printException(e.getMessage());
        }
    }

    private void reloadLang() {
        try {
            lang = ConfigurationProvider.getProvider(YamlConfiguration.class).load(lf);
        } catch (IOException e) {
            printException(e.getMessage());
        }
    }

    public static void printException(String msg) {
        log.warning("Error: " + msg);
    }
}
