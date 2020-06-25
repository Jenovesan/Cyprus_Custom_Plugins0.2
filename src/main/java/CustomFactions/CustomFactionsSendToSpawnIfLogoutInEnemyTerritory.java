package CustomFactions;

import Main.MainClass;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CustomFactionsSendToSpawnIfLogoutInEnemyTerritory implements Listener {
    MainClass mainClass;
    public CustomFactionsSendToSpawnIfLogoutInEnemyTerritory(MainClass mc) {
        mainClass = mc;
    }
    @EventHandler
    private void SendPlayerToSpawn(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        Faction FactionAtLocation = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        if (!FactionAtLocation.equals(faction) && !FactionAtLocation.isWilderness() && !mainClass.customFactionsCommandsFAlts.isAFAlt(player) && !FactionAtLocation.isWarZone() && !FactionAtLocation.isSafeZone() && !player.isOp()) {
            if (Bukkit.getWorld("FireRealm") != null) {
                player.teleport(Bukkit.getWorld("FireRealm").getSpawnLocation());
            } else {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }
}
