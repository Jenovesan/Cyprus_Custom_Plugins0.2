package SupplyDrop;

import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SupplyDropCommands extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    SupplyDropMain supplyDropMain;
    SupplyDropTimer supplyDropTimer;
    UtilityMain utilityMain = new UtilityMain();
    public SupplyDropCommands(MainClass mc) {
        mainClass = mc;
        supplyDropMain = new SupplyDropMain(mainClass);
        supplyDropTimer = new SupplyDropTimer(mainClass);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            Usage(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("time")) {
            Time(player);
        }
        else if (args[0].equalsIgnoreCase("items")) {
            Items(player);
        }
        if (player.isOp()) {
            if (args[0].equalsIgnoreCase("summon")) {
                Summon();
            }
            else if (args[0].equalsIgnoreCase("settime")) {
                if (args.length == 2) {
                    SetTime(player, Integer.parseInt(args[1]));
                }
                else {
                    Usage(player);
                }
            }
            else if (args[0].equalsIgnoreCase("enable")) {
                Enable(player);
            }
            else if (args[0].equalsIgnoreCase("disable")) {
                Disable(player);
            }
            else if (args[0].equalsIgnoreCase("state")) {
                State(player);
            }
        }
        return true;
    }

    private void Usage(Player player) {
        String g = ChatColor.GRAY + "";
        if (!(player.isOp())) {
            player.sendMessage(g + "/sd time - amount of time until next supply drop" + "\n" + "/sd items - items findable in the supply drop");
        }
        else {
            player.sendMessage(g + "/sd time - amount of time until next supply drop" + "\n" + "/sd items - items findable in the supply drop" + "\n" + "/sd summon - summons a supply drop" + "\n" + "/sd settime <Number> - sets the supply drop time" + "\n" + "/sd (enable/eisable) - enables or disables supply drops" + "\n" + "/sd state - returns whether supply drops are enabled or disabled");
        }
    }

    private void Enable(Player player) {
        mainClass.getConfig().set("State", true);
        mainClass.saveConfig();
        player.sendMessage(ChatColor.GREEN + "Supply drops have been enabled");
    }

    private void Disable(Player player) {
        mainClass.getConfig().set("State", false);
        mainClass.saveConfig();
        player.sendMessage(ChatColor.RED + "Supply drops have been disabled");
    }

    private void State(Player player) {
        boolean state = mainClass.getConfig().getBoolean("State");
        if (state) {
            player.sendMessage(ChatColor.GREEN + "Supply drops are currently enabled");
        }
        else {
            player.sendMessage(ChatColor.RED + "Supply drops are currently disabled");
        }
    }

    private void Time(Player player) {
        int TimeUntilSupplyDrop = mainClass.getConfig().getInt("TimeUntilSupplyDrop");
        player.sendMessage(ChatColor.RED + utilityMain.FormatTime(TimeUntilSupplyDrop));
    }

    private void Items(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 36, ChatColor.RED + "" + ChatColor.BOLD + "Supply Drop Items");
        SetItems(inv);
        player.openInventory(inv);
    }
    private void Summon() {
        supplyDropMain.SummonSupplyDrop();
        mainClass.getConfig().set("TimeUntilSupplyDrop", 240);
    }
    private void SetTime(Player player, int Time) {
        mainClass.getConfig().set("TimeUntilSupplyDrop", Time);
        mainClass.saveConfig();
        player.sendMessage(ChatColor.GREEN + "Supply drop time set to " + Time + " minutes");
    }

    private void SetItems(Inventory inv) {
        ItemStack Red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(0, Red);
        inv.setItem(1, Black);
        inv.setItem(2, Red);
        inv.setItem(3, Black);
        inv.setItem(4, Red);
        inv.setItem(5, Black);
        inv.setItem(6, Red);
        inv.setItem(7, Black);
        inv.setItem(8, Red);
        inv.setItem(27, Red);
        inv.setItem(28, Black);
        inv.setItem(29, Red);
        inv.setItem(30, Black);
        inv.setItem(31, Black);
        inv.setItem(32, Red);
        inv.setItem(33, Black);
        inv.setItem(34, Red);
        inv.setItem(35, Black);
        inv.setItem(9, createGuiItem(Material.ENDER_CHEST, 1, ChatColor.RED + "" + ChatColor.BOLD + "Tier II Money Pouch " + ChatColor.RESET + ChatColor.GRAY + "(Right Click)", ChatColor.GRAY + "This mysterious object was dug", ChatColor.GRAY + "out the grass by some very brave", ChatColor.GRAY + "players. It holds great power &", ChatColor.GRAY + "loot."));
        inv.setItem(10, createGuiItem(Material.ENDER_CHEST, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Tier III Money Pouch " + ChatColor.RESET + ChatColor.GRAY + "(Right Click)", ChatColor.GRAY + "This shining object was taken", ChatColor.GRAY + "from the cave of the great legend", ChatColor.GRAY + "known as Herobrine. It wields massive", ChatColor.GRAY + "power."));
        inv.setItem(11, createGuiItem(Material.MOB_SPAWNER,1,ChatColor.YELLOW + "Zombie " + ChatColor.WHITE + "Spawner"));
    }

    private ItemStack createGuiItem(Material material, int Amount, String name, String...lore) {
        ItemStack item = new ItemStack(material, Amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metaLore = new ArrayList<String>();
        for (String loreComments : lore) {
            metaLore.add(loreComments);
        }
        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }
}
