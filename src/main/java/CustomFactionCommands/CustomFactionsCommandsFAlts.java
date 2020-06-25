package CustomFactionCommands;

import Main.MainClass;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class CustomFactionsCommandsFAlts implements Listener {
    MainClass mainClass;
    public CustomFactionsCommandsFAlts(MainClass mc) {
        mainClass = mc;
    }

    public void StartingPoint(Player player, String[] args) {
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("invite") && Bukkit.getPlayerExact(args[1]) != null) {
                InvitePlayer(player, Bukkit.getPlayerExact(args[1]));
                return;
            } else if (args[0].equalsIgnoreCase("kick") && Bukkit.getOfflinePlayer(args[1]) != null) {
                if (isAFactionsFAlt(faction, Bukkit.getOfflinePlayer(args[1]))) {
                    KickPlayerFromFAlts(faction, Bukkit.getOfflinePlayer(args[1]));
                    player.sendMessage(ChatColor.DARK_GREEN + Bukkit.getOfflinePlayer(args[1]).getName() + ChatColor.GREEN + " has been kicked from your f alt list");
                    return;
                }
            } else if (args[0].equalsIgnoreCase("join") && getFaction(args[1]) != null) {
                if (CanJoinAsAnFAlt(player, Objects.requireNonNull(getFaction(args[1])))) {
                    JoinAsAnFAlt(player, Objects.requireNonNull(getFaction(args[1])));
                    return;
                }
            }
        }else if (args.length == 1 ) {
            if (args[0].equalsIgnoreCase("list")) {
                SendFAltList(player, faction);
                return;
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (isAFAlt(player)) {
                    LeaveFAlt(player);
                    return;
                }
            }
        }
        player.sendMessage(ChatColor.GRAY + "F Alts:" + "\n" + "/f alt invite <Player Name>" + "\n" + "/f alt kick <Player Name>" + "\n" + "/f alt join <Player Name or Faction>" + "\n" + "/f alt leave" + "\n" + "/f alt list");
    }

    HashMap<Faction, Set<Player>> InvitedPlayers = new HashMap<>();
    HashMap<String, List<UUID>> FAlts = new HashMap<>();

    private void LeaveFAlt(Player player) {
        for (Map.Entry<String, List<UUID>> element : FAlts.entrySet()) {
            List<UUID> uuids = element.getValue();
            uuids.remove(player.getUniqueId());
            FAlts.put(element.getKey(), uuids);
            player.sendMessage(ChatColor.GREEN + "You have left " + ChatColor.DARK_GREEN + Factions.getInstance().getFactionById(element.getKey()).getTag() + "'s" + ChatColor.GREEN + " f alt list");
        }
        SaveFAltsToConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void CancelInteractIfIsAnFAlt(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isAFAlt(player)) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot do this as you are an f alt");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void CancelAttackIfISAnFAlt(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        if (!isAFAlt(player)) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot do this as you are an f alt");
    }

    public Boolean isAFAlt(Player player) {
        UUID PlayerUUID = player.getUniqueId();
        for (Map.Entry<String, List<UUID>> element : FAlts.entrySet()) {
            for (UUID uuid : element.getValue()) {
                if (uuid.equals(PlayerUUID)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void SendFAltList(Player player, Faction faction) {
        List<String> FRosterNames = getFAltNames(faction);
        String message = String.join("\n", FRosterNames);
        player.sendMessage(message);
    }

    private List<String> getFAltNames(Faction faction) {
        List<UUID> uuids = FAlts.getOrDefault(faction.getId(), new ArrayList<>());
        List<String> names = new ArrayList<>();
        for (UUID uuid : uuids) {
            names.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        Collections.sort(names);
        return names;
    }

    private void KickPlayerFromFAlts(Faction faction, OfflinePlayer player) {
        List<UUID> uuids = FAlts.get(faction.getId());
        uuids.remove(player.getUniqueId());
        FAlts.put(faction.getId(), uuids);
        SaveFAltsToConfig();
        if (player.isOnline()) {
            Player player1 = (Player) player;
            player1.sendMessage(ChatColor.RED + "You have been kicked from " + ChatColor.DARK_RED + faction.getTag() + "'s" + ChatColor.RED + " f alt list");
        }
    }

    private Boolean isAFactionsFAlt(Faction faction, OfflinePlayer player) {
        return FAlts.containsKey(faction.getId()) && FAlts.get(faction.getId()).contains(player.getUniqueId());
    }

    private void InvitePlayer(Player Inviter, Player Invitee) {
        Faction InviterFaction = FPlayers.getInstance().getByPlayer(Inviter).getFaction();
        Set<Player> Invited = InvitedPlayers.getOrDefault(InviterFaction, new HashSet<>());
        Invited.add(Invitee);
        InvitedPlayers.put(InviterFaction, Invited);
        Inviter.sendMessage(ChatColor.GREEN + "You have invited " + ChatColor.DARK_GREEN + Invitee.getName() + ChatColor.GREEN + " as an f alt");
        Invitee.sendMessage(ChatColor.GREEN + "You have been invited to " + ChatColor.DARK_GREEN + InviterFaction.getTag() + ChatColor.GREEN + " as an f alt");
    }

    private void JoinAsAnFAlt(Player sender, Faction faction) {
        List<UUID> alts = FAlts.getOrDefault(faction.getId(), new ArrayList<>());
        alts.add(sender.getUniqueId());
        FAlts.put(faction.getId(), alts);
        sender.sendMessage(ChatColor.GREEN + "You have joined " + ChatColor.DARK_GREEN + faction.getTag() + ChatColor.GREEN + " as an f alt");
        SaveFAltsToConfig();
    }

    private Boolean CanJoinAsAnFAlt(Player sender, Faction faction) {
        if (FAlts.containsKey(faction.getId()) && FAlts.get(faction.getId()).contains(sender.getUniqueId())) {
            return false;
        }
        return InvitedPlayers.containsKey(faction) && InvitedPlayers.get(faction).contains(sender);
    }

    private Faction getFaction(String string) {
        if (Bukkit.getPlayerExact(string) != null) {
            return FPlayers.getInstance().getByPlayer(Bukkit.getPlayerExact(string)).getFaction();
        } else if (Factions.getInstance().getByTag(string) != null) {
            return Factions.getInstance().getByTag(string);
        }
        return null;
    }

    private void SaveFAltsToConfig() {
        List<String> alts = new ArrayList<>();
        for (Map.Entry<String, List<UUID>> element : FAlts.entrySet()) {
            String FactionId = element.getKey();
            for (UUID uuid : element.getValue()) {
                alts.add(FactionId + ":" + uuid);
            }
        }
        mainClass.getConfig().set("FAlts", alts);
        mainClass.saveConfig();
    }

    public void LoadFAltsFromConfig() {
        List<String> alts = mainClass.getConfig().getStringList("FAlts");
        for (String string : alts) {
            String FactionId = string.split(":")[0];
            UUID uuid = UUID.fromString(string.split(":")[1]);
            List<UUID> uuids = FAlts.getOrDefault(FactionId, new ArrayList<>());
            uuids.add(uuid);
            FAlts.put(FactionId, uuids);
        }
    }
}
