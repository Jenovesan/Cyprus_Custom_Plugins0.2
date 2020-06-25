package CustomFactionCommands;

import Main.MainClass;
import Staff.StaffMain;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.perms.Role;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.*;
import java.util.ArrayList;


public class CustomFactionCommandsFRoster implements Listener {
    Integer MaxRosterSize = 25;
    MainClass mainClass;
    StaffMain staffMain = new StaffMain();
    public CustomFactionCommandsFRoster(MainClass mc) {
        mainClass = mc;
    }

    public void StartingPoint(Player player, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            SendPlayerFRosterNames(player);
            return;
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("add") && Bukkit.getPlayerExact(args[1]) != null && FPlayers.getInstance().getByPlayer(player).getRole().isAtLeast(Role.MODERATOR)){
            AddPlayerToFRoster(player, Bukkit.getPlayerExact(args[1]));
            return;
        } if (staffMain.isStaff(player)) {
            if (args.length == 3 && args[0].equalsIgnoreCase("remove") && Bukkit.getOfflinePlayer(args[1]) != null) {
                if (Factions.getInstance().getByTag(args[2]) != null) {
                    OfflinePlayer TargetPlayer = Bukkit.getOfflinePlayer(args[1]);
                    RemovePlayerFromFRoster(player, TargetPlayer, Factions.getInstance().getByTag(args[2]));
                } else {
                    player.sendMessage(ChatColor.DARK_RED + args[2] + " is not a faction");
                }
                return;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("enable")) {
                    FRosterEnabled = true;
                    SaveFRosterEnabledInConfig();
                    player.sendMessage(ChatColor.GREEN + "F Roster has been " + ChatColor.DARK_GREEN + "enabled");
                    return;
                } else if (args[0].equalsIgnoreCase("disable")) {
                    FRosterEnabled = false;
                    SaveFRosterEnabledInConfig();
                    player.sendMessage(ChatColor.RED + "F Roster has been " + ChatColor.DARK_RED + "disabled");
                    return;
                }
            }
        }
        SendUsageMessage(player);
    }

    private boolean FRosterEnabled = true;

    public void LoadFRosterEnabledFromConfig() {
        FRosterEnabled = mainClass.getConfig().getBoolean("FRosterEnabled");
    }

    private void SaveFRosterEnabledInConfig() {
        mainClass.getConfig().set("FRosterEnabled", FRosterEnabled);
        mainClass.saveConfig();
    }

    private void SendUsageMessage(Player player) {
        player.sendMessage(ChatColor.GRAY + "F Roster Usage:" + "\n" + "/f roster list" + "\n" + "/f roster add <Player Name>");
        if (staffMain.isStaff(player)) {
            player.sendMessage(ChatColor.GOLD + "/f roster remove <Player Name> <Faction Name>" + "\n" + "/f roster <Enable/Disable>");
        }
    }

    @EventHandler
    private void DontAllowInviteIfNotInRoster(PlayerCommandPreprocessEvent event) {
        if (!FRosterEnabled) {
            return;
        }
        String command = event.getMessage();
        String[] args = command.split(" ");
        Player player = event.getPlayer();
        if (StringUtils.containsIgnoreCase(command, "/f join") && args.length == 3) {
            Faction faction = null;
            if (Factions.getInstance().getByTag(args[2]) != null) {
                faction = Factions.getInstance().getByTag(args[2]);
            } else if (Bukkit.getPlayerExact(args[2]) != null) {
                faction = FPlayers.getInstance().getByPlayer(Bukkit.getPlayerExact(args[2])).getFaction();
            } if (faction == null) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "invalid argument");
                return;
            }
            if (!isInFRoster(player, faction)) {
                player.sendMessage(ChatColor.RED + "You are not added to " + ChatColor.DARK_RED + faction.getTag() + ChatColor.RED + "'s roster");
                event.setCancelled(true);
            }
        }
    }

    HashMap<String, List<String>> FRosters = new HashMap<>();

    private void RemovePlayerFromFRoster(Player player, OfflinePlayer PlayerToRemove, Faction faction) {
        List<String> NewList = getFRosterUUIDS(faction);
        NewList.remove(PlayerToRemove.getUniqueId().toString());
        FRosters.replace(faction.getId(), NewList);
        SaveFRostersInConfig();
        player.sendMessage(ChatColor.DARK_GREEN + PlayerToRemove.getName() + ChatColor.GREEN + " removed from " + ChatColor.DARK_GREEN + faction.getTag() + ChatColor.GREEN + "'s roster");
    }

    private void AddPlayerToFRoster(Player player, Player PlayerToAdd) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = fPlayer.getFaction();
        if (fPlayer.getRole().isAtLeast(Role.COLEADER)) {
            if (!isInFRoster(PlayerToAdd, faction)) {
                if (AmountOfPlayersInFRoster(faction) < MaxRosterSize) {
                    List<String> NewList = getFRosterUUIDS(faction);
                    NewList.add(PlayerToAdd.getUniqueId().toString());
                    FRosters.put(faction.getId(), NewList);
                    SaveFRostersInConfig();
                    player.sendMessage(ChatColor.DARK_GREEN + PlayerToAdd.getName() + ChatColor.GREEN + " successfully added to the f roster");
                } else {
                    player.sendMessage(ChatColor.RED + "Your faction cannot have any more members in the f roster");
                }
            } else {
                player.sendMessage(ChatColor.RED + PlayerToAdd.getName() + " is already in the roster");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You must be co-leader or above to use this command");
        }
    }

    private Integer AmountOfPlayersInFRoster(Faction faction) {
        return getFRosterUUIDS(faction).size();
    }

    private boolean isInFRoster(Player player, Faction faction) {
        String uuid = player.getUniqueId().toString();
        String FactionId = faction.getId();
        List<String> FRosterMembers = FRosters.getOrDefault(FactionId, new ArrayList<>());
        return FRosterMembers.contains(uuid);
    }

    private void SendPlayerFRosterNames(Player player) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        List<String> FRosterNames = getFRosterNames(faction);
        String message = ChatColor.DARK_GREEN + String.valueOf(FRosterNames.size()) + "/" + MaxRosterSize + "\n" + ChatColor.GREEN + String.join("\n", FRosterNames);
        player.sendMessage(message);
    }

    private List<String> getFRosterNames(Faction faction) {
        List<String> uuids = getFRosterUUIDS(faction);
        List<String> names = new ArrayList<>();
        for (String uuid : uuids) {
            names.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
        }
        Collections.sort(names);
        return names;
    }

    private List<String> getFRosterUUIDS(Faction faction) {
        return FRosters.getOrDefault(faction.getId(), new ArrayList<>());
    }

    public void LoadFRostersFromConfig() {
        List<String> list = mainClass.getConfig().getStringList("FRosters");
        for (String string : list) {
            String FactionId = string.split(":")[0];
            String uuid = string.split(":")[1];
            if (FRosters.containsKey(FactionId)) {
                List<String> NewList = FRosters.get(FactionId);
                NewList.add(uuid);
                FRosters.put(FactionId, NewList);
            } else {
                FRosters.put(FactionId, new ArrayList<>(Arrays.asList(uuid)));
            }
        }
    }

    private void SaveFRostersInConfig() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, List<String>> element : FRosters.entrySet()) {
            String faction = element.getKey();
            List<String> PlayersUUID = element.getValue();
            for (String uuid : PlayersUUID) {
                list.add(faction + ":" + uuid);
            }
        }
        mainClass.getConfig().set("FRosters", list);
        mainClass.saveConfig();
    }
}
