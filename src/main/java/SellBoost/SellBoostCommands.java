package SellBoost;

import CustomItems.CustomItemsMain;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SellBoostCommands extends CommandExecute implements Listener, CommandExecutor {
    UtilityMain utilityMain = new UtilityMain();
    CustomItemsMain customItemsMain = new CustomItemsMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.GRAY + "Usage: /sellboost <Player Name> <Percentage> <Time (in hours)>");
            return true;
        }
        if (Bukkit.getPlayerExact(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + args[0] + " is not an online player");
            return true;
        }
        if (!(utilityMain.isNumber(args[1]))) {
            sender.sendMessage(ChatColor.RED + args[1] + " is not a number");
            return true;
        }
        if (!(utilityMain.isNumber(args[2]))) {
            sender.sendMessage(ChatColor.RED + args[2] + " is not a number");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        GiveSellBoostNote(target, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        return true;
    }

    private void GiveSellBoostNote(Player player, int Percentage, int Time) {
        ItemStack SellNote = customItemsMain.SellBoostNote(Percentage, Time);
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(SellNote);
        }
        else {
            player.getWorld().dropItemNaturally(player.getLocation(), SellNote);
        }
    }
}