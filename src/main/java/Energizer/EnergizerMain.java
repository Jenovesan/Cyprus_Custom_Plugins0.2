package Energizer;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EnergizerMain implements Listener {
    MainClass mainClass;
    public EnergizerMain(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void RightClickedEnergy(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (itemStack != null && itemStack.getType().equals(Material.DOUBLE_PLANT) && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "" + ChatColor.BOLD + "Energizer")) {
            if (mainClass.classesBard.isInBardGear(player)) {
                mainClass.classesBard.BardEnergy.put(player, 60);
                player.playSound(player.getLocation(), Sound.WITHER_IDLE, 1, 1);
                ItemStack NewItem = new ItemStack(itemStack);
                NewItem.setAmount(NewItem.getAmount() - 1);
                if (NewItem.getAmount() > 0) {
                    player.setItemInHand(NewItem);
                } else {
                    player.setItemInHand(null);
                }
            } else {
                player.sendMessage(ChatColor.RED + "You must be in bard to use this item");
                event.setCancelled(true);
            }
        }
    }
}
