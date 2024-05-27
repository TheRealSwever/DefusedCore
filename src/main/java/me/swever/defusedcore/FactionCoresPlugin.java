package me.swever.defusedcore;

import me.swever.defusedcore.listeners.FactionCoreListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionCoresPlugin {
    private final JavaPlugin plugin;

    public FactionCoresPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new FactionCoreListener(plugin), plugin);
    }
}
