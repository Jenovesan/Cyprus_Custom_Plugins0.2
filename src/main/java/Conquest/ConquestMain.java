package Conquest;

import Main.MainClass;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ConquestMain implements Listener {
    MainClass mainClass;
    Location conquestCenter = new Location(Bukkit.getWorld("world"), -1350, 50,650); //Should be firerealm
    Integer conquestRadius = 3;
    public ConquestMain(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void test(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(event.getPlayer().getWorld().getName());
    }

    public Faction conquestCaptor;
    Integer status = 0;
    public void conquestTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getFactionsOnConquest().size() == 1) {
                    Faction faction = getFactionOnConquest();
                    if (status <= 100) {
                        if (faction != conquestCaptor && conquestCaptor != null) {
                            if (status == 100 && faction != null) {
                                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "(" + ChatColor.RED + "!" + ChatColor.GRAY + ")  " + ChatColor.RED + "Conquest " + ChatColor.GRAY + ">>" + ChatColor.RESET + ChatColor.RED + faction.getTag() + ChatColor.GRAY + " has begun to take control of conquest" + ChatColor.RED + "!");
                            }
                            status--;
                        } else {
                            if (status < 100) {
                                status++;
                            }
                            conquestCaptor = faction;
                        }
                    }
                }
                if (status == 0) {
                    conquestCaptor = null;
                }
                sendProgressBar();
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);
    }

    private void sendProgressBar() {
        StringBuilder sb = new StringBuilder();
        String msg = "";
        for (int i = 1; i < 101; i++) {
            String color = ChatColor.GRAY + "";
            if (i >= status) {
                color = ChatColor.RED + "";
            }
            msg = sb.append(color).append("|").toString();
        }
        for (Player player : getPlayersOnConquest()) {
            player.sendMessage(msg);
        }
    }

    private List<Player> getPlayersOnConquest() {
        List<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(conquestCenter) <= conquestRadius + 0.5) {
                players.add(player);
            }
        }
        return players;
    }

    private Set<Faction> getFactionsOnConquest() {
        Set<Faction> factions = new HashSet<>();
        for (Player player : getPlayersOnConquest()) {
            factions.add(FPlayers.getInstance().getByPlayer(player).getFaction());
        }
        return factions;
    }

    private Faction getFactionOnConquest() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(conquestCenter) <= conquestRadius + 0.5) {
                return FPlayers.getInstance().getByPlayer(player).getFaction();
            }
        }
        return null;
    }
}
