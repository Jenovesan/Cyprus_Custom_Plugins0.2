package AutoSellGem;

import CustomItems.CustomItemsMain;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class AutoSellGemMain extends CommandExecute implements Listener, CommandExecutor {
    CustomItemsMain customItemsMain = new CustomItemsMain();
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                try {
                    Player TargetPlayer = Bukkit.getPlayerExact(args[0]);
                    if (TargetPlayer != null) {
                        GiveGem(TargetPlayer);
                        return true;
                    }
                }
                catch (Exception e) {}
                sender.sendMessage(ChatColor.GRAY + "Correct Usage: /autosellgem <Player Name>");
            }
        }
        else {
            sender.sendMessage(ChatColor.GRAY + "Correct Usage: /autosellgem <Player Name>");
        }
        return false;
    }

    private void GiveGem(Player player) {
        utilityMain.GivePlayerItem(player, customItemsMain.AutoSellGem(), 1);
    }

    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || event.getSlot() == -999) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem().getType().equals(Material.AIR) || event.getCursor().getType().equals(Material.AIR)) {
            return;
        }
        ItemStack CursorItem = event.getCursor();
        ItemStack CurrentItem = event.getCurrentItem();
        if (CurrentItem == null || CursorItem == null) {
            return;
        }
        if (CursorItem.hasItemMeta()) {
            if (CursorItem.getItemMeta().hasDisplayName()) {
                if (CursorItem.getItemMeta().getDisplayName().equals(AutoSellGemName)) {
                    if (isHarvesterHoe(CurrentItem)) { //Items compatible with auto sell gem
                        player.setItemOnCursor(null);
                        AddGemLore(CurrentItem);
                        event.setCancelled(true);
                        AddedGemSuccessEffect(player);
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "This item is not compatible with auto sell gem");
                    }
                }
            }
        }
    }

    private String AutoSellGemName = (ChatColor.GREEN + "" + ChatColor.BOLD + "Auto Sell Gem");

    private void AddedGemSuccessEffect(Player player) {
        for (int i = 0; i < 15; i++) {
            player.getWorld().playEffect(player.getEyeLocation(), Effect.HAPPY_VILLAGER, 1);
        }
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
    }

    private void AddGemLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(ChatColor.COLOR_CHAR + ".");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Auto Sell");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private boolean isHarvesterHoe(ItemStack item) {
        if (item != null) {
            if (item.getType().equals(Material.DIAMOND_HOE)) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().getDisplayName().contains(ChatColor.RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Harvester Hoe")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
