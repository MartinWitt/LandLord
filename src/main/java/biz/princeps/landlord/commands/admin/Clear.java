package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.PrincepsLib;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/07/17
 */
public class Clear extends LandlordCommand {

    public void onClearWorld(Player player, String target) {
        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                // Clearing all regions in one world
                if (target == null) {
                    World world = player.getWorld();
                    RegionManager regionManager = plugin.getWgHandler().getRegionManager(world);

                    Map<String, ProtectedRegion> regions = new HashMap<>(regionManager.getRegions());

                    regions.keySet().removeIf(key -> !evaluateRegion(key));

                    int count = regions.size();

                    regions.keySet().forEach(regionManager::removeRegion);

                    player.sendMessage(lm.getString("Commands.ClearWorld.success")
                            .replace("%count%", String.valueOf(count))
                            .replace("%world%", world.getName()));

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getMapManager().updateAll());
                } else {
                    // Clear only a specific player
                    plugin.getPlayerManager().getOfflinePlayerAsync(target, lPlayer -> {

                        if (lPlayer == null) {
                            // Failure
                            player.sendMessage(lm.getString("Commands.ClearWorld.noPlayer")
                                    .replace("%players%", target));
                        } else {
                            // Success
                            int amt = 0;
                            for (World world : Bukkit.getWorlds()) {
                                // Only count enabled worlds
                                if (!Landlord.getInstance().getConfig().getStringList("disabled-worlds").contains(world.getName())) {
                                    List<ProtectedRegion> rgs = plugin.getWgHandler().getRegions(lPlayer.getUuid(), world);
                                    amt += rgs.size();
                                    Set<String> toDelete = new HashSet<>();
                                    for (ProtectedRegion protectedRegion : rgs) {
                                        toDelete.add(protectedRegion.getId());
                                    }
                                    RegionManager rgm = plugin.getWgHandler().getRegionManager(world);
                                    for (String s : toDelete) {
                                        plugin.getOfferManager().removeOffer(s);
                                        rgm.removeRegion(s);
                                    }
                                }
                            }

                            player.sendMessage(lm.getString("Commands.ClearWorld.successPlayer")
                                    .replace("%count%", String.valueOf(amt))
                                    .replace("%player%", target));

                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getMapManager().updateAll());
                        }
                    });
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private boolean evaluateRegion(String a) {

        String[] split = a.split("_");
        try {
            int x = Integer.valueOf(split[1]);
            int z = Integer.valueOf(split[2]);
            Chunk chunk = Bukkit.getWorld(split[0]).getChunkAt(x, z);

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
