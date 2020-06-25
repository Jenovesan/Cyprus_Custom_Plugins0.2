package FactionUpgrades;

import Main.MainClass;
import XpBoost.XpBoostEvents;
import com.massivecraft.factions.FPlayers;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;

public class FactionUpgradesXpUpgrade implements Listener {
    MainClass mainClass;
    XpBoostEvents xpBoostEvents;
    public FactionUpgradesXpUpgrade(MainClass mc) {
        mainClass = mc;
        xpBoostEvents = new XpBoostEvents(mainClass);
    }
    @EventHandler
    private void entityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Blaze && event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        int ExtraAmount = 0;
        String FactionID = FPlayers.getInstance().getByPlayer(player).getFactionId();
        LoadXpUpgradeHashmapFromConfig();
        if (XpUpgrade.containsKey(FactionID)) {
            Integer level = XpUpgrade.get(FactionID);
            ExtraAmount = level * 2;
            xpBoostEvents.LoadXpBoostHashMapsInConfig();
            if (!(xpBoostEvents.doesNotHaveAnExpBoostCurrently(player))) {
                ExtraAmount = (int) (ExtraAmount + ((double) ExtraAmount / 100 * xpBoostEvents.XpBoostPerc.get(player.getUniqueId())));
            }
        }
        player.giveExp(ExtraAmount);
    }

    HashMap<String, Integer> XpUpgrade = new HashMap<>();
    private void LoadXpUpgradeHashmapFromConfig() {
        List<String> XpUpgradeStringFromConfig = mainClass.getConfig().getStringList("XpUpgrade");
        for (String string : XpUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            XpUpgrade.put(FactionId, Level);
        }
    }
}
