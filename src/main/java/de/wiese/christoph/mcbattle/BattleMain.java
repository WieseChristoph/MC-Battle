package de.wiese.christoph.mcbattle;

import org.bukkit.plugin.java.JavaPlugin;

public class BattleMain extends JavaPlugin {
    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        getLogger().info("Battle Plugin Loaded!");
        plugin = this;

        getCommand("battle").setExecutor(new BattleCommands());
        getCommand("battle").setTabCompleter(new BattleTabCompleter());
        getServer().getPluginManager().registerEvents(new BattleListener(), this);
        getServer().getPluginManager().registerEvents(new BattleSign(), this);

        BattleManager.initArenas();
    }
    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("Battle Plugin Unloaded!");
    }
}
