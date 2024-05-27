package me.swever.defusedcore;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

class SpawnerGiveCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public SpawnerGiveCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        // Check if the command has the correct number of arguments
        if (args.length != 3) {
            player.sendMessage("Usage: /spawner give <player> <mob> <tier>");
            return true;
        }

        // Get the target player, mob, and tier from the command arguments
        Player targetPlayer = plugin.getServer().getPlayer(args[0]);
        String mob = args[1];
        String tier = args[2];

        // Check if the target player is online
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage("Player " + args[0] + " is not online.");
            return true;
        }

        // Check if the mob and tier are valid
        if (!isValidMob(mob) || !isValidTier(tier)) {
            player.sendMessage("Invalid mob or tier.");
            return true;
        }

        // Create a mob spawner item for the specified mob and tier
        ItemStack spawner = createSpawner(mob, tier);
        // Give the spawner item to the target player
        targetPlayer.getInventory().addItem(spawner);
        player.sendMessage("Gave " + targetPlayer.getName() + " a " + tier + " " + mob + " spawner.");

        return true;
    }

    private boolean isValidMob(String mob) {
        // You can implement your own logic to check if the mob is valid
        // For simplicity, I'll assume all mobs are valid
        return true;
    }

    private boolean isValidTier(String tier) {
        // You can implement your own logic to check if the tier is valid
        // For simplicity, I'll assume all tiers are valid
        return true;
    }

    private ItemStack createSpawner(String mob, String tier) {
        // Assuming you have a custom spawner item material
        Material spawnerMaterial = Material.SPAWNER;

        // Create a new ItemStack for the spawner
        ItemStack spawner = new ItemStack(spawnerMaterial);

        // Set the display name and lore for the spawner
        ItemMeta meta = spawner.getItemMeta();
        meta.setDisplayName(tier + " " + mob + " Spawner");
        meta.setLore(Arrays.asList("Tier: " + tier, "Mob: " + mob));
        spawner.setItemMeta(meta);

        // Return the spawner ItemStack
        return spawner;
    }

}
