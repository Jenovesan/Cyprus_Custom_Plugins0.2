package FactionUpgrades;

import Main.MainClass;
import com.massivecraft.factions.FPlayers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;

public class FactionUpgradesDamageReduction implements Listener {
    MainClass mainClass;
    public FactionUpgradesDamageReduction(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void entityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (FPlayers.getInstance().getByPlayer(player).getFactionId() == null) {
            return;
        }
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        LoadDamageReductionUpgradeHashmapFromConfig();
        if (DamageReductionUpgrade.containsKey(FactionId)) {
            double NewDamage = (event.getFinalDamage() / 100) * (100 - DamageReductionUpgrade.get(FactionId) * 3);
            event.setDamage(NewDamage);
        }
    }

    HashMap<String, Integer> DamageReductionUpgrade = new HashMap<>();
    private void LoadDamageReductionUpgradeHashmapFromConfig() {
        List<String> DamageReductionUpgradeStringFromConfig = mainClass.getConfig().getStringList("DamageReductionUpgrade");
        for (String string : DamageReductionUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            DamageReductionUpgrade.put(FactionId, Level);
        }
    }

}
