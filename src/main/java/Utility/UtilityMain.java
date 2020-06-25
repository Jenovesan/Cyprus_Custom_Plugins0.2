package Utility;

import Staff.StaffMain;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.ItemArmor;
import net.minecraft.server.v1_8_R3.ItemSword;
import net.minecraft.server.v1_8_R3.ItemTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.*;

public class UtilityMain {
StaffMain staffMain = new StaffMain();
    public ItemStack createGuiItem(Material material, String name, String...lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metaLore = new ArrayList<>(Arrays.asList(lore));
        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createEnchantedItem(Material material, String name, Integer MainEnchantLevel, Integer UnbreakingLevel) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name);
        } if (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemArmor) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, MainEnchantLevel, true);
        } else if (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemSword) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, MainEnchantLevel, true);
        } else if (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemTool) {
            meta.addEnchant(Enchantment.DIG_SPEED, MainEnchantLevel, true);
        } if (UnbreakingLevel != null) {
            meta.addEnchant(Enchantment.DURABILITY, UnbreakingLevel, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createCustomBow(String name, Integer power, Integer punch, Integer flame, Integer unbreaking, Integer infinity) {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (power != null) {
            meta.addEnchant(Enchantment.ARROW_DAMAGE, power, true);
        } if (unbreaking != null) {
            meta.addEnchant(Enchantment.DURABILITY, unbreaking, true);
        } if (punch != null) {
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, punch, true);
        } if (flame != null) {
            meta.addEnchant(Enchantment.ARROW_FIRE, flame, true);
        } if (infinity != null) {
            meta.addEnchant(Enchantment.ARROW_INFINITE, infinity, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createGuiItemFromItemStack(ItemStack item, String name, String...lore) {
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name);
        }
        List<String> metaLore;
        if (meta.getLore() == null) {
            metaLore = new ArrayList<>();
        }
        else {
            metaLore = meta.getLore();
        }
        metaLore.addAll(Arrays.asList(lore));
        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    public void FillCyprusGuiSize27(Inventory inv) { //Checkered Pattern
        ItemStack BlackStainedGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15);
        ItemStack RedStainedGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);
        for (int i = 0; i < inv.getSize(); i++) {
            if (i % 2 == 0) {
                inv.setItem(i, BlackStainedGlass);
            }
            else {
                inv.setItem(i, RedStainedGlass);
            }
        }
    }

    public Random getRandom() {
        return new Random();
    }

    public boolean RandomNumberBoolean(Integer MaxValue, Integer TargetedValue) {
        Random random = new Random();
        Integer randomNumber =  random.nextInt(MaxValue);
        return TargetedValue < randomNumber;
    }

    public String FormatTime(Integer time) {
        int hours = time / 60;
        int minutes = time % 60;
        int days = hours / 24;
        if (days != 0) {
            hours = hours - (days * 24);
        }
        String FinalTime = "";
        if (days != 0) {
            FinalTime = FinalTime + days + "d ";
        } if (hours != 0 || days != 0) {
            FinalTime = FinalTime + hours + "h ";
        }
        FinalTime = FinalTime + minutes + "m";
        return FinalTime;
    }

    public String FormatPrice(Integer price) {
        Format CommaFormat = NumberFormat.getNumberInstance(Locale.US);
        if (price == null) {
            return "0";
        }
        if (CommaFormat instanceof DecimalFormat) {
            DecimalFormat DecimalFormat = (DecimalFormat) CommaFormat;
            DecimalFormat.applyPattern("#");
            DecimalFormat.setGroupingUsed(true);
            DecimalFormat.setGroupingSize(3);
        }
        return CommaFormat.format(price);
    }

    public String getPlayerNameFromUUID(String StringUUID) {
        if (StringUUID != null) {
            if (Bukkit.getOfflinePlayer(UUID.fromString(StringUUID)) != null) {
                UUID uuid = UUID.fromString(StringUUID);
                if (Bukkit.getOfflinePlayer(uuid) != null) {
                    return Bukkit.getOfflinePlayer(uuid).getName();
                }
            }
        }
        return "No one";
    }

    public ItemStack CustomPotion(String name, PotionType potionType, PotionEffectType effectType, int duration, int amplifier) {
        Potion pot = new Potion(potionType);
        ItemStack potion = new ItemStack(pot.toItemStack(1));
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        List<String> LoreList = new ArrayList<>();
        meta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), false);
        meta.setDisplayName(name);
        meta.setLore(LoreList);
        potion.setItemMeta(meta);
        return potion;
    }

    public ItemStack CreateCustomEnchantedBook(Enchantment enchantment, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        book.setItemMeta(meta);
        return book;
    }

    public boolean isNumber(String PotentialNumber) {
        try {
            Integer.parseInt(PotentialNumber);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isInFaction(Player player) {
        return FPlayers.getInstance().getByPlayer(player).hasFaction();
    }


    public void MessageFactionMembers(Faction faction, String message) {
        List<Player> FactionMembers = faction.getOnlinePlayers();
        for (Player PlayerInFaction : FactionMembers) {
            PlayerInFaction.sendMessage(message);
        }
    }

    public void GivePlayerItem(Player player, ItemStack item, Integer amount) {
        for (int i = 0; i < amount; i ++) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
                if (i == amount - 1) {
                    return;
                }
            } else {
                for (ItemStack invItem : player.getInventory()) {
                    ItemStack SingleItem = new ItemStack(invItem);
                    SingleItem.setAmount(1);
                    if (invItem.equals(item)) {
                        if (invItem.getAmount() < 64) {
                            player.getInventory().addItem(item);
                            if (i == amount - 1) {
                                return;
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < amount; i++) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    public boolean enemyIsNearby(Player player, Integer range) {
        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (entity instanceof Player && isInFaction((Player) entity) && !staffMain.isStaff((Player) entity)) {
                Faction OtherPlayersFaction = FPlayers.getInstance().getByPlayer((Player) entity).getFaction();
                Faction ThisPlayersFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
                if (!ThisPlayersFaction.equals(OtherPlayersFaction) || !isInFaction((Player) entity)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInEnemyTerritory(Player player, Location location) {
        FLocation fLocation = new FLocation(location);
        Faction PlayerFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
        Faction FactionAtLocation = Board.getInstance().getFactionAt(fLocation);
        return !(PlayerFaction.equals(FactionAtLocation) || FactionAtLocation.isWilderness());
    }
}
