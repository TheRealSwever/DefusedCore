package me.swever.defusedcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SpawnerUpgradeListener implements Listener {
    private final JavaPlugin plugin;
    private final Map<Location, Integer> spawnerTiers = new HashMap<>();

    public SpawnerUpgradeListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().isSneaking() && event.getClickedBlock().getType() == Material.SPAWNER) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            int currentTier = spawnerTiers.getOrDefault(block.getLocation(), 1);
            Inventory gui = Bukkit.createInventory(null, plugin.getConfig().getInt("defusedspawners.gui-layout.size"), plugin.getConfig().getString("defusedspawners.gui-layout.title"));

            for (int i = 1; i <= 3; i++) {
                ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("Tier " + i + " Upgrade");
                int cost = plugin.getConfig().getInt("defusedspawners.tiers." + i + ".cost");
                double spawnRate = plugin.getConfig().getDouble("defusedspawners.tiers." + i + ".spawn-rate");
                if (player.getLevel() < cost) {
                    meta.setLore(Arrays.asList("Cost: §c" + cost + " XP", "Spawn Rate: " + spawnRate));
                } else {
                    meta.setLore(Arrays.asList("Cost: " + cost + " XP", "Spawn Rate: " + spawnRate));
                }
                item.setItemMeta(meta);
                gui.setItem(i - 1, item);
            }

            player.openInventory(gui);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.SPAWNER) {
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                Block block = event.getBlock();
                int tier = spawnerTiers.getOrDefault(block.getLocation(), 1);
                ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
                ItemMeta meta = spawnerItem.getItemMeta();
                meta.setLore(Arrays.asList("Tier » " + tier));
                spawnerItem.setItemMeta(meta);
                block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);
                spawnerTiers.remove(block.getLocation());
            }
        }
    }
}
