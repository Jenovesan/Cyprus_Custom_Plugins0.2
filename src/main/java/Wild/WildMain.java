package Wild;

import CyprusTime.CyprusTimeMain;
import Main.MainClass;
import Teleport.TeleportMain;
import Utility.UtilityMain;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WildMain extends CommandExecute implements CommandExecutor {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public WildMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!WildCooldown.containsKey(((Player) sender).getUniqueId()) && mainClass.cyprusTimeMain.time != CyprusTimeMain.Time.SOTW) {
            mainClass.teleportMain.TeleportPlayer((Player) sender, RandomWildLocation());
            WildCooldown.put(((Player) sender).getUniqueId(), 60);
        } else {
            sender.sendMessage(ChatColor.RED + "Your /wild cooldown is on " + ChatColor.DARK_RED + utilityMain.FormatTime(WildCooldown.get(((Player) sender).getUniqueId())));
        }
        return false;
    }

    private Location RandomWildLocation() {
        Location location = GenerateNewLocation();
        for (int i = 0; i < 100; i++) {
            FLocation fLocation = new FLocation(location);
            if (!Board.getInstance().getFactionAt(fLocation).isWilderness()) {
                location = GenerateNewLocation();
            } else {
                break;
            }
        }
        for (int i =  255; i > 40; i--) {
            if (location.getBlock().getType().equals(Material.AIR)) {
                location.setY(location.getY() - 1);
            } else {
                location.setY(location.getY() + 1);
                return location;
            }
        }
        return location;
    }

    private Location GenerateNewLocation() {
        int x = utilityMain.getRandom().nextInt(2000) - 1000;
        int z = utilityMain.getRandom().nextInt(2000) - 1000;
        return new Location(Bukkit.getWorld("FireRealm"), x, 256, z);
    }

    HashMap<UUID, Integer> WildCooldown = new HashMap<>();
    public void WildCooldownTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Integer> element : WildCooldown.entrySet()) {
                    if (element.getValue() > 1) {
                        WildCooldown.put(element.getKey(), element.getValue() - 1);
                    } else {
                        WildCooldown.remove(element.getKey());
                    }
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 1200);
    }
}
