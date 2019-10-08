package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ClearGUI;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/07/17
 */
public class Clear extends LandlordCommand {

    private IWorldGuardManager wg;

    public Clear(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Clear.name"),
                pl.getConfig().getString("CommandSettings.Clear.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Clear.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Clear.aliases")));
        this.wg = pl.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }
        Player player = properties.getPlayer();

        if (arguments.size() == 1){
            // Clear a single player
            UUID id = UUID.fromString(arguments.get()[0]);
            clearPlayer(id, player);
            return;
        }

        if (isDisabledWorld(player)) return;

        /*
         * Clear Options:
         * 1. Clear all for player x        (target==x || player stands inside x claim)
         * 2. Clear only specific claim     (target==null)
         * 3. Clear entire world            (target==null)
         */
        ClearGUI clearGUI = new ClearGUI(plugin, player);
        clearGUI.display();
    }

    private void clearPlayer(UUID id, Player player) {
        plugin.getPlayerManager().getOffline(id, (lPlayer) -> {
            if (lPlayer == null) {
                // Failure
                lm.sendMessage(player, lm.getString("Commands.ClearWorld.noPlayer")
                        .replace("%players%", id.toString()));
            } else {
                // Success
                Set<IOwnedLand> regions = wg.getRegions(lPlayer.getUuid());
                int amt = handleUnclaim(regions);

                lm.sendMessage(player, lm.getString("Commands.ClearWorld.gui.clearplayer.success")
                        .replace("%count%", String.valueOf(amt))
                        .replace("%player%", lPlayer.getName()));

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getPlugin(),
                        () -> plugin.getMapManager().updateAll());
            }
        });
    }

    //TODO checkout if this is a memory leak
    private int handleUnclaim(Set<IOwnedLand> regions) {
        int count = regions.size();

        for (IOwnedLand region : Sets.newHashSet(regions)) {
            wg.unclaim(region);
        }
        return count;
    }
}
