package Classes;

import CustomEnchants.CustomEnchantsEvents;
import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ClassesBard implements Listener {
    MainClass mainClass;
    CustomEnchantsEvents customEnchantsEvents;
    UtilityMain utilityMain = new UtilityMain();
    public ClassesBard(MainClass mc) {
        mainClass = mc;
        customEnchantsEvents = new CustomEnchantsEvents(mainClass);
    }

    @EventHandler
    private void AddOrRemoveToBardEnergyHashMap(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (isInBardGear(player)) {
            if (!BardEnergy.containsKey(player)) {
                BardEnergy.put(player, 0);
            }
        } else {
            BardEnergy.remove(player);
        }
    }

    @EventHandler
    private void AddBardToHashMapWhenJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isInBardGear(player)) {
            BardEnergy.put(player, 0);
        }
    }

    @EventHandler
    private void UsesBardEffect(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        if (isInBardGear(player) && player.getItemInHand() != null) {
           clickedBardEffect(player);
           event.setCancelled(true);
        }
    }

    @EventHandler
    private void HeldEffects(PlayerItemHeldEvent event) {
        int slot = event.getNewSlot();
        Player player = event.getPlayer();
        if (player.getInventory().getItem(slot) == null) {
            return;
        }
        ItemStack item = player.getInventory().getItem(slot);
        if (isInBardGear(player)) {
            HeldItemBardEffect(player, item);
        }
    }


    List<Material> HeldItems = new ArrayList<>(Arrays.asList(Material.GHAST_TEAR, Material.SUGAR, Material.IRON_INGOT, Material.BLAZE_POWDER, Material.MAGMA_CREAM, Material.RAW_FISH));
    private void HeldItemBardEffect(Player player, ItemStack item) {
        if (!utilityMain.isInFaction(player)) {
            return;
        }
        if (!HeldItems.contains(item.getType())) {
            return;
        }
        Material type = item.getType();
        PotionEffect effect = null;
        int duration = 120;
        if (type.equals(Material.GHAST_TEAR)) {
            effect = new PotionEffect(PotionEffectType.REGENERATION, duration, 0);
        } else if (type.equals(Material.SUGAR)) {
            effect = new PotionEffect(PotionEffectType.SPEED, duration, 1);
        } else if (type.equals(Material.IRON_INGOT)) {
            effect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, 0);
        } else if (type.equals(Material.BLAZE_POWDER)) {
            effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 0);
        } else if (type.equals(Material.MAGMA_CREAM)) {
            effect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0);
        } else if (type.equals(Material.RAW_FISH)) {
            effect = new PotionEffect(PotionEffectType.WATER_BREATHING, duration, 0);
        }
        assert effect != null;
        for (Player member : getNearbyFactionMembers(player)) {
            for (PotionEffect potionEffect : member.getActivePotionEffects()) {
                if (potionEffect.getType().equals(effect.getType())) {
                    if (potionEffect.getDuration() < 120 && potionEffect.getAmplifier() <= effect.getAmplifier()) {
                        member.removePotionEffect(effect.getType());
                    }
                    break;
                }
            }
            member.addPotionEffect(effect);
        }
    }
    
    

    List<Material> ClickAbilityItems = new ArrayList<>(Arrays.asList(Material.GHAST_TEAR, Material.SUGAR, Material.IRON_INGOT, Material.SPECKLED_MELON, Material.BLAZE_POWDER, Material.FEATHER, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.SLIME_BALL, Material.SPIDER_EYE));
    private void clickedBardEffect(Player player) {
        if (!utilityMain.isInFaction(player)) {
            return;
        }
        if (!ClickAbilityItems.contains(player.getItemInHand().getType())) {
            return;
        }
        if (BardEnergy.get(player) < 29) {
            player.sendMessage(ChatColor.RED + "Need " + ChatColor.DARK_RED + "" + ChatColor.BOLD + (29 - BardEnergy.get(player)) + ChatColor.RED + " more energy");
            return;
        }
        Material type = player.getItemInHand().getType();
        PotionEffect effect = null;
        int duration = 120;
        boolean NegativeEffect = false;
        if (type.equals(Material.GHAST_TEAR)) {
            effect = new PotionEffect(PotionEffectType.REGENERATION, duration, 2);
        } else if (type.equals(Material.IRON_INGOT)) {
            effect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, 9);
        } else if (type.equals(Material.SUGAR)) {
            effect = new PotionEffect(PotionEffectType.SPEED, duration, 2);
        } else if (type.equals(Material.SPECKLED_MELON)) {
            effect = new PotionEffect(PotionEffectType.HEAL, 1, 9);
        } else if (type.equals(Material.BLAZE_POWDER)) {
            effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 1);
        } else if (type.equals(Material.FEATHER)) {
            effect = new PotionEffect(PotionEffectType.JUMP, duration, 5);
        } else if (type.equals(Material.RED_MUSHROOM)) {
            effect = new PotionEffect(PotionEffectType.POISON, duration, 2);
            NegativeEffect = true;
        } else if (type.equals(Material.BROWN_MUSHROOM)) {
            effect = new PotionEffect(PotionEffectType.WEAKNESS, duration, 4);
            NegativeEffect = true;
        } else if (type.equals(Material.SLIME_BALL)) {
            effect = new PotionEffect(PotionEffectType.SLOW, duration, 1);
            NegativeEffect = true;
        } else if (type.equals(Material.SPIDER_EYE)) {
            effect = new PotionEffect(PotionEffectType.WITHER, duration, 1);
            NegativeEffect = true;
        }
        List<Player> PlayersToEffect;
        if (!NegativeEffect) {
             PlayersToEffect = getNearbyFactionMembers(player);
        } else {
            PlayersToEffect = getNearbyEnemies(player);
        }
        assert effect != null;
        for (Player member : PlayersToEffect) {
            member.removePotionEffect(effect.getType());
            member.addPotionEffect(effect);
            ResetPotionEffectsLater(member);
        }
        BardEnergy.put(player, BardEnergy.get(player) - 30);
        RemoveOneItem(player);
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Used bard effect");
        SendMessage(player, BuildEnergyBar(BardEnergy.get(player)));
    }

    private void RemoveOneItem(Player player) {
        player.sendMessage("Removed item");
        ItemStack item = player.getItemInHand();
        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() == 0) {
            player.setItemInHand(null);
        }
    }

    private List<Player> getNearbyEnemies(Player player) {
        List<Player> enemies = new ArrayList<>();
        Faction PlayerFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                Player enemy = (Player) entity;
                if (!utilityMain.isInFaction(player)) {
                    enemies.add(enemy);
                } else {
                    Faction EnemyFaciton = FPlayers.getInstance().getByPlayer((Player) entity).getFaction();
                    if (!PlayerFaction.equals(EnemyFaciton)) {
                        enemies.add(enemy);
                    }
                }
            }
        }
        return enemies;
    }

    private void ResetPotionEffectsLater(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ResetPermamentCeEffects(player);
            }
        }.runTaskLater(mainClass.getPlugin(),100);
    }

    private void ResetPermamentCeEffects(Player player) {
        if (!player.isOnline()) {
            return;
        }
        customEnchantsEvents.RemovePotionEffects(player, false);
        for (ItemStack gear : player.getInventory().getArmorContents()) {
            if (gear != null && gear.hasItemMeta() && gear.getItemMeta().hasLore()) {
                for (String lore : gear.getItemMeta().getLore()) {
                    customEnchantsEvents.AddPermamentPotionEffects(ChatColor.stripColor(lore), player);
                }
            }
        }
        if (player.getItemInHand() != null && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasLore()) {
            for (String lore : player.getItemInHand().getItemMeta().getLore()) {
                customEnchantsEvents.AddPermamentPotionEffects(ChatColor.stripColor(lore), player);
            }
        }
    }

    int radius = 30;
    private List<Player> getNearbyFactionMembers(Player player) {
        List<Player> FactionMembers = FPlayers.getInstance().getByPlayer(player).getFaction().getOnlinePlayers();
        List<Player> NearbyMembers = new ArrayList<>();
        for (Player member : FactionMembers) {
            if (member.getLocation().distance(player.getLocation()) <= radius) {
                NearbyMembers.add(member);
            }
        }
        return NearbyMembers;
    }

    public HashMap<Player, Integer> BardEnergy = new HashMap<>();

    public boolean isInBardGear(Player player) {
        for (ItemStack gear : player.getInventory().getArmorContents()) {
            if (!gear.getType().toString().contains("GOLD")) {
                return false;
            }
        }
        return true;
    }

    public void BardEnergyManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Integer> element : BardEnergy.entrySet()) {
                    Player player = element.getKey();
                    Integer energy = element.getValue();
                    if (energy < 60) {
                        BardEnergy.replace(player, energy + 1);
                    }
                    SendMessage(player, BuildEnergyBar(BardEnergy.get(player)));
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);
    }

    private String BuildEnergyBar(Integer energy) {
        List<String> ExpBarList = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            if (i < energy) {
                ExpBarList.add(ChatColor.YELLOW + "|");
            }
            else {
                ExpBarList.add(ChatColor.GRAY + "|");
            }
        }
        return String.join("", ExpBarList);
    }

    private void SendMessage(Player player, String bar) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + bar + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }
}
