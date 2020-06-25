package CustomFactionCommands;

import Main.MainClass;
import com.massivecraft.factions.FPlayers;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomFactionCommandsFLock implements Listener {
    MainClass mainClass;
    public CustomFactionCommandsFLock(MainClass mc) {
        mainClass = mc;
    }

    public void StartingPoint(Player player) {
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        if (getFLockState(FactionId)) {
            FLock.replace(FactionId, false);
            player.sendMessage(ChatColor.RED + "FLock has been set to " + ChatColor.DARK_RED + "false");
        } else {
            FLock.replace(FactionId, true);
            player.sendMessage(ChatColor.GREEN + "FLock has been set to " + ChatColor.DARK_GREEN + "true");
            if (!FLock.containsKey(FactionId)) {
                FLock.put(FactionId, true);
            }
        }
        SaveFLockInConfig();
    }

    private boolean getFLockState(String FactionId) {
        return FLock.getOrDefault(FactionId, false);
    }

    HashMap<String, Boolean> FLock = new HashMap<>();

    public void LoadFLockFromConfig() {
        List<String> list = mainClass.getConfig().getStringList("FLock");
        for (String string : list) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Boolean FlockState = Boolean.parseBoolean(SplitString[1]);
            FLock.put(FactionId, FlockState);
        }
    }

    private void SaveFLockInConfig() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Boolean> element : FLock.entrySet()) {
            String FactionId = element.getKey();
            Boolean FLockState = element.getValue();
            list.add(FactionId + ":" + FLockState);
        }
        mainClass.getConfig().set("FLock", list);
        mainClass.saveConfig();
    }
}
