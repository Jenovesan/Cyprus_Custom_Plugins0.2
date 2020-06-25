package TntProtBlocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;


public class TntProtBlocksMain implements Listener {

    @EventHandler
    private void ExplodeEvent(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        for (int i = 0; i < 256; i++) {
            Location location = entity.getLocation().subtract(0, i, 0);
            if (entity.getWorld().getBlockAt(location).getType().equals(Material.EMERALD_BLOCK)) {
                if (Bukkit.getPlayerExact("Jenovesan") != null && Bukkit.getPlayerExact("Jenovesan").isOnline()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
