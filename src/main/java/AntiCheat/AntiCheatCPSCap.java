package AntiCheat;

import Main.MainClass;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AntiCheatCPSCap implements Listener {
    Integer MaxCPS = 18;
    MainClass mainClass;
    public AntiCheatCPSCap(MainClass mc) {
        mainClass = mc;
    }

    HashMap<Player, Integer> PlayerClicks = new HashMap<>();
    @EventHandler
    private void GatherCPS(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        Player player = event.getPlayer();
        Integer clicks = PlayerClicks.getOrDefault(player, 0);
        PlayerClicks.put(player, clicks + 1);
    }

    public void CPSCapTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Integer> element : PlayerClicks.entrySet()) {
                    Player player = element.getKey();
                    int cps = element.getValue();
                    if (cps >= MaxCPS) {
                        player.kickPlayer(ChatColor.RED + "You are clicking too fast");
                    }
                    PlayerClicks.put(player, 0);
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 20);
    }
}
