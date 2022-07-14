package com.github.carthax08.prisonenchants;

import com.github.carthax08.prisonenchants.commands.EnchantsCommand;
import com.github.carthax08.prisonenchants.events.BlockBreakHandler;
import com.github.carthax08.prisonenchants.events.GUIClickEvent;
import com.github.carthax08.prisonenchants.events.PlayerInteractHandler;
import com.github.carthax08.prisonenchants.util.Util;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PluginMain extends JavaPlugin {

    private static PluginMain instance;

    public static Plugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Logger logger = getServer().getLogger();
        logger.info("[PrisonEnchants] Plugin loading, please wait...");
        getCommand("enchants").setExecutor(new EnchantsCommand());
        getServer().getPluginManager().registerEvents(new GUIClickEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractHandler(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakHandler(), this);
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Util.loadKeyChances(getConfig());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
