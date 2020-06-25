package FactionsPickaxe;

import CustomItems.CustomItemsMain;
import Utility.UtilityMain;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class FactionsPickaxeCommands extends CommandExecute implements Listener, CommandExecutor {
    CustomItemsMain customItemsMain = new CustomItemsMain();
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.isOp()) {
            return false;
        }
        int pickStrength;
        Player player;
        try {
            player = sender.getServer().getPlayerExact(args[0]);
            pickStrength = Integer.parseInt(args[1]);
        } catch (Exception e) {
            return false;
        }
        utilityMain.GivePlayerItem(player, customItemsMain.trenchPickaxe(pickStrength), 1);
        return true;
    }
}
