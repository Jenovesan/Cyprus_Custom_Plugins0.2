package WhiteList;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WhiteListMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    public WhiteListMain(MainClass mc) {
        mainClass = mc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be OP'd to use this command");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                WhiteListEnabled = true;
                sender.sendMessage(ChatColor.GREEN + "Whitelist " + ChatColor.DARK_GREEN + "enabled");
                SaveWhiteListInfoInConfig();
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                WhiteListEnabled = false;
                sender.sendMessage(ChatColor.RED + "Whitelist " + ChatColor.DARK_RED + "disabled");
                SaveWhiteListInfoInConfig();
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(String.join("\n", getWhiteListedNames()));
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                if (nameIsWhiteListed(args[1])) {
                    WhiteListedPlayers.remove(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                    SaveWhiteListInfoInConfig();
                    sender.sendMessage(ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + " removed from whitelist");
                } else {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not whitelisted");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("add")) {
                if (Bukkit.getOfflinePlayer(args[1]) != null) {
                    WhiteListedPlayers.add(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                    SaveWhiteListInfoInConfig();
                    sender.sendMessage(ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + " added to the whitelist");
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a valid player");
                }
                return true;
            }
        }
        sender.sendMessage(ChatColor.WHITE + "Usage:" + "\n" + "/whitelist list" + "\n" + "/whitelist add <Player Name>" + "\n" + "/whitelist remove <Player Name>" + "\n" + "/whitelist on" + "\n" + "/whitelist off");
        return true;
    }

    @EventHandler
    private void AllowOrDisallowAPlayerToJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (WhiteListEnabled && !isWhiteListed(player) && !player.isOp()) {
            player.kickPlayer(ChatColor.RED + "You are not whitelisted");
        }
    }

    private boolean isWhiteListed(Player player) {return WhiteListedPlayers.contains(player.getUniqueId().toString()); }

    private boolean nameIsWhiteListed(String name) { return getWhiteListedNames().contains(name); }

    private List<UUID> getWhiteListedUUIDS() {
        List<UUID> uuids = new ArrayList<>();
        for (String string : WhiteListedPlayers) {
            uuids.add(UUID.fromString(string));
        }
        return uuids;
    }

    private List<String> getWhiteListedNames() {
        List<UUID> uuids = getWhiteListedUUIDS();
        List<String> names = new ArrayList<>();
        for (UUID uuid : uuids) {
            names.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        Collections.sort(names);
        return names;
    }


    boolean WhiteListEnabled = true;
    List<String> WhiteListedPlayers = new ArrayList<>();

    private void SaveWhiteListInfoInConfig() {
        mainClass.getConfig().set("WhiteListEnabled", WhiteListEnabled);
        mainClass.getConfig().set("WhiteListedPlayers", WhiteListedPlayers);
        mainClass.saveConfig();
    }

    public void LoadWhiteListInfoFromConfig() {
        WhiteListEnabled = mainClass.getConfig().getBoolean("WhiteListEnabled");
        WhiteListedPlayers = mainClass.getConfig().getStringList("WhiteListedPlayers");
    }
}
