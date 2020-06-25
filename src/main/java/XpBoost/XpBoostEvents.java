package XpBoost;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class XpBoostEvents implements Listener {
    MainClass mainClass;
    public XpBoostEvents(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void playerInteract(PlayerInteractEvent event) { //Checking if they right clicked an Xp Note
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (isXpBoostNote(player)) {
                if (doesNotHaveAnExpBoostCurrently(player)) {
                    AddXpBoostToPlayer(player);
                }
                else {
                    SendTitleToPlayer(player, "§4Already Activated");
                }
            }
        }
    }

    @EventHandler
    private void playerXpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        if (XpBoostPerc.containsKey(player.getUniqueId())) {
            event.setAmount((int) (event.getAmount() + ((double) event.getAmount() / 100 * XpBoostPerc.get(player.getUniqueId()))));
        }
    }

    public boolean doesNotHaveAnExpBoostCurrently(Player player) {
        if (XpBoostPerc.containsKey(player.getUniqueId())) {
            return false;
        }
        return true;
    }

    private boolean isXpBoostNote(Player player) {
        if (player.getItemInHand() != null) {
            if (player.getItemInHand().hasItemMeta()) {
                if (player.getItemInHand().getItemMeta().hasDisplayName()) {
                    if (player.getItemInHand().getItemMeta().hasLore()) {
                        if (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.GREEN + "") && player.getItemInHand().getItemMeta().getLore().contains(ChatColor.GRAY + "Right click to apply the xp boost")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public HashMap<UUID, Integer> XpBoostPerc = new HashMap<>();
    public HashMap<UUID, Integer> XpBoostTime = new HashMap<>();

    List<UUID> uuids;
    List<Integer> percentages;
    List<Integer> times;

    public void LoadXpBoostHashMapsInConfig() {
        List<String> ListUuids = mainClass.getConfig().getStringList("XpBoostPlayers");
        List<Integer> ListPercentages = mainClass.getConfig().getIntegerList("XpBoostPercentages");
        List<Integer> ListTimes = mainClass.getConfig().getIntegerList("XpBoostTimes");
        for (int i = 0; i < ListUuids.size(); i++) {
            XpBoostPerc.put(UUID.fromString(ListUuids.get(i)), ListPercentages.get(i));
            XpBoostTime.put(UUID.fromString(ListUuids.get(i)), ListTimes.get(i));
        }
    }

    public void SaveXpBoostHashMapsInConfig() {
        uuids = new ArrayList<>(XpBoostPerc.keySet());
        List<String> UuidsToStringList = new ArrayList<>();
        for (UUID uuid : uuids) {
            UuidsToStringList.add(uuid.toString());
        }
        mainClass.getConfig().set("XpBoostPlayers", UuidsToStringList);
        percentages = new ArrayList<>(XpBoostPerc.values());
        mainClass.getConfig().set("XpBoostPercentages", percentages);
        times = new ArrayList<>(XpBoostTime.values());
        mainClass.getConfig().set("XpBoostTimes", times);
        mainClass.saveConfig();
    }

    private void AddXpBoostToPlayer(Player player) {
        SendTitleToPlayer(player, "§aXp Boost Activated"); //Title: used xp boost
        String[] SplitName = player.getItemInHand().getItemMeta().getDisplayName().split("%");
        int XpBoostPercentage = Integer.parseInt(ChatColor.stripColor(SplitName[0].replace("+", "")));
        XpBoostPerc.put(player.getUniqueId(), XpBoostPercentage);
        int XpBoostTimeInt = Integer.parseInt(SplitName[1].replaceAll("[^0-9]",""));
        XpBoostTime.put(player.getUniqueId(), XpBoostTimeInt);
        RemoveXpBoostNote(player);
        SaveXpBoostHashMapsInConfig();
    }

    public void XpBoostTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry element : XpBoostTime.entrySet()) {
                    UUID uuid = (UUID) element.getKey();
                    int time = (int) element.getValue();
                    if (time == 0) {
                        XpBoostPerc.remove(uuid);
                        XpBoostTime.remove(uuid);
                        if (Bukkit.getPlayer(uuid) != null) {
                            SendTitleToPlayer(Bukkit.getPlayer(uuid), "§4Xp Boost Expired");
                            GetToByAnotherXpBoost(Bukkit.getPlayer(uuid));
                        }
                    }
                    else {
                        element.setValue(time - 1);
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 72000);
    }

    private void RemoveXpBoostNote(Player player) {
        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        }
        else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
    }

    private void SendTitleToPlayer(Player player, String Message) { //Title: used xp boost
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + Message + "\"}"),20,20,20);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
    }

    private void GetToByAnotherXpBoost(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
                PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Buy Another Xp Boost Here\",\"bold\":true,\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"store.cyprusmc.com\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"store.cyprusmc.com\",\"bold\":true,\"color\":\"red\"}]}}"));
                connection.sendPacket(packet);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
        }.runTaskLaterAsynchronously(mainClass.getPlugin(), 100);
    }
}
