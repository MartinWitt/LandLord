package biz.princeps.landlord.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public interface ILandLord {

    /**
     * Adapter to JavaPlugin#getConfig
     *
     * @return the config file
     */
    FileConfiguration getConfig();

    /**
     * Adapter to JavaPlugin#getConfig
     *
     * @return the config file
     */
    Logger getLogger();

    /**
     * Returns the instance of the JavaPlugin.
     * Useful for starting runnables.
     *
     * @return instance of JavaPlugin
     */
    JavaPlugin getPlugin();


    /**
     * Gets the reference to the WorldGuardManager.
     * This class is responsible to handle all interactions with worldguard.
     *
     * @return the worldguard manager
     */
    IWorldGuardManager getWGManager();

    /**
     * Gets the reference to the MaterialManager.
     * This class is responsible to handle Materials and Itemstacks, that change from version to version.
     *
     * @return the mat manager
     */
    IMaterialsManager getMaterialsManager();

    /**
     * Gets the reference to the UtilsManager
     * This class is responsible to handle interactions with spigot, that change from version to version.
     *
     * @return the utils manager
     */
    IUtilsManager getUtilsManager();

    /**
     * Gets the reference to the PlayerManager.
     * This class is responsible to handle interactions with LPlayers.
     *
     * @return the player manager
     */
    IPlayerManager getPlayerManager();

    /**
     * Gets the reference to the OfferManager.
     * This class manages interactions with the land advertise/offer system.
     *
     * @return the offer manager
     */
    IOfferManager getOfferManager();

    /**
     * Gets the reference to the CostManager.
     * This class calculates costs for the next claim.
     *
     * @return the cost manager
     */
    ICostManager getCostManager();

    /**
     * Gets the reference to the MapManager.
     * This class handles interactions with the land map system.
     *
     * @return the map manager
     */
    IMapManager getMapManager();

    /**
     * Gets the reference to the language manager.
     * This class is responsible to handle translations.
     *
     * @return the language manager
     */
    ILangManager getLangManager();

    /**
     * Gets the reference to the vault manager.
     * This class is responsible for interactions with money.
     *
     * @return the vault manager
     */
    IVaultManager getVaultManager();

    /**
     * Gets the reference to the delimitation manager.
     * This class is responsible for the delimitation of lands.
     *
     * @return the delimitation manager
     */
    IDelimitationManager getDelimitationManager();

    /**
     * Sets up PrincepsLib. Also sets specific translated messages for princepslib
     */
    void setupPrincepsLib();

    /**
     * Get the reference to the MobManager.
     * This class is responsible to interact with spigot mobs that change from version to version.
     *
     * @return the mob manager
     */
    IMobManager getMobManager();
}
