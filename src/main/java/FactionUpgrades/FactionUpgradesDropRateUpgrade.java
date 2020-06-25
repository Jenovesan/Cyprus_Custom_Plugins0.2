package FactionUpgrades;

import Main.MainClass;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FactionUpgradesDropRateUpgrade implements Listener {
    MainClass mainClass;
    public FactionUpgradesDropRateUpgrade(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void blockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        if (block.getType().equals(Material.MOB_SPAWNER)) {
            IncreaseSpawnerDropRate(FactionId, block);
        }
    }

    private void IncreaseSpawnerDropRate(String FactionId, Block block) {
        LoadDropRateUpgradeHashmapFromConfig();
        if (DropRateUpgrade.containsKey(FactionId)) {
            Integer level = DropRateUpgrade.get(FactionId);
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            spawner.setDelay((int) (spawner.getDelay() + (spawner.getDelay() * ((double) level / 20))));
        }
    }

    public void PurchasedDropRateUpgrade(String FactionId) {
        Set<FLocation> FactionLocations = Factions.getInstance().getFactionById(FactionId).getAllClaims();
        for (FLocation fLocation : FactionLocations) {
            Chunk chunk = Bukkit.getWorld(fLocation.getWorldName()).getChunkAt((int) fLocation.getX(), (int) fLocation.getZ());
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        Block block = chunk.getBlock(x,y,z);
                        if (block.getType().equals(Material.MOB_SPAWNER)) {
                            IncreaseSpawnerDropRate(FactionId, block);
                        }
                    }
                }
            }
        }
    }

    HashMap<String, Integer> DropRateUpgrade = new HashMap<>();
    private void LoadDropRateUpgradeHashmapFromConfig() {
        List<String> DropRateUpgradeStringFromConfig = mainClass.getConfig().getStringList("DropRateUpgrade");
        for (String string : DropRateUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            DropRateUpgrade.put(FactionId, Level);
        }
    }
}
