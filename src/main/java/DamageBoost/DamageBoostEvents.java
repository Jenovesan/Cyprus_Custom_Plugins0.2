package DamageBoost;

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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DamageBoostEvents implements Listener {
    MainClass mainClass;
    public DamageBoostEvents(MainClass mc) {
        mainClass = mc;
    }

    @EventHandler
    private void playerInteract(PlayerInteractEvent event) { //Checking if they right clicked an Damage Note
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (isDamageBoostNote(player)) {
                if (doesNotHaveADamageBoostCurrently(player)) {
                    AddDamageBoostToPlayer(player);
                }
                else {
                    SendTitleToPlayer(player, "§4Already Activated");
                }
            }
        }
    }

    @EventHandler
    private void entityHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof  Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        if (DamageBoostPerc.containsKey(damager.getUniqueId())) {
            double DamageIncreasePercentage = DamageBoostPerc.get(damager.getUniqueId());
            event.setDamage(event.getDamage() + (event.getDamage() / 100 * DamageIncreasePercentage));
        }
    }

    private boolean doesNotHaveADamageBoostCurrently(Player player) {
        if (DamageBoostPerc.containsKey(player.getUniqueId())) {
            return false;
        }
        return true;
    }

    private boolean isDamageBoostNote(Player player) {
        if (player.getItemInHand() != null) {
            if (player.getItemInHand().hasItemMeta()) {
                if (player.getItemInHand().getItemMeta().hasDisplayName()) {
                    if (player.getItemInHand().getItemMeta().hasLore()) {
                        if (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.GREEN + "") && player.getItemInHand().getItemMeta().getLore().contains(ChatColor.GRAY + "Right click to apply the damage boost")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    HashMap<UUID, Integer> DamageBoostPerc = new HashMap<>();
    HashMap<UUID, Integer> DamageBoostTime = new HashMap<>();

    List<UUID> uuids;
    List<Integer> percentages;
    List<Integer> times;

    public void LoadDamageBoostHashMapsInConfig() {
        List<String> ListUuids = mainClass.getConfig().getStringList("DamageBoostPlayers");
        List<Integer> ListPercentages = mainClass.getConfig().getIntegerList("DamageBoostPercentages");
        List<Integer> ListTimes = mainClass.getConfig().getIntegerList("DamageBoostTimes");
        for (int i = 0; i < ListUuids.size(); i++) {
            DamageBoostPerc.put(UUID.fromString(ListUuids.get(i)), ListPercentages.get(i));
            DamageBoostTime.put(UUID.fromString(ListUuids.get(i)), ListTimes.get(i));
        }
    }

    public void SaveDamageBoostHashMapsInConfig() {
        uuids = new ArrayList<>(DamageBoostPerc.keySet());
        List<String> UuidsToStringList = new ArrayList<>();
        for (UUID uuid : uuids) {
            UuidsToStringList.add(uuid.toString());
        }
        mainClass.getConfig().set("DamageBoostPlayers", UuidsToStringList);
        percentages = new ArrayList<>(DamageBoostPerc.values());
        mainClass.getConfig().set("DamageBoostPercentages", percentages);
        times = new ArrayList<>(DamageBoostTime.values());
        mainClass.getConfig().set("DamageBoostTimes", times);
        mainClass.saveConfig();
    }

    private void AddDamageBoostToPlayer(Player player) {
        SendTitleToPlayer(player, "§aDmg Boost Activated"); //Title: used damage boost
        String[] SplitName = player.getItemInHand().getItemMeta().getDisplayName().split("%");
        int DamageBoostPercentage = Integer.parseInt(ChatColor.stripColor(SplitName[0].replace("+", "")));
        DamageBoostPerc.put(player.getUniqueId(), DamageBoostPercentage);
        int DamageBoostTimeInt = Integer.parseInt(SplitName[1].replaceAll("[^0-9]",""));
        DamageBoostTime.put(player.getUniqueId(), DamageBoostTimeInt);
        RemoveDamageBoostNote(player);
        SaveDamageBoostHashMapsInConfig();
    }

    public void DamageBoostTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry element : DamageBoostTime.entrySet()) {
                    UUID uuid = (UUID) element.getKey();
                    int time = (int) element.getValue();
                    if (time == 0) {
                        DamageBoostPerc.remove(uuid);
                        DamageBoostTime.remove(uuid);
                        SaveDamageBoostHashMapsInConfig();
                        if (Bukkit.getPlayer(uuid) != null) {
                            SendTitleToPlayer(Bukkit.getPlayer(uuid), "§4Damage Boost Expired");
                            GetToByAnotherDamageBoost(Bukkit.getPlayer(uuid));
                        }
                    }
                    else {
                        element.setValue(time - 1);
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 72000);
    }

    private void RemoveDamageBoostNote(Player player) {
        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        }
        else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
    }

    private void SendTitleToPlayer(Player player, String Message) { //Title: used damage boost
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + Message + "\"}"),20,20,20);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
    }

    private void GetToByAnotherDamageBoost(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
                PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Buy Another Damage Boost Here\",\"bold\":true,\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"store.cyprusmc.com\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"store.cyprusmc.com\",\"bold\":true,\"color\":\"red\"}]}}"));
                connection.sendPacket(packet);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
        }.runTaskLaterAsynchronously(mainClass.getPlugin(), 100);
    }
}
