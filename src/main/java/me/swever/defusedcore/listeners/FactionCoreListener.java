package me.swever.defusedcore.listeners;

import com.massivecraft.factions.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionCoreListener implements Listener {
    private final JavaPlugin plugin;
    private final Map<Location, Integer> coreHealth = new HashMap<>();

    public FactionCoreListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.BEACON) {
            Player player = event.getPlayer();
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();

            if (!faction.isWilderness() && !faction.isSafeZone() && !faction.isWarZone()) {
                Location loc = block.getLocation();
                coreHealth.put(loc, plugin.getConfig().getInt("factioncores.core-beacon.health"));
                faction.sendMessage(ChatColor.GREEN + "Your Faction core has been placed");
            } else {
                player.sendMessage(ChatColor.RED + "You can only place faction cores in faction territory.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        if (block.getType() == Material.BEACON && coreHealth.containsKey(loc)) {
            Player player = event.getPlayer();
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();
            Faction defendingFaction = Board.getInstance().getFactionAt(new FLocation(loc));

            if (defendingFaction == null || faction == defendingFaction) {
                player.sendMessage(ChatColor.RED + "You cannot destroy your own faction's core.");
                event.setCancelled(true);
                return;
            }

            int currentHealth = coreHealth.get(loc);
            if (currentHealth <= 1) {
                coreHealth.remove(loc);
                Board.getInstance().unclaimAll(defendingFaction.getId());
                String broadcastMessage = String.format(plugin.getConfig().getString("factioncores.core-beacon.broadcast-message"),
                        defendingFaction.getTag(), fPlayer.getName());
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcastMessage));
            } else {
                coreHealth.put(loc, currentHealth - 1);
                player.sendMessage(ChatColor.YELLOW + "Faction core health: " + (currentHealth - 1));
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        for (Block block : blocks) {
            if (block.getType() == Material.BEACON) {
                Location loc = block.getLocation();
                if (coreHealth.containsKey(loc)) {
                    int currentHealth = coreHealth.get(loc);
                    coreHealth.put(loc, currentHealth - plugin.getConfig().getInt("factioncores.core-beacon.tnt-damage"));
                    if (currentHealth <= 1) {
                        coreHealth.remove(loc);
                        Faction defendingFaction = Board.getInstance().getFactionAt(new FLocation(loc));
                        Board.getInstance().unclaimAll(defendingFaction.getId());
                        String broadcastMessage = String.format(plugin.getConfig().getString("factioncores.core-beacon.broadcast-message"),
                                defendingFaction.getTag(), "TNT");
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcastMessage));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.BEACON) {
            Player player = event.getPlayer();
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();
            Faction defendingFaction = Board.getInstance().getFactionAt(new FLocation(block.getLocation()));

            if (defendingFaction != null && faction.equals(defendingFaction)) {
                Inventory gui = Bukkit.createInventory(null, 9, "Faction Core");
                // Add GUI items
                player.openInventory(gui);
                event.setCancelled(true);
            } else {
                player.sendMessage(ChatColor.RED + "You can only interact with your own faction's core.");
            }
        }
    }
}
