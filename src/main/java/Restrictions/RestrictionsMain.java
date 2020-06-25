package Restrictions;

import Main.MainClass;
import Roam.RoamMain;
import Staff.StaffMain;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class RestrictionsMain implements Listener {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    StaffMain staffMain = new StaffMain();
    RoamMain roamMain;
    public RestrictionsMain(MainClass mc) {
        mainClass = mc;
        roamMain = mainClass.roamMain;
    }

    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType().equals(Material.SKULL)) {
            Skull skull = (Skull) block.getState();
            if (skull.getSkullType().equals(SkullType.WITHER)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot place this item");
            }
        }
        if (block.getType().equals(Material.BREWING_STAND)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place this item");
        }
    }

    @EventHandler
    private void illegalAnvilItem(InventoryClickEvent event) {
        if (event.getSlot() == -999) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) {
            return;
        }
        if (illegalClick(event.getClick(), event.getCurrentItem(), event.getInventory())) {
            player.sendMessage(ChatColor.RED + "Sorry but you cannot shift click items or alter special items in an anvil");
            event.setCancelled(true);
        }
    }

    private boolean illegalClick(ClickType clickType, ItemStack item, Inventory inventory) {
        if (!inventory.getType().equals(InventoryType.ANVIL)) {
            return false;
        }
        if (clickType.equals(ClickType.SHIFT_LEFT) || clickType.equals(ClickType.SHIFT_RIGHT)) {
            return true;
        }
        return inventory.getType().equals(InventoryType.ANVIL) && SpecialName(item);
    }

    private boolean SpecialName(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        return !item.getItemMeta().getDisplayName().equals(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
    }

    @EventHandler
    private void CannotFly(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        if (command.contains("fly") && utilityMain.enemyIsNearby(player, 50) && !staffMain.isStaff(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot fly because an enemy is nearby");
        }
    }

    public void DisableFlyWhenEnemiesNearby() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isFlying() && utilityMain.enemyIsNearby(player, 50) && !staffMain.isStaff(player) && !mainClass.roamMain.isInRoam(player)) {
                        FPlayers.getInstance().getByPlayer(player).setFlying(false);
                        player.sendMessage(ChatColor.RED + "Your flight has been disabled due to an enemy being nearby");
                    }
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 20);
    }
}
