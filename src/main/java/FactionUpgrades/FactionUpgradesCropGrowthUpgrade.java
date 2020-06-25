package FactionUpgrades;

import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.HashMap;
import java.util.List;

public class FactionUpgradesCropGrowthUpgrade implements Listener {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public FactionUpgradesCropGrowthUpgrade(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void cropGrowth(BlockGrowEvent event) {
        Block block = event.getBlock();
        if (!(block.getLocation().subtract(0,1,0).getBlock().getType().equals(Material.SUGAR_CANE_BLOCK))) {
            return;
        }
        int MaxGrowthHeight = getGrowthHeight(block);
        for (int i = 1; i < (MaxGrowthHeight + 1); i++) {
            Location NewCaneLocation = block.getLocation().add(0, i,0);
            if (getCaneStockHeight(NewCaneLocation) > 4) {
                return;
            }
            if (!(NewCaneLocation.getBlock().getType().equals(Material.AIR))) {
                return;
            }
            NewCaneLocation.getBlock().setType(Material.SUGAR_CANE_BLOCK);
        }
    }

    private Integer getGrowthHeight(Block block) {
        FLocation BlockLocation = new FLocation(block.getLocation());
        Faction faction = Board.getInstance().getFactionAt(BlockLocation);
        String FactionId = faction.getId();
        LoadCropGrowthUpgradeHashmapFromConfig();
        if (!(CropGrowthUpgrade.containsKey(FactionId))) {
            return 0;
        }
        Integer level = CropGrowthUpgrade.get(FactionId);
        switch (level) {
            case 1:
                if (utilityMain.RandomNumberBoolean(4, 1)) {
                    return 1;
                }
            case 2:
                if (utilityMain.RandomNumberBoolean(10, 6)) {
                    return 1;
                }
            case 3:
                return 1;
            case 4:
                if (utilityMain.RandomNumberBoolean(1,2)) {
                    return 2;
                }
                return 1;
            case 5:
                return 2;
        }
        return 0;
    }


    private Integer getCaneStockHeight(Location location) {
        Integer Height = 0;
        for (int i = 4; i > 0; i--) {
            if (location.subtract(0,1,0).getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
                Height++;
            }
            else {
                break;
            }
        }
        return Height;
    }

    HashMap<String, Integer> CropGrowthUpgrade = new HashMap<>();
    private void LoadCropGrowthUpgradeHashmapFromConfig() {
        List<String> CropGrowthUpgradeStringFromConfig = mainClass.getConfig().getStringList("CropGrowthUpgrade");
        for (String string : CropGrowthUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            CropGrowthUpgrade.put(FactionId, Level);
        }
    }
}
