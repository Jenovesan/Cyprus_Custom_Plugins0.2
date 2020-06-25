package AfkCheck;

import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AfkCheckMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public AfkCheckMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("cyprus.afkcheck")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return true;
        }
        if (args.length == 1 && Bukkit.getPlayerExact(args[0]) != null) {
            Player TargetPlayer = Bukkit.getPlayerExact(args[0]);
            sender.sendMessage(playerIsAfk(TargetPlayer) + " " + utilityMain.FormatTime((int) ((System.currentTimeMillis() - LastTimeSinceInteracted.get(TargetPlayer)) / 60000)));
            return true;
        }
        return true;
    }

    private Boolean playerIsAfk(Player player) {
        if (LastTimeSinceInteracted.containsKey(player)) {
            return System.currentTimeMillis() - 60000 > LastTimeSinceInteracted.get(player);
        }
        return false;
    }

    @EventHandler
    private void ReplaceLastTimeSinceInteractedIfInteracted(PlayerInteractEvent event) {
        LastTimeSinceInteracted.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    private void AddPlayerToHashMaps(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LastTimeSinceInteracted.put(player, System.currentTimeMillis());
        PlayersLastLocation.put(player, player.getLocation());
    }

    HashMap<Player, Long> LastTimeSinceInteracted = new HashMap<>();

    HashMap<Player, Location> PlayersLastLocation = new HashMap<>();
    public void CheckIfMoved() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Location> element : PlayersLastLocation.entrySet()) {
                    Player player = element.getKey();
                    Location LastLocation = element.getValue();
                    if (!player.isOnline()) {
                        if (player.isOnline() && !player.getLocation().equals(LastLocation)) {
                            LastTimeSinceInteracted.put(player, System.currentTimeMillis());
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 200);
    }

}
