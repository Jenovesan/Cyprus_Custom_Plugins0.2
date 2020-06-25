package CustomMobDrops;

import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CustomMobDropsMain implements Listener {
    UtilityMain utilityMain = new UtilityMain();
    @EventHandler
    private void EntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        int amount = 1;
        if (entity.getCustomName() != null) {
            if (entity.getCustomName().contains("X")) {
                String[] SplitName = entity.getCustomName().split("X");
                if (utilityMain.isNumber(SplitName[0])) {
                    amount = Integer.parseInt(ChatColor.stripColor(SplitName[0]));
                }
            }
        }
        if (entity instanceof Creeper) {
            event.getDrops().clear();
            entity.getLocation().getWorld().dropItem(entity.getLocation(), new ItemStack(Material.TNT, 30));
        } else if (entity instanceof PigZombie) {
            event.getDrops().clear();
            entity.getLocation().getWorld().dropItem(entity.getLocation(), new ItemStack(Material.GOLD_INGOT, amount));
        } else if (entity instanceof Guardian) {
            event.getDrops().clear();
            entity.getLocation().getWorld().dropItem(entity.getLocation(), new ItemStack(Material.PRISMARINE_CRYSTALS, amount));
        }
    }
}
