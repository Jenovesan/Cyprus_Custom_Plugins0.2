package Kits;

import CustomItems.CustomItemsMain;
import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitsMain implements Listener {
    UtilityMain utilityMain = new UtilityMain();
    MainClass mainClass;
    KitsGui kitsGui;
    CustomItemsMain customItemsMain = new CustomItemsMain();
    public KitsMain(MainClass mc) {
        mainClass = mc;
        kitsGui = new KitsGui(mainClass);
    }

    @EventHandler
    private void ClickedKitsGui(InventoryClickEvent event) {
        if (event.getSlot() == -999 || !event.getInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Kits") || event.getCurrentItem() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Material type = item.getType();
        if (type.equals(Material.STAINED_GLASS_PANE)) {
            event.setCancelled(true);
        }
        if (type.equals(Material.RABBIT_FOOT)) {
            ClickedTraveler(player);
        } else if (type.equals(Material.FEATHER) && player.hasPermission("kit.novice")) {
            ClickedNovice(player);
        } else if (type.equals(Material.GHAST_TEAR) && player.hasPermission("kit.elite")) {
            ClickedElite(player);
        } else if (type.equals(Material.DOUBLE_PLANT) && player.hasPermission("kit.legion")) {
            ClickedLegion(player);
        } else if (type.equals(Material.NETHER_STALK) && player.hasPermission("kit.spartan")) {
            ClickedSpartan(player);
        } else if (type.equals(Material.NETHER_STAR) && player.hasPermission("kit.titan")) {
            ClickedTitan(player);
        } else if (type.equals(Material.PRISMARINE_SHARD) && player.hasPermission("kit.olympian")) {
            ClickedOlympian(player);
        } else if (type.equals(Material.DIAMOND_SWORD) && player.hasPermission("kit.juggernaut")) {
            ClickedJuggernaut(player);
        } else if (type.equals(Material.IRON_SWORD) && player.hasPermission("kit.ravager")) {
            ClickedRavager(player);
        } else if (type.equals(Material.GOLD_AXE) && player.hasPermission("kit.viking")) {
            ClickedViking(player);
        } else if (type.equals(Material.DIAMOND_CHESTPLATE) && player.hasPermission("kit.tank")) {
            ClickedTank(player);
        } else if (type.equals(Material.MONSTER_EGG) && player.hasPermission("kit.raiding")) {
            ClickedRaiding(player);
        } else if (type.equals(Material.EXP_BOTTLE) && player.hasPermission("kit.enchanter")) {
            ClickedEnchanter(player);
        } else if (type.equals(Material.BOW) && player.hasPermission("kit.archer")) {
            ClickedArcher(player);
        } else if (type.equals(Material.GOLD_HELMET) && player.hasPermission("kit.bard")) {
            ClickedBard(player);
        } else if (type.equals(Material.SUGAR_CANE) && player.hasPermission("kit.sotw")) {
            ClickedSOTW(player);
        } else if (type.equals(Material.DIAMOND_PICKAXE) && player.hasPermission("kit.tools")) {
            ClickedTools(player);
        }
        event.setCancelled(true);
    }

    public HashMap<UUID, Integer> Tools = new HashMap<>();
    private void ClickedTools(Player player) {
        UUID uuid = player.getUniqueId();
        if (Tools.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveTools(player);
        Tools.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveTools(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_PICKAXE, null, 10, null), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_AXE, null, 10, null), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SPADE, null, 10, null), 1);
    }
    
    public HashMap<UUID, Integer> SOTW = new HashMap<>();
    private void ClickedSOTW(Player player) {
        UUID uuid = player.getUniqueId();
        if (SOTW.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveSOTW(player);
        SOTW.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveSOTW(Player player) {
        utilityMain.GivePlayerItem(player, customItemsMain.trenchPickaxe(11), 1);
        utilityMain.GivePlayerItem(player, customItemsMain.HarvesterHoe(1), 1);
        utilityMain.GivePlayerItem(player, customItemsMain.ChunkBuster(), 10);
        utilityMain.GivePlayerItem(player, customItemsMain.SellWand(null), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.HOPPER), 16);
        Economy economy = MainClass.getEconomy();
        economy.depositPlayer(player, 250000);
    }

    public HashMap<UUID, Integer> Bard = new HashMap<>();
    private void ClickedBard(Player player) {
        UUID uuid = player.getUniqueId();
        if (Bard.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveBard(player);
        Bard.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveBard(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.GOLD_HELMET, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Bard Helmet", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.GOLD_CHESTPLATE, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Bard Chestplate", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.GOLD_LEGGINGS, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Bard leggings", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.GOLD_BOOTS, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Bard Boots", 6, 6), 1);
        utilityMain.GivePlayerItem(player, customItemsMain.Energizer(), 3);
    }
    
    public HashMap<UUID, Integer> Archer = new HashMap<>();
    private void ClickedArcher(Player player) {
        UUID uuid = player.getUniqueId();
        if (Archer.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveArcher(player);
        Archer.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveArcher(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.LEATHER_HELMET, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Archer Helmet", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.LEATHER_CHESTPLATE, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Archer Chestplate", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.LEATHER_LEGGINGS, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Archer leggings", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.LEATHER_BOOTS, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Archer Boots", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createCustomBow(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Archer Bow", 8, null, 2, 5, 1), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.ARROW), 1);
    }
    
    public HashMap<UUID, Integer> Raiding = new HashMap<>();
    private void ClickedRaiding(Player player) {
        UUID uuid = player.getUniqueId();
        if (Raiding.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveRaiding(player);
        Raiding.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveRaiding(Player player) {
        utilityMain.GivePlayerItem(player, customItemsMain.SuperCegg(), 4);
        mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "creeperegg give " + player.getName() + " 8");
        utilityMain.GivePlayerItem(player, new ItemStack(Material.MONSTER_EGG, 1, (short) 50), 16);
        ItemStack fish = new ItemStack(Material.RAW_FISH, 1, (short) 2);
        fish.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
        utilityMain.GivePlayerItem(player, fish, 1);
        utilityMain.GivePlayerItem(player, customItemsMain.LightningRod(), 1);
        utilityMain.GivePlayerItem(player, customItemsMain.SandBot(), 2);
        mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "voucher give tnt500 1 " + player.getName());
    }

    public HashMap<UUID, Integer> Enchanter = new HashMap<>();
    private void ClickedEnchanter(Player player) {
        UUID uuid = player.getUniqueId();
        if (Enchanter.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveEnchanter(player);
        Enchanter.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveEnchanter(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createGuiItem(Material.BOOK, ChatColor.YELLOW + "" + ChatColor.BOLD + "Legendary Book"), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createGuiItem(Material.BOOK, ChatColor.BLUE + "" + ChatColor.BOLD + "Rare Book"), 2);
        utilityMain.GivePlayerItem(player, customItemsMain.ExpSword(), 1);
        player.giveExp(5359);
    }

    public HashMap<UUID, Integer> Tank = new HashMap<>();
    private void ClickedTank(Player player) {
        UUID uuid = player.getUniqueId();
        if (Tank.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveTank(player);
        Tank.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveTank(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.DARK_AQUA + "" + ChatColor.DARK_AQUA + "Tank Helmet", 4, 10), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Tank Chestplate", 4, 10), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Tank leggings", 4, 10), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Tank Boots", 4, 10), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Tank Sword", 5, 10), 1);
    }

    public HashMap<UUID, Integer> Viking = new HashMap<>();
    private void ClickedViking(Player player) {
        UUID uuid = player.getUniqueId();
        if (Viking.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveViking(player);
        Viking.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveViking(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.YELLOW + "" + ChatColor.BOLD + "Viking Helmet", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.YELLOW + "" + ChatColor.BOLD + "Viking Chestplate", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.YELLOW + "" + ChatColor.BOLD + "Viking leggings", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.YELLOW + "" + ChatColor.BOLD + "Viking Boots", 6, 5), 1);
        ItemStack VikingAxe = new ItemStack(utilityMain.createGuiItem(Material.DIAMOND_AXE, ChatColor.YELLOW + "" + ChatColor.BOLD + "Viking Axe"));
        ItemMeta meta = VikingAxe.getItemMeta();
        meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
        meta.addEnchant(Enchantment.DURABILITY, 5, true);
        VikingAxe.setItemMeta(meta);
        utilityMain.GivePlayerItem(player, VikingAxe,1);
    }
    
    public HashMap<UUID, Integer> Ravager = new HashMap<>();
    private void ClickedRavager(Player player) {
        UUID uuid = player.getUniqueId();
        if (Ravager.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveRavager(player);
        Ravager.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveRavager(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.RED + "" + ChatColor.BOLD + "Ravager Helmet", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.RED + "" + ChatColor.BOLD + "Ravager Chestplate", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.RED + "" + ChatColor.BOLD + "Ravager leggings", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.RED + "" + ChatColor.BOLD + "Ravager Boots", 5, 5), 1);
        ItemStack RavagerSword = new ItemStack(utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.RED + "" + ChatColor.BOLD + "Ravager Sword", 5, 5));
        ItemMeta meta = RavagerSword.getItemMeta();
        meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        RavagerSword.setItemMeta(meta);
        utilityMain.GivePlayerItem(player, RavagerSword,1);
    }
    
    public HashMap<UUID, Integer> Juggernaut = new HashMap<>();
    private void ClickedJuggernaut(Player player) {
        UUID uuid = player.getUniqueId();
        if (Juggernaut.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveJuggernaut(player);
        Juggernaut.put(uuid, 4320);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveJuggernaut(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.GOLD + "" + ChatColor.BOLD + "Juggernaut Helmet", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.GOLD + "" + ChatColor.BOLD + "Juggernaut Chestplate", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.GOLD + "" + ChatColor.BOLD + "Juggernaut leggings", 6, 6), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.GOLD + "" + ChatColor.BOLD + "Juggernaut Boots", 6, 6), 1);
        ItemStack JuggernautSword = new ItemStack(utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.GOLD + "" + ChatColor.BOLD + "Juggernaut Sword", 6, 6));
        ItemMeta meta = JuggernautSword.getItemMeta();
        meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        JuggernautSword.setItemMeta(meta);
        utilityMain.GivePlayerItem(player, JuggernautSword,1);
    }

    public HashMap<UUID, Integer> Olympian = new HashMap<>();
    private void ClickedOlympian(Player player) {
        UUID uuid = player.getUniqueId();
        if (Olympian.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveOlympian(player);
        Olympian.put(uuid, 1440);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveOlympian(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Helmet", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Chestplate", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Leggings", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Boots", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Sword", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SPADE, ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Shovel", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_PICKAXE, ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Pickaxe", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_AXE, ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Axe", 6, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createCustomBow(ChatColor.AQUA + "" + ChatColor.BOLD + "Olym" + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "pian" + ChatColor.AQUA + "" + ChatColor.BOLD + " Bow", 6, null, 1, 5, 1), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 10);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.COOKED_BEEF), 64);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.ARROW), 64);
    }

    public HashMap<UUID, Integer> Spartan = new HashMap<>();
    private void ClickedSpartan(Player player) {
        UUID uuid = player.getUniqueId();
        if (Spartan.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveSpartan(player);
        Spartan.put(uuid, 1440);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveSpartan(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan Helmet", 4, 4), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan Chestplate", 4, 4), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan leggings", 4, 4), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan Boots", 4, 4), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan Sword", 4, 4), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SPADE, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan Shovel", 4, 4), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_PICKAXE, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan Pickaxe", 4, 4), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_AXE, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan Axe", 4, 4), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.BOW, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spartan Bow", 4, 4), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 3);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.COOKED_BEEF), 64);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.ARROW), 64);
    }

    public HashMap<UUID, Integer> Titan = new HashMap<>();
    private void ClickedTitan(Player player) {
        UUID uuid = player.getUniqueId();
        if (Titan.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveTitan(player);
        Titan.put(uuid, 1440);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveTitan(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.RED + "" + ChatColor.BOLD + "Titan Helmet", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.RED + "" + ChatColor.BOLD + "Titan Chestplate", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.RED + "" + ChatColor.BOLD + "Titan leggings", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.RED + "" + ChatColor.BOLD + "Titan Boots", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.RED + "" + ChatColor.BOLD + "Titan Sword", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SPADE, ChatColor.RED + "" + ChatColor.BOLD + "Titan Shovel", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_PICKAXE, ChatColor.RED + "" + ChatColor.BOLD + "Titan Pickaxe", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_AXE, ChatColor.RED + "" + ChatColor.BOLD + "Titan Axe", 5, 5), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createCustomBow(ChatColor.RED + "Titan Bow", 5, null, 1, 5, 1), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 5);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.COOKED_BEEF), 64);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.ARROW), 64);
    }

    public HashMap<UUID, Integer> Legion = new HashMap<>();
    private void ClickedLegion(Player player) {
        UUID uuid = player.getUniqueId();
        if (Legion.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveLegion(player);
        Legion.put(uuid, 1440);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveLegion(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.YELLOW + "" + ChatColor.BOLD + "Legion Helmet", 3, 3), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.YELLOW + "" + ChatColor.BOLD + "Legion Chestplate", 3, 3), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.YELLOW + "" + ChatColor.BOLD + "Legion leggings", 3, 3), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.YELLOW + "" + ChatColor.BOLD + "Legion Boots", 3, 3), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.YELLOW + "" + ChatColor.BOLD + "Legion Sword", 3, 3), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SPADE, ChatColor.YELLOW + "" + ChatColor.BOLD + "Legion Shovel", 3, 3), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_PICKAXE, ChatColor.YELLOW + "Legion Pickaxe", 3, 3), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_AXE, ChatColor.YELLOW + "" + ChatColor.BOLD + "Legion Axe", 3, 3), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 10);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.GOLDEN_APPLE), 10);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.COOKED_BEEF), 64);
    }

    public HashMap<UUID, Integer> Novice = new HashMap<>();
    private void ClickedNovice(Player player) {
        UUID uuid = player.getUniqueId();
        if (Novice.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveNovice(player);
        Novice.put(uuid, 1440);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveNovice(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.BLUE + "" + ChatColor.BOLD + "Novice Helmet", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.BLUE + "" + ChatColor.BOLD + "Novice Chestplate", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.BLUE + "" + ChatColor.BOLD + "Novice leggings", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.BLUE + "" + ChatColor.BOLD + "Novice Boots", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.BLUE + "" + ChatColor.BOLD + "Novice Sword", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SPADE, ChatColor.BLUE + "" + ChatColor.BOLD + "Novice Shovel", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_PICKAXE, ChatColor.BLUE + "" + ChatColor.BOLD + "Novice Pickaxe", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_AXE, ChatColor.BLUE + "" + ChatColor.BOLD + "Novice Axe", 1, 1), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.GOLDEN_APPLE), 3);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.COOKED_BEEF), 64);
    }

    public HashMap<UUID, Integer> Elite = new HashMap<>();
    private void ClickedElite(Player player) {
        UUID uuid = player.getUniqueId();
        if (Elite.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveElite(player);
        Elite.put(uuid, 1440);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveElite(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_HELMET, ChatColor.AQUA + "" + ChatColor.BOLD + "Elite Helmet", 2, 2), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_CHESTPLATE, ChatColor.AQUA + "" + ChatColor.BOLD + "Elite Chestplate", 2, 2), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_LEGGINGS, ChatColor.AQUA + "" + ChatColor.BOLD + "Elite leggings", 2, 2), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_BOOTS, ChatColor.AQUA + "" + ChatColor.BOLD + "Elite Boots", 2, 2), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.AQUA + "" + ChatColor.BOLD + "Elite Sword", 2, 2), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SPADE, ChatColor.AQUA + "" + ChatColor.BOLD + "Elite Shovel", 2, 2), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_PICKAXE, ChatColor.AQUA + "" + ChatColor.BOLD + "Elite Pickaxe", 2, 2), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_AXE, ChatColor.AQUA + "" + ChatColor.BOLD + "Elite Axe", 2, 2), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.GOLDEN_APPLE), 10);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.COOKED_BEEF), 64);
    }

    public HashMap<UUID, Integer> Traveler = new HashMap<>();
    private void ClickedTraveler(Player player) {
        UUID uuid = player.getUniqueId();
        if (Traveler.containsKey(uuid)) {
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        GiveTraveler(player);
        Traveler.put(uuid, 1440);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        kitsGui.LoadGui(player);
        SaveKitsInConfig();
    }

    private void GiveTraveler(Player player) {
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.IRON_HELMET, ChatColor.GRAY + "" + ChatColor.BOLD + "Traveler Helmet", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.IRON_CHESTPLATE, ChatColor.GRAY + "" + ChatColor.BOLD + "Traveler Chestplate", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.IRON_LEGGINGS, ChatColor.GRAY + "" + ChatColor.BOLD + "Traveler leggings", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.IRON_BOOTS, ChatColor.GRAY + "" + ChatColor.BOLD + "Traveler Boots", 1, 1), 1);
        utilityMain.GivePlayerItem(player, utilityMain.createEnchantedItem(Material.DIAMOND_SWORD, ChatColor.GRAY + "" + ChatColor.BOLD + "Traveler Sword", 1, 1), 1);
        utilityMain.GivePlayerItem(player, new ItemStack(Material.COOKED_BEEF), 64);
    }

    public void KitTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Integer> element : Traveler.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Traveler.remove(uuid);
                    } else {
                        Traveler.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Novice.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Novice.remove(uuid);
                    } else {
                        Novice.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Elite.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Elite.remove(uuid);
                    } else {
                        Elite.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Legion.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Legion.remove(uuid);
                    } else {
                        Legion.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Spartan.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Spartan.remove(uuid);
                    } else {
                        Spartan.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Titan.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Titan.remove(uuid);
                    } else {
                        Titan.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Olympian.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Olympian.remove(uuid);
                    } else {
                        Olympian.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Juggernaut.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Juggernaut.remove(uuid);
                    } else {
                        Juggernaut.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Ravager.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Ravager.remove(uuid);
                    } else {
                        Ravager.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Viking.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Viking.remove(uuid);
                    } else {
                        Viking.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Tank.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Tank.remove(uuid);
                    } else {
                        Tank.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Raiding.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Raiding.remove(uuid);
                    } else {
                        Raiding.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Enchanter.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Enchanter.remove(uuid);
                    } else {
                        Enchanter.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Archer.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Archer.remove(uuid);
                    } else {
                        Archer.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Bard.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Bard.remove(uuid);
                    } else {
                        Bard.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : SOTW.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        SOTW.remove(uuid);
                    } else {
                        SOTW.replace(uuid, time - 1);
                    }
                }
                for (Map.Entry<UUID, Integer> element : Tools.entrySet()) {
                    UUID uuid = element.getKey();
                    int time = element.getValue();
                    if (time <= 1) {
                        Tools.remove(uuid);
                    } else {
                        Tools.replace(uuid, time - 1);
                    }
                }
                SaveKitsInConfig();
            }
        }.runTaskTimerAsynchronously(mainClass, 0, 1200);
    }

    public void SaveKitsInConfig() {
        List<String> TravelerList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Traveler.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            TravelerList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Traveler", TravelerList);
        List<String> NoviceList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Novice.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            NoviceList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Novice", NoviceList);
        List<String> EliteList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Elite.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            EliteList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Elite", EliteList);
        List<String> LegionList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Legion.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            LegionList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Legion", LegionList);
        List<String> SpartanList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Spartan.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            SpartanList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Spartan", SpartanList);
        List<String> TitanList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Titan.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            TitanList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Titan", TitanList);
        List<String> OlympianList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Olympian.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            OlympianList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Olympian", OlympianList);
        List<String> JuggernautList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Juggernaut.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            JuggernautList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Juggernaut", JuggernautList);
        List<String> RavagerList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Ravager.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            RavagerList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Ravager", RavagerList);
        List<String> VikingList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Viking.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            VikingList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Viking", VikingList);
        List<String> TankList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Tank.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            TankList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Tank", TankList);
        List<String> RaidingList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Raiding.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            RaidingList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Raiding", RaidingList);
        List<String> EnchanterList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Enchanter.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            EnchanterList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Enchanter", EnchanterList);
        List<String> ArcherList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Archer.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            ArcherList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Archer", ArcherList);
        List<String> BardList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Bard.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            BardList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Bard", BardList);
        List<String> SOTWList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : SOTW.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            SOTWList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("SOTW", SOTWList);
        List<String> ToolsList = new ArrayList<>();
        for (Map.Entry<UUID, Integer> element : Tools.entrySet()) {
            String uuid = element.getKey().toString();
            String time = String.valueOf(element.getValue());
            ToolsList.add(uuid + ":" + time);
        }
        mainClass.getConfig().set("Tools", ToolsList);
        mainClass.saveConfig();
    }

    public void LoadKitsFromConfig() {
        List<String> TravelerList = mainClass.getConfig().getStringList("Traveler");
        for (String string : TravelerList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Traveler.put(uuid, time);
        }
        List<String> NoviceList = mainClass.getConfig().getStringList("Novice");
        for (String string : NoviceList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Novice.put(uuid, time);
        }
        List<String> EliteList = mainClass.getConfig().getStringList("Elite");
        for (String string : EliteList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Elite.put(uuid, time);
        }
        List<String> LegionList = mainClass.getConfig().getStringList("Legion");
        for (String string : LegionList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Legion.put(uuid, time);
        }
        List<String> SpartanList = mainClass.getConfig().getStringList("Spartan");
        for (String string : SpartanList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Spartan.put(uuid, time);
        }
        List<String> TitanList = mainClass.getConfig().getStringList("Titan");
        for (String string : TitanList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Titan.put(uuid, time);
        }
        List<String> OlympianList = mainClass.getConfig().getStringList("Olympian");
        for (String string : OlympianList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Olympian.put(uuid, time);
        }
        List<String> JuggernautList = mainClass.getConfig().getStringList("Juggernaut");
        for (String string : JuggernautList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Juggernaut.put(uuid, time);
            Juggernaut.put(uuid, time);
        }
        List<String> RavagerList = mainClass.getConfig().getStringList("Ravager");
        for (String string : RavagerList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Ravager.put(uuid, time);
        }
        List<String> VikingList = mainClass.getConfig().getStringList("Viking");
        for (String string : VikingList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Viking.put(uuid, time);
        }
        List<String> TankList = mainClass.getConfig().getStringList("Tank");
        for (String string : TankList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Tank.put(uuid, time);
        }
        List<String> RaidingList = mainClass.getConfig().getStringList("Raiding");
        for (String string : RaidingList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Raiding.put(uuid, time);
        }
        List<String> EnchanterList = mainClass.getConfig().getStringList("Enchanter");
        for (String string : EnchanterList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Enchanter.put(uuid, time);
        }
        List<String> ArcherList = mainClass.getConfig().getStringList("Archer");
        for (String string : ArcherList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Archer.put(uuid, time);
        }
        List<String> BardList = mainClass.getConfig().getStringList("Bard");
        for (String string : BardList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Bard.put(uuid, time);
        }
        List<String> SOTWList = mainClass.getConfig().getStringList("SOTW");
        for (String string : SOTWList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            SOTW.put(uuid, time);
        }
        List<String> ToolsList = mainClass.getConfig().getStringList("Tools");
        for (String string : ToolsList) {
            String[] SplitString = string.split(":");
            UUID uuid = UUID.fromString(SplitString[0]);
            Integer time = Integer.parseInt(SplitString[1]);
            Tools.put(uuid, time);
        }
    }
}
