package Teleport;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TeleportMain implements Listener {
    MainClass mainClass;
    public TeleportMain(MainClass mc) {
        mainClass = mc;
    }

    HashMap<Player, Integer> TeleportingPlayers = new HashMap<>();
    HashMap<Player, Location> TeleportingPlayersLocations = new HashMap<>();
    public void TeleportPlayer(Player player, Location location) {
        TeleportingPlayers.put(player, 6);
        TeleportingPlayersLocations.put(player, location);
        player.sendMessage(ChatColor.GREEN + "Teleporting...");
    }

    public void TeleportingPlayersTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Integer> element : TeleportingPlayers.entrySet()) {
                    if (element.getValue() < 1) {
                        element.getKey().teleport(TeleportingPlayersLocations.get(element.getKey()));
                        TeleportingPlayers.remove(element.getKey());
                        TeleportingPlayersLocations.remove(element.getKey());
                    } else {
                        TeleportingPlayers.put(element.getKey(), element.getValue() - 1);
                    }
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 20);
    }

    HashMap<Player, Location> OriginalLocation = new HashMap<>();
    public void CancelTeleportIfMoves() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Location> element : OriginalLocation.entrySet()) {
                    Player player = element.getKey();
                    Location last = element.getValue();
                    if (player.getLocation().distance(last) > 0.25) {
                        TeleportingPlayers.remove(player);
                        TeleportingPlayersLocations.remove(player);
                        OriginalLocation.remove(player);
                        player.sendMessage(ChatColor.RED + "You teleportation has been cancelled");
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 10);
    }
}
