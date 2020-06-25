package AntiCheat;

import Main.MainClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AntiCheatLongTermAutoClicker implements Listener {
    Integer MaxCPS = 20;
    MainClass mainClass;
    public AntiCheatLongTermAutoClicker(MainClass mc) {
        mainClass = mc;
    }

    HashMap<Player, Integer> PlayerClicks = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    private void GatherCPS(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Blaze)) {
            return;
        }
        if (!CollectCPS) {
            return;
        }
        Player player = (Player) event.getDamager();
        Integer clicks = PlayerClicks.getOrDefault(player, 0);
        PlayerClicks.put(player, clicks + 1);
    }

    public void LongTermCPSCapTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                CollectCPSForASecond();
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 1200, 1200);
    }

    Boolean CollectCPS = false;
    Integer TimesCollectedCPS = 0;
    HashMap<Player, Integer> WasClickingOver10CPS = new HashMap<>();
    private void CollectCPSForASecond() {
        TimesCollectedCPS++;
        CollectCPS = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                CollectCPS = false;
                for (Map.Entry<Player, Integer> element : PlayerClicks.entrySet()) {
                    Player player = element.getKey();
                    Integer clicks = element.getValue();
                    if (clicks > 9) {
                        Integer times = WasClickingOver10CPS.getOrDefault(player, 0);
                        WasClickingOver10CPS.put(player, times + 1);
                    }
                }
                PlayerClicks.clear();
                if (TimesCollectedCPS >= 10) {
                    CheckData();
                    TimesCollectedCPS = 0;
                }
            }
        }.runTaskLaterAsynchronously(mainClass, 20);
    }

    private void CheckData() {
        for (Map.Entry<Player, Integer> element : WasClickingOver10CPS.entrySet()) {
            if (WasClickingOver10CPS.get(element.getKey()) >= 10) {
                element.getKey().kickPlayer(ChatColor.RED + "Kicked for auto clicking");
            }
        }
    }
}
