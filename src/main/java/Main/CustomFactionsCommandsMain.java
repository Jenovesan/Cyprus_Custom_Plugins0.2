package Main;

import FactionUpgrades.FactionUpgradeFShield;
import FactionUpgrades.FactionUpgradesFRally;
import FactionUpgrades.FactionUpgradesFTtntUpgrade;
import FactionUpgrades.FactionUpgradesGui;
import JackPot.JackPotCommands;
import Utility.UtilityMain;
import WarpGui.WarpGuiMain;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CustomFactionsCommandsMain implements Listener {
    MainClass mainClass;
    FactionUpgradesGui factionUpgradesGui;
    FactionUpgradesFTtntUpgrade factionUpgradesFTtntUpgrade;
    FactionUpgradesFRally factionUpgradesFRally;
    UtilityMain utilityMain = new UtilityMain();
    JackPotCommands jackPotCommands;
    WarpGuiMain warpGuiMain = new WarpGuiMain();
    public CustomFactionsCommandsMain(MainClass mc) {
        mainClass = mc;
        factionUpgradesGui = new FactionUpgradesGui(mainClass);
        factionUpgradesFTtntUpgrade = new FactionUpgradesFTtntUpgrade(mainClass);
        factionUpgradesFRally = new FactionUpgradesFRally(mainClass);
        jackPotCommands = mainClass.jackPotCommands;
    }

    @EventHandler
    private void chatEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        String[] args = getArgs(command);
        if (event.isCancelled()) {
            return;
        }

        // Override Commands //

        if (command.equalsIgnoreCase("/rl")) {
            player.sendMessage(ChatColor.RED + "Wrong server stupid head");
            event.setCancelled(true);
            return;
        } else if (command.equalsIgnoreCase("/printer") && !Board.getInstance().getFactionAt(new FLocation(player.getLocation())).equals(FPlayers.getInstance().getByPlayer(player).getFaction())) {
            player.sendMessage(ChatColor.RED + "You can only use printer in your territory");
            event.setCancelled(true);
        }
        else if (command.contains("/jackpot") || command.contains("/jp")) {
            jackPotCommands.StartingPoint(player, args);
            event.setCancelled(true);
        } else if (command.equalsIgnoreCase("/warp") || command.equalsIgnoreCase("/warps")) {
            warpGuiMain.StartingPoint(player);
            event.setCancelled(true);
        }

        // Custom Faction Commands //

        if (isCustomFactionsCommand(command) && !utilityMain.isInFaction(player)) {
            player.sendMessage(ChatColor.RED + "You must been in a faction to use this command");
            return;
        }
        if (StringUtils.containsIgnoreCase(command, "/f upgrade")) {
            factionUpgradesGui.StartingPoint(player);
            event.setCancelled(true);
        } else if (StringUtils.containsIgnoreCase(command, "/f tnt")) {
            factionUpgradesFTtntUpgrade.StartingPoint(player, args);
            event.setCancelled(true);
        } else if (StringUtils.containsIgnoreCase(command, "/f shield")) {
            mainClass.factionUpgradeFShield.StartingPointForFShield(player, args);
            event.setCancelled(true);
        } else if (StringUtils.containsIgnoreCase(command, "/f setregion") || StringUtils.containsIgnoreCase(command, "/f setpos1") || StringUtils.containsIgnoreCase(command, "/f setpos2") || StringUtils.containsIgnoreCase(command, "/f checkregion")) {
            mainClass.factionUpgradeFShield.StarrtingPointForFBaseChunk(player, command);
            event.setCancelled(true);
        } else if (StringUtils.containsIgnoreCase(command, "/f rally") || StringUtils.containsIgnoreCase(command, "/f setrally")) {
            factionUpgradesFRally.StartingPoint(player, args, command);
            event.setCancelled(true);
        } else if (StringUtils.containsIgnoreCase(command, "/f hhlock")) {
            mainClass.customFactionCommandsFLock.StartingPoint(player);
            event.setCancelled(true);
        } else if (StringUtils.containsIgnoreCase(command, "/f chest")) {
            mainClass.customFactionCommandsFChest.StartingPoint(player, args);
            event.setCancelled(true);
        } else if (StringUtils.containsIgnoreCase(command, "/f roster")) {
            mainClass.customFactionCommandsFRoster.StartingPoint(player, args);
            event.setCancelled(true);
        } else if (StringUtils.containsIgnoreCase(command, "/f alt")) {
            mainClass.customFactionsCommandsFAlts.StartingPoint(player, args);
            event.setCancelled(true);
        }
    }

    List<String> CustomFCommands = new ArrayList<>(Arrays.asList("/f upgade", "/f tnt", "/f shield", "/f rally", "/f hhlock", "/f chest", "/f roster", "/f setregion", "/f setpos1", "/f setpos2", "/f checkregion", "/f alt"));
    private boolean isCustomFactionsCommand(String command) {
        for (String string : CustomFCommands) {
            if (StringUtils.containsIgnoreCase(command, string)) {
                return true;
            }
        }
        return false;
    }

    private String[] getArgs(String command) {
        String[] args = command.split(" ");
        List<String> list = new LinkedList<String>(Arrays.asList(args));
        if (isCustomFactionsCommand(command)) {
            if (list.size() >= 2) {
                list.remove(0);
                list.remove(0);
            }
        } else {
            list.remove(0);
        }
        args = new String[list.size()];
        list.toArray(args);
        return args;
    }
}
