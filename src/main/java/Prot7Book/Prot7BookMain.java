package Prot7Book;

import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;

public class Prot7BookMain extends CommandExecute implements Listener, CommandExecutor{
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 2 && Bukkit.getPlayerExact(args[0]) != null && utilityMain.isNumber(args[1]) && sender.isOp()) {
            utilityMain.GivePlayerItem(Bukkit.getPlayerExact(args[0]), utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.PROTECTION_ENVIRONMENTAL, 7), null, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Drag and drop on a piece of gear", ChatColor.YELLOW + "" + ChatColor.ITALIC + "to apply the enchant"), Integer.parseInt(args[1]));
            sender.sendMessage(ChatColor.DARK_GREEN + Bukkit.getPlayerExact(args[0]).getName() + ChatColor.GREEN + " has received the book");
        }
        return false;
    }
}
