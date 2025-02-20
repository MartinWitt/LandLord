package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.admin.AdminClaim;
import biz.princeps.landlord.commands.admin.AdminTeleport;
import biz.princeps.landlord.commands.admin.Clear;
import biz.princeps.landlord.commands.admin.ClearInactive;
import biz.princeps.landlord.commands.admin.Debug;
import biz.princeps.landlord.commands.admin.GiveClaims;
import biz.princeps.landlord.commands.admin.Reload;
import biz.princeps.landlord.commands.admin.Update;
import biz.princeps.landlord.commands.claiming.Claim;
import biz.princeps.landlord.commands.claiming.Claims;
import biz.princeps.landlord.commands.claiming.MultiClaim;
import biz.princeps.landlord.commands.claiming.MultiUnclaim;
import biz.princeps.landlord.commands.claiming.Shop;
import biz.princeps.landlord.commands.claiming.Unclaim;
import biz.princeps.landlord.commands.claiming.UnclaimAll;
import biz.princeps.landlord.commands.claiming.adv.Advertise;
import biz.princeps.landlord.commands.claiming.adv.RemoveAdvertise;
import biz.princeps.landlord.commands.friends.Addfriend;
import biz.princeps.landlord.commands.friends.AddfriendAll;
import biz.princeps.landlord.commands.friends.ListFriends;
import biz.princeps.landlord.commands.friends.MultiAddfriend;
import biz.princeps.landlord.commands.friends.MultiRemovefriend;
import biz.princeps.landlord.commands.friends.Unfriend;
import biz.princeps.landlord.commands.friends.UnfriendAll;
import biz.princeps.landlord.commands.homes.Home;
import biz.princeps.landlord.commands.homes.SetHome;
import biz.princeps.landlord.commands.management.Info;
import biz.princeps.landlord.commands.management.LandMap;
import biz.princeps.landlord.commands.management.ListLands;
import biz.princeps.landlord.commands.management.Manage;
import biz.princeps.landlord.commands.management.ManageAll;
import biz.princeps.landlord.commands.management.MultiListLands;
import biz.princeps.landlord.commands.management.MultiManage;
import biz.princeps.landlord.commands.management.Regenerate;
import biz.princeps.landlord.commands.management.borders.Borders;
import biz.princeps.landlord.multi.MultiMode;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.MainCommand;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.command.SubCommand;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/07/17
 * <p>
 * This command protection may look a bit unfamiliar. It is based on shitty system I programmed a long time ago (PrincepsLib)
 * Basically a single command is created by extending MainCommand. For example you would do:
 * class HealCommand extends MainCommand {...} // introduces a heal command
 * Landlordbase describes the base command ./landlord
 * Subcommands are created by creating a class, that extends SubCommand. Add that one as subcmd.
 * Always call super(cmdname, description, usage, permissions, aliases) to initialize the (sub)command with everything
 * it needs to work.
 */
public class Landlordbase extends MainCommand {

    private final ILandLord plugin;

    public Landlordbase(ILandLord plugin) {
        super(plugin.getConfig().getString("CommandSettings.Main.name"),
                plugin.getConfig().getString("CommandSettings.Main.description"),
                plugin.getConfig().getString("CommandSettings.Main.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Main.permissions")),
                plugin.getConfig().getStringList("CommandSettings.Main.aliases").toArray(new String[]{}));
        this.plugin = plugin;
        reloadCommands();
    }

    /**
     * Reloads all commands. Reinitialisation, to pick up changed config variables
     */
    private void reloadCommands() {
        this.clearSubcommands();
        this.addSubcommand(new Version());
        this.addSubcommand(new Confirm());

        this.addSubcommand(new Info(plugin));
        this.addSubcommand(new Claim(plugin, false));
        this.addSubcommand(new Unclaim(plugin));
        this.addSubcommand(new UnclaimAll(plugin));
        this.addSubcommand(new Addfriend(plugin));
        this.addSubcommand(new AddfriendAll(plugin));
        this.addSubcommand(new Unfriend(plugin));
        this.addSubcommand(new UnfriendAll(plugin));
        this.addSubcommand(new Advertise(plugin));
        this.addSubcommand(new RemoveAdvertise(plugin));
        this.addSubcommand(new ListFriends(plugin));
        this.addSubcommand(new ListLands(plugin));
        this.addSubcommand(new Claims(plugin));
        this.addSubcommand(new Shop(plugin));
        this.addSubcommand(new GiveClaims(plugin));
        this.addSubcommand(new Update(plugin));
        this.addSubcommand(new AdminTeleport(plugin));
        this.addSubcommand(new AdminClaim(plugin));
        this.addSubcommand(new MultiClaim(plugin));
        this.addSubcommand(new Borders(plugin));
        this.addSubcommand(new Home(plugin));
        this.addSubcommand(new SetHome(plugin));
        this.addSubcommand(new Manage(plugin));
        this.addSubcommand(new ManageAll(plugin));
        this.addSubcommand(new Clear(plugin));
        this.addSubcommand(new ClearInactive(plugin));
        this.addSubcommand(new LandMap(plugin));
        this.addSubcommand(new Reload(plugin));
        this.addSubcommand(new Regenerate(plugin));
        this.addSubcommand(new MultiUnclaim(plugin));
        this.addSubcommand(new MultiAddfriend(plugin));
        this.addSubcommand(new MultiRemovefriend(plugin));
        this.addSubcommand(new MultiListLands(plugin));
        this.addSubcommand(new MultiManage(plugin));
        this.addSubcommand(new Debug(plugin));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> tabReturn = new ArrayList<>();

        // Length == 1 means there is just the first thing like /ll typed
        if (args.length == 1) {
            for (SubCommand subCommand : this.subCommandMap.values()) {
                if (subCommand.hasPermission(sender)) {
                    if (subCommand instanceof Borders) {
                        if (Options.enabled_borders()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof LandMap) {
                        if (Options.enabled_map()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof Shop || subCommand instanceof GiveClaims) {
                        if (Options.enabled_shop()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof Home || subCommand instanceof SetHome) {
                        if (Options.enabled_homes()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else {
                        tabReturn.add(subCommand.getName());
                    }
                }
            }

            if (!args[0].isEmpty()) {
                tabReturn.removeIf(next -> !next.startsWith(args[0]));
            }
        } else if (args.length == 2) {
            for (SubCommand subCommand : subCommandMap.values()) {
                if (subCommand.matches(args[0])) {
                    if (subCommand instanceof GiveClaims) {
                        tabReturn.add("<amount>");
                        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                            tabReturn.add(onlinePlayer.getName());
                        }
                        return tabReturn;
                    }

                    if (subCommand instanceof AdminTeleport) {
                        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                            tabReturn.add(onlinePlayer.getName());
                        }
                        return tabReturn;
                    }

                    if (subCommand instanceof LandMap) {
                        tabReturn.add("on");
                        tabReturn.add("off");
                        return tabReturn;
                    }

                    if (subCommand instanceof Addfriend || subCommand instanceof AddfriendAll ||
                            subCommand instanceof Unfriend || subCommand instanceof UnfriendAll) {
                        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                            if (!onlinePlayer.getName().startsWith(args[1]) ||
                                    (sender instanceof Player && !((Player) sender).canSee(onlinePlayer)))
                                continue;

                            tabReturn.add(onlinePlayer.getName());
                        }
                        return tabReturn;
                    }

                    if (subCommand instanceof MultiClaim || subCommand instanceof MultiUnclaim ||
                            subCommand instanceof MultiAddfriend || subCommand instanceof MultiRemovefriend ||
                            subCommand instanceof MultiListLands || subCommand instanceof MultiManage) {
                        for (MultiMode multiMode : MultiMode.values()) {
                            tabReturn.add(multiMode.name());
                        }
                        return tabReturn;
                    }

                    if (subCommand instanceof UnclaimAll) {
                        for (World world : plugin.getServer().getWorlds()) {
                            tabReturn.add(world.getName());
                        }
                        return tabReturn;
                    }

                    if (subCommand instanceof Update) {
                        tabReturn.add("-u");
                        tabReturn.add("-r");
                        tabReturn.add("-c");
                        return tabReturn;
                    }
                }
            }
        } else if (args.length == 3) {
            for (SubCommand subCommand : subCommandMap.values()) {
                if (subCommand.matches(args[0])) {
                    if (subCommand instanceof GiveClaims) {
                        tabReturn.add("<amount>");
                        tabReturn.add("<price>");
                        return tabReturn;
                    }

                    if (subCommand instanceof MultiAddfriend || subCommand instanceof MultiRemovefriend) {
                        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                            if (!onlinePlayer.getName().startsWith(args[2]) ||
                                    (sender instanceof Player && !((Player) sender).canSee(onlinePlayer)))
                                continue;

                            tabReturn.add(onlinePlayer.getName());
                        }
                        return tabReturn;
                    }
                }
            }
        } else if (args.length == 4) {
            for (SubCommand subCommand : subCommandMap.values()) {
                if (subCommand.matches(args[0])) {
                    if (subCommand instanceof GiveClaims) {
                        tabReturn.add("<amount>");
                    }
                }
            }
        }
        return tabReturn;
    }

    /**
     * Main onCommand function of ./landlord.
     * Display the help menu here.
     * This function is not called for subcommands like ./ll claim
     *
     * @param properties a cool properties object, that contains stuff like isPlayer, isConsole
     * @param arguments  the arguments passed here.
     */
    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole())
            return;

        ILangManager lm = plugin.getLangManager();
        List<String> playersList = lm.getStringList("Commands.Help.players");
        List<String> adminList = lm.getStringList("Commands.Help.admins");

        int perSite = plugin.getConfig().getInt("HelpCommandPerSite");

        String[] argsN = new String[1];
        if (arguments.get().length == 1) {
            argsN[0] = (arguments.get(0) == null ? "0" : arguments.get(0));
        }

        List<String> toDisplay = new ArrayList<>();
        if (properties.getPlayer().hasPermission("landlord.admin.help"))
            toDisplay.addAll(adminList);
        toDisplay.addAll(playersList);

        // plugin.getLogger().log(Level.INFO, String.valueOf(toDisplay.size()));

        MultiPagedMessage msg = new MultiPagedMessage.Builder()
                .setElements(toDisplay)
                .setPerSite(perSite)
                .setHeaderString(lm.getRawString("Commands.Help.header"))
                .setNextString(lm.getRawString("Commands.Help.next"))
                .setPreviousString(lm.getRawString("Commands.Help.previous"))
                .setCommand(plugin.getConfig().getString("CommandSettings.Main.name"), argsN).build();
        plugin.getUtilsManager().sendBasecomponent(properties.getPlayer(), msg.create());
    }

    public class Confirm extends SubCommand {

        public Confirm() {
            super("confirm",
                    "/ll help",
                    Sets.newHashSet(Collections.singleton("landlord.use")),
                    Sets.newHashSet());
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            // just a placeholder for the confirmationmanager, this is on purpose! Check PrincepsLib for more info.
        }
    }

    public class Version extends SubCommand {

        public Version() {
            super("version",
                    "/ll version",
                    Sets.newHashSet(Collections.singleton("landlord.admin")),
                    Sets.newHashSet());
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            String msg = plugin.getLangManager().getTag() + " &aLandLord version: &7%version%"
                    .replace("%version%", plugin.getDescription().getVersion());
            properties.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

}
