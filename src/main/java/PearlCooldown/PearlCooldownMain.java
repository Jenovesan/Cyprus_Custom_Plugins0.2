package PearlCooldown;

import Main.MainClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PearlCooldownMain implements Listener {
    Integer PearlCooldownTime = 15;
    MainClass mainClass;
    public PearlCooldownMain(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void TestsIfAPlayerThrewAPearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && player.getItemInHand() != null && player.getItemInHand().getType().equals(Material.ENDER_PEARL)) {
            if (getPearlCooldownTime(player) != -1) {
                player.sendMessage(ChatColor.RED + "Pearl Cooldown: " + getPearlCooldownTime(player));
                player.setItemInHand(player.getItemInHand());
                event.setCancelled(true);
            } else {
                AddTime(player);
            }
        }
    }

    HashMap<UUID, Integer> PearlCooldown = new HashMap<>();
    private void AddTime(Player player) {   PearlCooldown.put(player.getUniqueId(), PearlCooldownTime);}

    private Integer getPearlCooldownTime(Player player) {return PearlCooldown.getOrDefault(player.getUniqueId(), -1);}

    public void PearlCooldownTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Integer> element : PearlCooldown.entrySet()) {
                    Integer time = element.getValue();
                    UUID uuid = element.getKey();
                    if (time - 1 <= 0) {
                        PearlCooldown.remove(uuid);
                    } else {
                        PearlCooldown.replace(uuid, time - 1);
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);
    }
}
