package Classes;

import Main.MainClass;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ClassesArcher implements Listener {
    MainClass mainClass;
    public ClassesArcher(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void AddOrRemoveArcherSpeed(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (isInArcherGear(player)) {
            player.setWalkSpeed((float) 0.24);
        } else {
            player.setWalkSpeed((float) .2);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void TagPlayer(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (TaggedPlayers.containsKey(event.getEntity())) {
            event.setDamage(event.getDamage() * 1.25);
        }
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }
        Player damager = (Player) arrow.getShooter();
        Player damagee = (Player) event.getEntity();
        if (isInArcherGear(damager)) {
            TaggedPlayers.put(damagee, 10);
            mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "lp user " + damagee.getName() + " parent add archer");
        }
    }

    @EventHandler
    private void RemoveSpeedWhenGearBreaks(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if (!isInArcherGear(player)) {
            player.setWalkSpeed((float) 0.2);
        }
    }

    HashMap<Player, Integer> TaggedPlayers = new HashMap<>();

    public void TaggedPlayersManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Integer> element : TaggedPlayers.entrySet()) {
                    Player player = element.getKey();
                    Integer time = element.getValue();
                    player.sendMessage(String.valueOf(time));
                    if (time > 1) {
                        TaggedPlayers.replace(player, time - 1);
                    } else {
                        TaggedPlayers.remove(player);
                        mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "lp user " + player.getName() + " parent remove archer");
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);
    }

    @EventHandler
    private void RemoveSpeedWhenArmorBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if (!isInArcherGear(player)) {
            player.setWalkSpeed((float) 0.2);
        }
    }

    private boolean isInArcherGear(Player player) {
        for (ItemStack gear : player.getInventory().getArmorContents()) {
            if (!gear.getType().toString().contains("LEATHER")) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    private void RemoveArcherLpWhenConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "lp user " + player.getName() + " parent remove archer");
    }
}
