package HarvesterHoes;

import CustomItems.CustomItemsMain;
import Utility.UtilityMain;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;

public class HarvesterHoeCommands extends CommandExecute implements Listener, CommandExecutor{
    UtilityMain utilityMain = new UtilityMain();
    CustomItemsMain customItemsMain = new CustomItemsMain();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length == 3 && utilityMain.isNumber(args[2]) && args[0].equalsIgnoreCase("give") && Bukkit.getPlayerExact(args[1]) != null) {
                Player TargetPlayer = Bukkit.getPlayerExact(args[1]);
                utilityMain.GivePlayerItem(TargetPlayer, customItemsMain.HarvesterHoe(Integer.parseInt(args[2])), 1);
                return true;
            }
            else {
                sender.sendMessage(ChatColor.GRAY + "Usage: /hh give <PlayerName> <Level>");
            }
        }
        return false;
    }
}