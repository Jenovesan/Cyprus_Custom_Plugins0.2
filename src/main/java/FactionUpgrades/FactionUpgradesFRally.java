package FactionUpgrades;

import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionUpgradesFRally implements Listener {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public FactionUpgradesFRally(MainClass mc) {
        mainClass = mc;
    }
    public void StartingPoint(Player player, String[] args, String command) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        if (!utilityMain.isInFaction(player)) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this command");
            return;
        }
        if (command.contains("/f rally")) {
            if (args.length == 0) {
                AttemptToTpToRally(player); 
            } else if (args[0].equalsIgnoreCase("cooldown")) {
                TellPlayerCooldown(player);
            } else {
                player.sendMessage(HelpMessage);
            }
        }
        else if (command.contains("/f setrally")) {
            if (!isOnCooldown(faction) && !RallyLocation.containsKey(faction.getId())) {
                SetRally(player);
            } else {
                player.sendMessage(ChatColor.RED + "Cannot set rally");
            }
        }
    }

    private void TellPlayerCooldown(Player player) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        String FactionId = faction.getId();
        LoadRallyCooldownFromConfig();
        if (RallyCooldown.containsKey(FactionId)) {
            Integer Cooldown = RallyCooldown.get(FactionId);
            player.sendMessage(ChatColor.RED + "Faction Rally Cooldown: " + Cooldown + "h");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Faction rally is not on cooldown");
    }

    private void AttemptToTpToRally(Player player) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        String FactionId = faction.getId();
        LoadRallyLocationFromConfig();
        if (!RallyLocation.containsKey(FactionId)) {
            player.sendMessage(ChatColor.RED + "Your faction does not have a rally set");
            return;
        }
        Location location = RallyLocation.get(FactionId);
        mainClass.teleportMain.TeleportPlayer(player, location);
    }

    private String HelpMessage = ChatColor.GRAY + "f rally usage:" + "\n" + "/f setrally - sets your faction's rally point" + "\n" + "/f rally - teleports you to your faction's rally point" + "\n" + "/f rally cooldown - says the cooldown your faction's rally is on";

    private void SetRally(Player player) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        for (Player OnlineFactionMember : faction.getOnlinePlayers()) {
            OnlineFactionMember.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Your faction's rally point has been set!");
            OnlineFactionMember.playSound(OnlineFactionMember.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
        }
        Location location = player.getLocation();
        location.getWorld().playSound(location, Sound.ENDERDRAGON_GROWL, 1, 1);
        int cooldown = 24; //hours
        RallyCooldown.put(faction.getId(), cooldown);
        RallyLocation.put(faction.getId(), location);
        RallyUse.put(faction.getId(), getRallyUseLength(faction));
        SaveRallyUseInConfig();
        SaveRallyCooldownInConfig();
        SaveRallyLocationInConfig();
    }

    private Integer getRallyUseLength(Faction faction) {
        String FactionId = faction.getId();
        LoadFactionRallyUpgradeHashmapFromConfig();
        if (!FactionRallyUpgrade.containsKey(FactionId)) {
            return 3;
        }
        Integer level = FactionRallyUpgrade.get(FactionId);
        switch (level) {
            case 1:
                return 4;
            case 2:
                return 5;
            case 3:
                return 6;
            case 4:
                return 7;
            default:
                return 8;
        }
    }

    public void RallyCooldownTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                LoadRallyCooldownFromConfig();
                if (!RallyCooldown.isEmpty()) {
                    for (Map.Entry element : RallyCooldown.entrySet()) {
                        String FactionId = (String) element.getKey();
                        Integer Cooldown = (Integer) element.getValue();
                        if (Cooldown > 0) {
                            RallyCooldown.replace(FactionId, Cooldown - 1);
                        }
                        if (Cooldown == 0) {
                            RallyCooldown.remove(FactionId);
                            SaveRallyLocationInConfig();
                            Faction faction = Factions.getInstance().getFactionById(FactionId);
                            utilityMain.MessageFactionMembers(faction, ChatColor.GREEN + "" + ChatColor.BOLD + "Your faction's rally is no longer on cooldown");
                        }
                    }
                    SaveRallyCooldownInConfig();
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 72000); //Should be 72,000
    }

    public void RallyUseTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                LoadRallyUseFromConfig();
                if (!RallyUse.isEmpty()) {
                    for (Map.Entry element : RallyUse.entrySet()) {
                        String FactionId = (String) element.getKey();
                        Integer Cooldown = (Integer) element.getValue();
                        if (Cooldown > 0) {
                            RallyUse.replace(FactionId, Cooldown - 1);
                        }
                        if (Cooldown == 0) {
                            RallyUse.remove(FactionId);
                            RallyLocation.remove(FactionId);
                            SaveRallyLocationInConfig();
                            utilityMain.MessageFactionMembers(Factions.getInstance().getFactionById(FactionId), ChatColor.RED + "" + ChatColor.BOLD + "Your faction's rally has ended!");
                        }
                    }
                    SaveRallyUseInConfig();
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 1200); //Should be 1,200
    }

    HashMap<String, Integer> RallyUse = new HashMap<>();
    private void SaveRallyUseInConfig() {
        try {
            List<String> list = new ArrayList<>();
            for (Map.Entry element : RallyUse.entrySet()) {
                String FactionId = (String) element.getKey();
                String time = String.valueOf(element.getValue());
                list.add(FactionId + ":" + time);
            }
            mainClass.getConfig().set("RallyUse", list);
            mainClass.saveConfig();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + e.toString());
        }
    }

    private void LoadRallyUseFromConfig() {
        List<String> list = mainClass.getConfig().getStringList("RallyUse");
        RallyUse = new HashMap<>();
        for (String string : list) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer time = Integer.parseInt(SplitString[1]);
            RallyUse.put(FactionId, time);
        }
    }

    HashMap<String, Integer> FactionRallyUpgrade = new HashMap<>();
    private void LoadFactionRallyUpgradeHashmapFromConfig() {
        List<String> FactionRallyUpgradeStringFromConfig = mainClass.getConfig().getStringList("FactionRallyUpgrade");
        FactionRallyUpgrade = new HashMap<>();
        for (String string : FactionRallyUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            FactionRallyUpgrade.put(FactionId, Level);
        }
    }

    HashMap<String, Integer> RallyCooldown = new HashMap<>();
    private void LoadRallyCooldownFromConfig() {
        List<String> ListFromConfig = mainClass.getConfig().getStringList("RallyCooldown");
        RallyCooldown = new HashMap<>();
        for (String string : ListFromConfig) {
            String[] SplitString = string.split(":");
            RallyCooldown.put(SplitString[0], Integer.parseInt(SplitString[1]));
        }
    }

    HashMap<String, Location> RallyLocation = new HashMap<>();
    private void LoadRallyLocationFromConfig() {
        List<String> ListFromConfig = mainClass.getConfig().getStringList("RallyLocation");
        RallyLocation = new HashMap<>();
        for (String string : ListFromConfig) {
            String[] SplitString = string.split(":");
            RallyLocation.put(SplitString[0], new Location(Bukkit.getWorld(SplitString[1]), Double.parseDouble(SplitString[2]), Double.parseDouble(SplitString[3]), Double.parseDouble(SplitString[4])));
        }
    }

    private void SaveRallyLocationInConfig() {
        try {
            List<String> list = new ArrayList<>();
            for (Map.Entry element : RallyLocation.entrySet()) {
                String FactionId = (String) element.getKey();
                Location location = (Location) element.getValue();
                String WorldName = location.getWorld().getName();
                String x = String.valueOf(location.getX());
                String y = String.valueOf(location.getY());
                String z = String.valueOf(location.getZ());
                list.add(FactionId + ":" + WorldName + ":" + x + ":" + y + ":" + z);
            }
            mainClass.getConfig().set("RallyLocation", list);
            mainClass.saveConfig();

        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + e.toString());
        }
    }

    private void SaveRallyCooldownInConfig() {
       try {
           List<String> RallyCooldownString = new ArrayList<>();
           for (Map.Entry element : RallyCooldown.entrySet()) {
               String FactionId = (String) element.getKey();
               String Cooldown = String.valueOf(element.getValue());
               RallyCooldownString.add(FactionId + ":" + Cooldown);
           }
           mainClass.getConfig().set("RallyCooldown", RallyCooldownString);
           mainClass.saveConfig();
       } catch (Exception e) {
           Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + e.toString());
       }
    }

    private Boolean isOnCooldown(Faction faction) {
        String TargetFactionId = faction.getId();
        LoadRallyCooldownFromConfig();
        return RallyCooldown.containsKey(TargetFactionId);
    }
}
