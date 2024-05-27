package me.swever.defusedcore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DefusedCore extends JavaPlugin {
    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();

        Objects.requireNonNull(getCommand("spawner")).setExecutor(new SpawnerGiveCommand(this));

        // Register plugins
        new DefusedSpawnersPlugin(this);
        new FactionCoresPlugin(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
