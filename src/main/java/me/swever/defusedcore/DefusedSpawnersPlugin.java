package me.swever.defusedcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DefusedSpawnersPlugin {
    private final JavaPlugin plugin;

    public DefusedSpawnersPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new me.swever.defusedcore.listeners.SpawnerUpgradeListener(plugin), plugin);
    }
}
