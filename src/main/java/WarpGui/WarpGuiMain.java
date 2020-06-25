package WarpGui;

import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarpGuiMain implements Listener {
    UtilityMain utilityMain = new UtilityMain();
    public void StartingPoint(Player player) {
        LoadGui(player);
    }

    private void LoadGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 45, ChatColor.RED + "" + ChatColor.BOLD + "Warps");
        FillGui(inv);
        SetWarpItems(inv);
        player.openInventory(inv);
    }

    private void SetWarpItems(Inventory inv) {
       inv.setItem(11, utilityMain.createGuiItem(Material.ENDER_STONE, ChatColor.RED + "" + ChatColor.BOLD + "End", ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to warp to the end"));
       inv.setItem(15, utilityMain.createGuiItem(Material.NETHERRACK, ChatColor.RED + "" + ChatColor.BOLD + "Nether", ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to warp to the nether"));
       inv.setItem(28, utilityMain.createGuiItem(Material.TRIPWIRE_HOOK, ChatColor.RED + "" + ChatColor.BOLD + "Crates", ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to warp to the crates warp"));
       inv.setItem(31, utilityMain.createGuiItem(Material.DIAMOND_SWORD, ChatColor.RED + "" + ChatColor.BOLD + "PVP", ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to warp to the pvp warp"));
       inv.setItem(34, utilityMain.createGuiItem(Material.ENCHANTED_BOOK, ChatColor.RED + "" + ChatColor.BOLD + "Enchant", ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to warp to the enchant warp"));
    }

    private void FillGui(Inventory inv) {
        List<Integer> list = new ArrayList<>(Arrays.asList(2,6,10,12,13,14,16,19,20,22,24,25,27,29,30,32,33,35,37,40,43));
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack Red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        for (int i = 0; i < inv.getSize(); i++) {
            if (list.contains(i)) {
                inv.setItem(i, Red);
            } else {
                inv.setItem(i, Black);
            }
        }
    }

    @EventHandler
    private void WarpWhenClickWarp(InventoryClickEvent event) {
        if (event.getSlot() == -999) {
            return;
        }
        if (!event.getInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Warps")) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Material type = event.getCurrentItem().getType();
        if (type.equals(Material.STAINED_GLASS_PANE)) {
            return;
        }
        if (type.equals(Material.ENDER_STONE)) {
            player.performCommand("warp end");
        } else if (type.equals(Material.NETHERRACK)) {
            player.performCommand("warp nether");
        } else if (type.equals(Material.TRIPWIRE)) {
            player.performCommand("warp crates");
        } else if (type.equals(Material.DIAMOND_SWORD)) {
            player.performCommand("warp pvp");
        } else if (type.equals(Material.ENCHANTED_BOOK)) {
            player.performCommand("warp enchant");
        }
        player.closeInventory();
    }
}
