package AntiCheat;

import Main.MainClass;
import Staff.StaffMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AntiCheatNukerAndKarma implements Listener {
    StaffMain staffMain = new StaffMain();
    MainClass mainClass;
    public AntiCheatNukerAndKarma(MainClass mc) {
        mainClass = mc;
    }

    HashMap<Player, Integer> CaneBroken = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    private void BreakCane(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK) && event.getBlock().getLocation().subtract(0,1,0).getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
            if (CaneBroken.containsKey(player)) {
                Integer amount = CaneBroken.get(player);
                CaneBroken.replace(player, amount + 1);
            } else {
                CaneBroken.put(player, 1);
            }
        }
    }

    public void AntiCheatNukeAndKarmaRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Integer> element : CaneBroken.entrySet()) {
                    Player player = element.getKey();
                    Integer amount = element.getValue();
                    if (amount > 119) {
                        staffMain.SendStaffMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " has been kicked for breaking too much cane " + ChatColor.DARK_RED + "(" + amount + ")");
                        player.kickPlayer(ChatColor.RED + "You are breaking cane too fast");
                    }
                    CaneBroken.remove(player);
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 100);
    }
}
