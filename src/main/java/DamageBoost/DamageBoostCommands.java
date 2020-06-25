package DamageBoost;

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

public class DamageBoostCommands extends CommandExecute implements Listener, CommandExecutor {
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.GRAY + "Usage: /damageboost <Player Name> <Percentage> <Time (in hours)>");
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
        GiveDamageBoostNote(target, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        return true;
    }

    private void GiveDamageBoostNote(Player player, int Percentage, int Time) {
        ItemStack DamageNote = DamageBoostNote(Percentage, Time);
        utilityMain.GivePlayerItem(player, DamageNote, 1);
    }

    private ItemStack DamageBoostNote(int Percentage, int Time) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + Percentage + "% Damage Boost " + ChatColor.YELLOW + "(" + Time + " hours)");
        lore.add(ChatColor.GRAY + "Right click to apply the damage boost");
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 0, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

}