package FactionUpgrades;
import Main.MainClass;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.List;

public class FactionUpgradesFWarpUpgrade implements Listener {
    MainClass mainClass;
    public FactionUpgradesFWarpUpgrade(MainClass mc) {
        mainClass = mc;
    }
    @EventHandler
    private void test(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        Player player = event.getPlayer();
        if (command.contains("/f setwarp") && !canSetWarp(player)) {
            player.sendMessage(ChatColor.RED + "Your faction cannot have this many warps");
            event.setCancelled(true);
        }
    }

    private boolean canSetWarp(Player player) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        int warps = faction.getWarps().size();
        Integer MaxWarps = getMaxWarps(faction);
        return warps < MaxWarps;
    }

    private Integer getMaxWarps(Faction faction) {
        LoadFactionWarpsHashmapFromConfig();
        String FactionId = faction.getId();
        if (!FactionWarps.containsKey(FactionId)) {
            return 3;
        }
        Integer level = FactionWarps.get(FactionId);
        switch (level) {
            case 1:
                return 4;
            case 2:
                return 5;
            case 3:
                return 6;
            case 4:
                return 7;
            case 5:
                return 8;
            default:
                return 3;
        }
    }

    HashMap<String, Integer> FactionWarps = new HashMap<>();
    private void LoadFactionWarpsHashmapFromConfig() {
        List<String> FactionWarpsStringFromConfig = mainClass.getConfig().getStringList("FactionWarpsUpgrade");
        for (String string : FactionWarpsStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            FactionWarps.put(FactionId, Level);
        }
    }
}
