package biz.princeps.landlord;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public class LandLord extends ALandLord {

    @Override
    public void onLoad() {
        WorldGuardManager.initFlags();
    }

    @Override
    public void onEnable() {
        if (!checkDependencies()) {
            return;
        }

        this.worldGuardManager = new WorldGuardManager(this, getWorldGuard());
        this.utilsManager = new UtilsManager();
        this.materialsManager = new MaterialsManager();
        this.mobManager = new MobsManager();

        ((WorldGuardManager) worldGuardManager).initCache();

        super.onEnable();

        new PistonOverwriter(this);
    }


    @Override
    public void onDisable() {
        super.onDisable();
    }

    /**
     * Checks versions+availability for
     * a) spigot
     * b) protocollib
     * c) worldguard
     * d) worldedit
     * e) vault
     * <p>
     * Historically during the 1.13.2 development there was a lot of chanage in worldguard/edit. People constantly
     * complained about stuff not working because of some dumb updates that require variable renaming.
     * <p>
     * These checks should not be here in the first place in my opinion. So to my future me/anybody else: might wanna
     * get rid of this!!
     *
     * @return returns if all dependencies are satisfied
     */
    @Override
    protected boolean checkDependencies() {
        if (!super.checkDependencies()) return false;

        // Dependency stuff
        if (!Bukkit.getVersion().contains("1.13.2") && !Bukkit.getVersion().contains("1.14")) {
            haltPlugin("Invalid spigot version detected! LandLord requires 1.13.2/1.14.x");
            return false;
        }

        if (getWorldGuard() == null) {
            haltPlugin("WorldGuard not found! Please ensure you have the correct version of WorldGuard in order to " +
                    "use LandLord");
            return false;
        } else {
            String worldGuardVersion = getWorldGuard().getDescription().getVersion();
            if (!worldGuardVersion.contains("7.0.0") || worldGuardVersion.contains("SNAPSHOT")) {
                haltPlugin("Invalid WorldGuard Version found. LandLord requires WG 7.0.0! : You have WG " + worldGuardVersion);
                return false;
            }

            String worldEditVersion = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();
            if (!worldEditVersion.contains("7.0.0") || worldEditVersion.contains("SNAPSHOT")) {
                haltPlugin("Invalid WorldEdit Version found. LandLord requires WE 7.0.0! : You have WE " + worldEditVersion);
                return false;
            }
        }
        return true;
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }
}
