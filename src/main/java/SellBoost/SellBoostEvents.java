package SellBoost;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SellBoostEvents implements Listener {
    MainClass mainClass;
    public SellBoostEvents(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void playerInteract(PlayerInteractEvent event) { //Checking if they right clicked an Sell Note
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (isSellBoostNote(player)) {
                if (!hasASellBoostCurrently(player)) {
                    AddSellBoostToPlayer(player);
                }
                else {
                    SendTitleToPlayer(player, "§4Already Activated");
                }
            }
        }
    }

    public boolean hasASellBoostCurrently(Player player) {
        return SellBoostPerc.containsKey(player.getUniqueId());
    }

    public Integer getSellBoostPercentage(Player player) {
        return SellBoostPerc.getOrDefault(player.getUniqueId(), 0);
    }

    private boolean isSellBoostNote(Player player) {
        if (player.getItemInHand() != null) {
            if (player.getItemInHand().hasItemMeta()) {
                if (player.getItemInHand().getItemMeta().hasDisplayName()) {
                    if (player.getItemInHand().getItemMeta().hasLore()) {
                        if (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.GREEN + "") && player.getItemInHand().getItemMeta().getLore().contains(ChatColor.GRAY + "Right click to apply the sell boost")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    HashMap<UUID, Integer> SellBoostPerc = new HashMap<>();
    HashMap<UUID, Integer> SellBoostTime = new HashMap<>();

    List<UUID> uuids;
    List<Integer> percentages;
    List<Integer> times;

    public void LoadSellBoostHashMapsInConfig() {
        List<String> ListUuids = mainClass.getConfig().getStringList("SellBoostPlayers");
        List<Integer> ListPercentages = mainClass.getConfig().getIntegerList("SellBoostPercentages");
        List<Integer> ListTimes = mainClass.getConfig().getIntegerList("SellBoostTimes");
        for (int i = 0; i < ListUuids.size(); i++) {
            SellBoostPerc.put(UUID.fromString(ListUuids.get(i)), ListPercentages.get(i));
            SellBoostTime.put(UUID.fromString(ListUuids.get(i)), ListTimes.get(i));
        }
    }

    public void SaveSellBoostHashMapsInConfig() {
        uuids = new ArrayList<>(SellBoostPerc.keySet());
        List<String> UuidsToStringList = new ArrayList<>();
        for (UUID uuid : uuids) {
            UuidsToStringList.add(uuid.toString());
        }
        mainClass.getConfig().set("SellBoostPlayers", UuidsToStringList);
        percentages = new ArrayList<>(SellBoostPerc.values());
        mainClass.getConfig().set("SellBoostPercentages", percentages);
        times = new ArrayList<>(SellBoostTime.values());
        mainClass.getConfig().set("SellBoostTimes", times);
        mainClass.saveConfig();
    }

    private void AddSellBoostToPlayer(Player player) {
        SendTitleToPlayer(player, "§aDmg Boost Activated"); //Title: used sell boost
        String[] SplitName = player.getItemInHand().getItemMeta().getDisplayName().split("%");
        int SellBoostPercentage = Integer.parseInt(ChatColor.stripColor(SplitName[0].replace("+", "")));
        SellBoostPerc.put(player.getUniqueId(), SellBoostPercentage);
        int SellBoostTimeInt = Integer.parseInt(SplitName[1].replaceAll("[^0-9]",""));
        SellBoostTime.put(player.getUniqueId(), SellBoostTimeInt);
        RemoveSellBoostNote(player);
        SaveSellBoostHashMapsInConfig();
    }

    public void SellBoostTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry element : SellBoostTime.entrySet()) {
                    UUID uuid = (UUID) element.getKey();
                    int time = (int) element.getValue();
                    if (time == 0) {
                        SellBoostPerc.remove(uuid);
                        SellBoostTime.remove(uuid);
                        SaveSellBoostHashMapsInConfig();
                        if (Bukkit.getPlayer(uuid) != null) {
                            SendTitleToPlayer(Bukkit.getPlayer(uuid), "§4Sell Boost Expired");
                            GetToByAnotherSellBoost(Bukkit.getPlayer(uuid));
                        }
                    }
                    else {
                        element.setValue(time - 1);
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 72000);
    }

    private void RemoveSellBoostNote(Player player) {
        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        }
        else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
    }

    private void SendTitleToPlayer(Player player, String Message) { //Title: used sell boost
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + Message + "\"}"),20,20,20);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
    }

    private void GetToByAnotherSellBoost(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
                PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Buy Another Sell Boost Here\",\"bold\":true,\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"store.cyprusmc.com\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"store.cyprusmc.com\",\"bold\":true,\"color\":\"red\"}]}}"));
                connection.sendPacket(packet);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
        }.runTaskLaterAsynchronously(mainClass.getPlugin(), 100);
    }
}
