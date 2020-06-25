package GoldenApplesCooldown;

import Main.MainClass;
import CustomEnchants.CustomEnchantsEvents;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoldenApplesCooldownMain implements Listener {
    MainClass mainClass;
    CustomEnchantsEvents customEnchantsEvents;
    public GoldenApplesCooldownMain(MainClass mc) {
        mainClass = mc;
        customEnchantsEvents = new CustomEnchantsEvents(mc);
    }

    @EventHandler
    private void playerConsume(PlayerItemConsumeEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item.getType().equals(Material.GOLDEN_APPLE)) {
            if (item.getDurability() == 0) {
                if (TrackCrapple.containsKey(player.getName())) {
                    event.setCancelled(true);
                    ResetMainHand(player);
                    player.sendMessage(ChatColor.RED + "Crapple Cooldown: " + TrackCrapple.get(player.getName()) + "s");
                    return;
                }
                CrappleConsume(player);
            }
            else {
                if (TrackGapple.containsKey(player.getName())) {
                    event.setCancelled(true);
                    ResetMainHand(player);
                    player.sendMessage(ChatColor.RED + "Gapple Cooldown: " + TrackGapple.get(player.getName()) + "s");
                    return;
                }
                GappleConsume(player);
            }
        }
    }

    public HashMap<String, Integer> TrackGapple = new HashMap<>();
    public HashMap<String, Integer> TrackCrapple = new HashMap<>();

    private void GappleConsume(Player player) {
        if (!(GetArmorLores(player).contains("Gapple"))) {
            TrackGapple.put(player.getName(), mainClass.getConfig().getInt("GodAppleCooldown"));
        }
        else {
            TrackGapple.put(player.getName(), (mainClass.getConfig().getInt("GodAppleCooldown") - 10));
        }
    }

    private void CrappleConsume(Player player) {
        TrackCrapple.put(player.getName(), mainClass.getConfig().getInt("GoldenAppleCooldown"));
    }

    public void RemoveCooldowns() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry element : TrackGapple.entrySet()) {
                    String player = (String) element.getKey();
                    int time = (int) element.getValue();
                    TrackGapple.replace(player, time - 1);
                    if (time <= 1) {
                        GappleCooldownResetMessage(Bukkit.getPlayerExact(player));
                        TrackGapple.remove(player);
                    }
                }
                for (Map.Entry element : TrackCrapple.entrySet()) {
                    String player = (String) element.getKey();
                    int time = (int) element.getValue();
                    TrackCrapple.replace(player, time - 1);
                    if (time <= 1) {
                        TrackCrapple.remove(player);
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);
    }

    private void GappleCooldownResetMessage(Player player) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§eGapple Cooldown Reset\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }

    private void ResetMainHand(Player player) {
        ItemStack itemStack = player.getItemInHand();
        player.setItemInHand(null);
        player.setItemInHand(itemStack);
    }

    public List<String> GetArmorLores(Player player) {
        List<String> Lores = new ArrayList<>();
        for (ItemStack Armor : player.getInventory().getArmorContents()) {
            if (Armor.hasItemMeta()) {
                if (Armor.getItemMeta().hasLore()) {
                    for (String Lore : Armor.getItemMeta().getLore()) {
                        Lores.add(net.md_5.bungee.api.ChatColor.stripColor(Lore));
                    }
                }
            }
        }
        return Lores;
    }
}
