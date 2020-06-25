package CustomItems;

import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.ItemSword;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomItemsMain {
    UtilityMain utilityMain = new UtilityMain();

    public ItemStack KingsSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ChatColor.MAGIC + "-" + ChatColor.YELLOW +  " " + ChatColor.BOLD + "King's Sword " + ChatColor.GOLD + "" + ChatColor.BOLD + ChatColor.MAGIC + "-");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 7, true);
        meta.spigot().setUnbreakable(true);
        sword.setItemMeta(meta);
        return sword;
    }

    public ItemStack QueensAxe() {
        ItemStack Axe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = Axe.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ChatColor.MAGIC + "-" + ChatColor.LIGHT_PURPLE +  " " + ChatColor.BOLD + "Queen's Axe " + ChatColor.GOLD + "" + ChatColor.BOLD + ChatColor.MAGIC + "-");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 8, true);
        meta.spigot().setUnbreakable(true);
        Axe.setItemMeta(meta);
        return Axe;
    }

    public ItemStack AutoSellGem() {
        ItemStack gem = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = gem.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Auto Sell Gem");
        lore.add(ChatColor.YELLOW + "Drag and drop on an item to apply auto sell");
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 0, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);
        gem.setItemMeta(meta);
        return gem;
    }

    public ItemStack SellBoostNote(int Percentage, int Time) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + Percentage + "% Sell Boost " + ChatColor.YELLOW + "(" + Time + " hours)");
        lore.add(ChatColor.GRAY + "Right click to apply the sell boost");
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 0, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack XpBoost(int Percentage, int Time) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (Time != 1) {
            meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + Percentage + "% Xp Boost " + ChatColor.YELLOW + "(" + Time + " hours)");
        }
        else {
            meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+" + Percentage + "% Xp Boost " + ChatColor.YELLOW + "(" + Time + " hour)");
        }
        lore.add(ChatColor.GRAY + "Right click to apply the xp boost");
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 0, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack SuperCegg() {
        String name = ChatColor.GREEN + "" + ChatColor.BOLD + "Throwable Super Creeper Egg";
        return utilityMain.createGuiItemFromItemStack(new ItemStack(Material.MONSTER_EGG, 1, EntityType.CREEPER.getTypeId()), name, "");
    }

    public ItemStack trenchPickaxe(int strength) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GRAY + "Use this tool to clear large areas");
        list.add(ChatColor.GRAY + "fast or use on netherrack to clear");
        list.add(ChatColor.GRAY + "nearby netherrack out!");
        meta.setDisplayName(ChatColor.RED + "Factions Pickaxe " + ChatColor.GRAY + "(" + ChatColor.RED + strength + ChatColor.GRAY + "x" + ChatColor.RED + strength + ChatColor.GRAY + ")");
        meta.setLore(list);
        meta.spigot().setUnbreakable(true);
        itemStack.setItemMeta(meta);
        itemStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
        return itemStack;
    }

    public ItemStack SandBot() {
        String name = ChatColor.YELLOW + "" + ChatColor.BOLD + "Sand Bot";
        ItemStack item = new ItemStack(Material.MONSTER_EGG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack CaneBot() {
        String name = ChatColor.GREEN + "" + ChatColor.BOLD + "Cane Bot";
        ItemStack item = new ItemStack(Material.MONSTER_EGG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack SellWand(Integer uses) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        String name = ChatColor.YELLOW + "" + ChatColor.BOLD + "Sell Wand";
        if (uses != null) {
            name = name + ChatColor.GOLD + " (" + uses + ")";
        }
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click on a chest to automatically sell its contents");
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Automatically adds tnt into your faction's tnt bank");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
        meta.addItemFlags();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wand.setItemMeta(meta);
        return wand;
    }

    public ItemStack ChunkBuster() {
        ItemStack itemStack = new ItemStack(Material.ENDER_PORTAL_FRAME);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Chunk Buster");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Place in your territory to destroy the chunk");
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack LightningRod() {
        return utilityMain.createGuiItem(Material.BLAZE_ROD, ChatColor.YELLOW + "" + ChatColor.BOLD + "Lightning Rod");
    }

    public ItemStack ExpSword() {
        ItemStack sword = utilityMain.createGuiItem(Material.DIAMOND_SWORD, ChatColor.RED + "" + ChatColor.BOLD + "EXP Sword" + ChatColor.GRAY + " +15%");
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
        return sword;
    }

    public ItemStack Energizer() {
        return utilityMain.createGuiItem(Material.DOUBLE_PLANT,ChatColor.YELLOW + "" + ChatColor.BOLD + "Energizer", ChatColor.GRAY + "" + ChatColor.ITALIC + "Right click to restore all your bard energy");
    }

    public ItemStack HarvesterHoe(Integer level) {
        ItemStack HarvesterHoeItem = new ItemStack(Material.DIAMOND_HOE, 1);
        ItemMeta HarvesterHoeMeta = HarvesterHoeItem.getItemMeta();
        HarvesterHoeMeta.setDisplayName(ChatColor.RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Harvester Hoe" + ChatColor.RED + " " + ChatColor.GRAY + "(" + ChatColor.RED + ChatColor.BOLD + level + ChatColor.GRAY + ")");
        List<String> HarvesterHoeLore = new ArrayList<>();
        HarvesterHoeMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, false);
        HarvesterHoeLore.add("");
        HarvesterHoeLore.add(ChatColor.YELLOW + "Level " + level + " " + ChatColor.GRAY + "||||||||||||||||||||||||||||||||||||||||||||||||||" + ChatColor.YELLOW + " Level " + (level + 1));
        HarvesterHoeLore.add("");
        HarvesterHoeLore.add(DropRates(level));
        HarvesterHoeLore.add(ChatColor.GRAY + "Cane Broken: " + ChatColor.RED + CaneDrop(level));
        HarvesterHoeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        HarvesterHoeMeta.setLore(HarvesterHoeLore);
        HarvesterHoeItem.setItemMeta(HarvesterHoeMeta);
        return HarvesterHoeItem;
    }

    private String CaneDrop(Integer level) {
        int total = 0;
        for (int i = 1; i < level; i++)
        {
            total = total + ((int) (1000 * (5 * Math.pow(i, 2))));
        }
        return Integer.toString(total);
    }

    private String DropRates(Integer level) {
        switch (level) {
            case 1:
                return ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "5%";
            case 2:
                return ChatColor.GRAY+ "Double Yield: " + ChatColor.RED + "15%";
            case 3:
                return ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "30%";
            case 4:
                return ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "55%";
            case 5:
                return ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "100%";
            case 6:
                return ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "5%";
            case 7:
                return ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "15%";
            case 8:
                return ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "30%";
            case 9:
                return ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "55%";
            case 10:
                return ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "100%";
            case 11:
                return ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "5%";
            case 12:
                return ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "15%";
            case 13:
                return ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "30%";
            case 14:
                return ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "55%";
            case 15:
                return ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "100%";
        }
        return ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "5%";
    }
}