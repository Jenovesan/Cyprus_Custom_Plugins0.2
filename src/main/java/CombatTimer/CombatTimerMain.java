package CombatTimer;

import Main.MainClass;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatTimerMain implements Listener {
    MainClass mainClass;
    public CombatTimerMain(MainClass mc) {
        mainClass = mc;
    }
    public Integer CombatTimeInSeconds = 30;

    @EventHandler(priority = EventPriority.HIGH)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if(!(event.getDamager() instanceof Player)) {
            if(event.getDamager() instanceof Arrow) {
                if(!((((Arrow) event.getDamager()).getShooter()) instanceof Player)) {
                    return;
                }
            } else {
                return;
            }
        }
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damagee = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!damagee.isOp()) {
            addHash(damagee);
        }
        if (!damager.isOp()) {
            addHash(damager);
        }
    }

    public HashMap<UUID, Integer> damageTracking = new HashMap<>();
    private void addHash(Player player) {
        if(damageTracking.containsKey(player.getUniqueId())) {
            damageTracking.replace(player.getUniqueId(), CombatTimeInSeconds);
        } else {
            damageTracking.put(player.getUniqueId(), CombatTimeInSeconds);
        }
    }

    private Integer getTime(Player player) {
        return damageTracking.getOrDefault(player.getUniqueId(), -1);
    }

    public boolean isCombatTagged(Player player) {
        return damageTracking.containsKey(player.getUniqueId());
    }

    public void CombatTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Integer> element : damageTracking.entrySet()) {
                    Integer time = element.getValue();
                    UUID uuid = element.getKey();
                    int NewTime = time - 1;
                    if (NewTime <= 0) {
                        SendOutOfCombatMessage(Bukkit.getPlayer(uuid));
                        damageTracking.remove(uuid);
                    } else {
                        damageTracking.replace(uuid, NewTime);
                        if (NewTime <= 5) {
                            SendTimeMessag(Bukkit.getPlayer(uuid));
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);
    }

    private void SendTimeMessag(Player player) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§c" + getTime(player) + "s until you are out of combat" + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }

    private void SendOutOfCombatMessage(Player player) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§aYou are out of combat\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isCombatTagged(player)) {
            player.setHealth(0);
            damageTracking.remove(player.getUniqueId());
        }
    }

    List<String> CommandsAllowedInCombat = new ArrayList<>(Arrays.asList("/feed", "/msg", "/f setrally"));
    @EventHandler
    private void sendsCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        if (CommandsAllowedInCombat.contains(command)) {
            return;
        }
        if (isCombatTagged(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are in combat for " + ChatColor.DARK_RED + getTime(player) + "s");
        }
    }

    @EventHandler
    private void RemovePlayerFromCombatTagWhenDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        damageTracking.remove(player.getUniqueId());
    }
}
