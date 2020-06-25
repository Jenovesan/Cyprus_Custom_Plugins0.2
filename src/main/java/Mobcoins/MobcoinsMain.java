package Mobcoins;

import CustomItems.CustomItemsMain;
import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class MobcoinsMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    CustomItemsMain customItemsMain = new CustomItemsMain();
    public MobcoinsMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("mobshop")) {
            LoadMobCoinGui((Player) sender);
            return true;
        }
        if (sender.isOp()) {
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (Bukkit.getPlayerExact(args[1]) != null) {
                        Player target = Bukkit.getPlayerExact(args[1]);
                        if (utilityMain.isNumber(args[2])) {
                            GiveMobCoin(target, Integer.parseInt(args[2]));
                            sender.sendMessage(ChatColor.GREEN + "You gave " + args[1] + " " + args[2] + " mob coins");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED  + args[2] + " is not a number");
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + args[1] + " is not a valid player");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.GRAY + "Usage: /mobcoins give <Player Name> <Amount>  a");
                }
                return true;
            }
            else if (args.length != 0){
                sender.sendMessage(ChatColor.GRAY + "Usage: /mobcoins give <Player Name> <Amount>  b");
            }
        }
        if (cmd.getName().equalsIgnoreCase("mobcoins")) {
            sender.sendMessage(ChatColor.RED + "You have: " + ChatColor.DARK_RED + utilityMain.FormatPrice(getMobCoinAmount((Player) sender)) + ChatColor.RED + " mob coins");
        }
        return true;
    }

    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Mob Shop"))) {
            return;
        }
        if (event.getSlot() == -999) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack CurrentItem = event.getCurrentItem();
        if (CurrentItem.getType().equals(Material.BARRIER)) {
            player.closeInventory();
            return;
        }
        if (CostOfItem(CurrentItem) != 0) {
            LoadMobCoinsHashMap();
            if (getMobCoinAmount(player) >= CostOfItem(CurrentItem)) {
                GiveItemToPlayer(player, CurrentItem);
                LoadMobCoinGui(player);
            }
            else {
                player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 10, 10);
            }
        }
    }

    private void GiveItemToPlayer(Player player, ItemStack itemStack) {
        TakeMobCoin(player,  CostOfItem(itemStack));
        ItemStack item = new ItemStack(itemStack);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (String string : lore) {
            if (string.contains("Cost")) {
                lore.remove(string);
                break;
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        utilityMain.GivePlayerItem(player, item, 1);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1,1);
    }

    private int CostOfItem(ItemStack item) {
        if (item == null) {
            return 0;
        }
        if (!(item.hasItemMeta())) {
            return 0;
        }
        if (!(item.getItemMeta().hasLore())) {
            return 0;
        }
        for (String lore : item.getItemMeta().getLore())  {
            if (lore.contains("Cost")) {
                String reduced = ChatColor.stripColor(lore.replace("Cost: ", "").replace(",", ""));
                return Integer.parseInt(reduced);
            }
        }
        return 0;
    }


    private Integer getMobCoinAmount(Player player) {
        LoadMobCoinsHashMap();
        if (MobCoins.containsKey(player.getUniqueId().toString())) {
            return MobCoins.get(player.getUniqueId().toString());
        }
        return 0;
    }

    private void LoadMobCoinGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 54, ChatColor.RED + "" + ChatColor.BOLD + "Mob Shop");
        FillMobCoinShopGui(inv);
        SetItemsInMobShopGui(inv, player);
        player.openInventory(inv);
    }

    private void SetItemsInMobShopGui(Inventory inv, Player player) {
        inv.setItem(2, utilityMain.createGuiItemFromItemStack(utilityMain.CustomPotion("Potion of Strength", PotionType.STRENGTH, PotionEffectType.INCREASE_DAMAGE, 12000, 0), null,  ChatColor.RED + "Cost: 5,000"));
        inv.setItem(4, utilityMain.createGuiItemFromItemStack(utilityMain.CustomPotion("Potion of Switftness", PotionType.SPEED, PotionEffectType.SPEED, 12000, 1), null,  ChatColor.RED + "Cost: 5,000"));
        inv.setItem(6, utilityMain.createGuiItemFromItemStack(utilityMain.CustomPotion("Potion of Health Boost", PotionType.JUMP, PotionEffectType.HEALTH_BOOST, 12000, 0), null,  ChatColor.RED + "Cost: 5,000"));
        inv.setItem(10, utilityMain.createGuiItemFromItemStack(utilityMain.createGuiItem(Material.ENCHANTED_BOOK,ChatColor.GREEN + "" + ChatColor.BOLD + "Strength", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent strength 2", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"), null, ChatColor.RED + "Cost: 50,000"));
        inv.setItem(12, utilityMain.createGuiItemFromItemStack(utilityMain.createGuiItem(Material.ENCHANTED_BOOK,ChatColor.GREEN + "" + ChatColor.BOLD + "Speed", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent speed 2", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"), null, ChatColor.RED + "Cost: 50,000"));
        inv.setItem(14, utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.DEPTH_STRIDER, 3), null, ChatColor.RED + "Cost: 15,000"));
        inv.setItem(16, utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.PROTECTION_FALL, 3), null, ChatColor.RED + "Cost: 10,000"));
        inv.setItem(20, utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.PROTECTION_ENVIRONMENTAL, 5), null, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Drag and drop on a piece of gear", ChatColor.YELLOW + "" + ChatColor.ITALIC + "to apply the enchant", ChatColor.RED + "Cost: 25,000"));
        inv.setItem(22, utilityMain.createGuiItemFromItemStack(customItemsMain.XpBoost(100, 1), null, ChatColor.RED + "Cost: 50,000"));
        inv.setItem(24, utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.PROTECTION_ENVIRONMENTAL, 4), null, ChatColor.RED + "Cost: 5,000"));
        inv.setItem(28, utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.DURABILITY, 5), null, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Drag and drop on a piece of gear", ChatColor.YELLOW + "" + ChatColor.ITALIC + "to apply the enchant", ChatColor.RED + "Cost: 30,000"));
        inv.setItem(30, utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.DURABILITY, 4), null, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Drag and drop on a piece of gear", ChatColor.YELLOW + "" + ChatColor.ITALIC + "to apply the enchant", ChatColor.RED + "Cost: 15,000"));
        inv.setItem(32, utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.DAMAGE_ALL, 5), null, ChatColor.RED + "Cost: 15,000"));
        inv.setItem(34, utilityMain.createGuiItemFromItemStack(utilityMain.CreateCustomEnchantedBook(Enchantment.ARROW_DAMAGE, 5), null, ChatColor.RED + "Cost: 15,000"));
        inv.setItem(40, utilityMain.createGuiItemFromItemStack(customItemsMain.KingsSword(), null, ChatColor.RED + "Cost: 300,000"));
        inv.setItem(45, utilityMain.createGuiItem(Material.BARRIER, ChatColor.RED + "Exit"));
        inv.setItem(53, utilityMain.createGuiItem(Material.PRISMARINE_CRYSTALS, ChatColor.DARK_RED + "Mob Coins",ChatColor.RED + utilityMain.FormatPrice(getMobCoinAmount(player))));
    }

    private void FillMobCoinShopGui(Inventory inv) {
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack Red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Black);
        }
        inv.setItem(3, Red);
        inv.setItem(5, Red);
        inv.setItem(11, Red);
        inv.setItem(13, Red);
        inv.setItem(15, Red);
        inv.setItem(21, Red);
        inv.setItem(23, Red);
        inv.setItem(29, Red);
        inv.setItem(33, Red);
        inv.setItem(39, Red);
        inv.setItem(41, Red);
        inv.setItem(49, Red);
    }


    HashMap<String, Integer> MobCoins = new HashMap<>();
    private void LoadMobCoinsHashMap() {
        List<String> strings = mainClass.getConfig().getStringList("MobCoins");
        for (String string : strings) {
            String[] SplitString = string.split(":");
            MobCoins.put(SplitString[0], Integer.parseInt(SplitString[1]));
        }
    }

    private void SaveMobCoinsHashMap() {
        List<String> Empty = new ArrayList<>();
        for (Map.Entry element : MobCoins.entrySet()) {
            String uuid = (String) element.getKey();
            Integer count = (Integer) element.getValue();
            Empty.add(uuid + ":" + count);
        }
        mainClass.getConfig().set("MobCoins", Empty);
        mainClass.saveConfig();
    }

    public void GiveMobCoin(Player player, int amount) {
        LoadMobCoinsHashMap();
        String uuid = player.getUniqueId().toString();
        if (MobCoins.containsKey(uuid)) {
            MobCoins.replace(uuid, (MobCoins.get(uuid) + amount));
        }
        else {
            MobCoins.put(uuid, amount);
        }
        SaveMobCoinsHashMap();
    }

    public void TakeMobCoin(Player player, int amount) {
        LoadMobCoinsHashMap();
        String uuid = player.getUniqueId().toString();
        MobCoins.put(uuid, (getMobCoinAmount(player) - amount));
        SaveMobCoinsHashMap();
        SaveMobCoinsHashMap();
    }
}