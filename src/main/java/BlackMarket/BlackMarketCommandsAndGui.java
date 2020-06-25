package BlackMarket;

import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BlackMarketCommandsAndGui extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    BlackMarketManager blackMarketManager;
    UtilityMain utilityMain = new UtilityMain();
    public BlackMarketCommandsAndGui(MainClass mc) {
        mainClass = mc;
        blackMarketManager = new BlackMarketManager(mainClass);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))  {
            return false;
        }
        if (args.length == 0) {
            LoadBlackMarketGui((Player) sender);
            return true;
        }
        if (sender.isOp() && args.length == 1) {
            if (args[0].equalsIgnoreCase("newitems")) {
                blackMarketManager.NewBlackMarketItems();
            }
            else {
                sender.sendMessage(ChatColor.GRAY + "Usage: blackmarket newitems");
            }
        }
        return true;
    }

    public void LoadBlackMarketGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 45, ChatColor.WHITE + "" + "☠ " + ChatColor.RED  + "" + ChatColor.BOLD +  "Black Market " + ChatColor.WHITE + "☠");
        FillBlackMarketGui(inv);
        player.openInventory(inv);
    }

    private void FillBlackMarketGui(Inventory inv)  {
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack Red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Black);
        }
        inv.setItem(2, Red);
        inv.setItem(4, Red);
        inv.setItem(6, Red);
        inv.setItem(10, Red);
        inv.setItem(12, Red);
        inv.setItem(14, Red);
        inv.setItem(16, Red);
        inv.setItem(18, Red);
        inv.setItem(20, Red);
        inv.setItem(22, Red);
        inv.setItem(24, Red);
        inv.setItem(26, Red);
        inv.setItem(28, Red);
        inv.setItem(30, Red);
        inv.setItem(32, Red);
        inv.setItem(34, Red);
        inv.setItem(38, Red);
        inv.setItem(42, Red);
        LoadBlackMarketInstanceVariables();
        inv.setItem(10, CreateCommonItem());
        inv.setItem(13, CreateUncommonItem());
        inv.setItem(16, CreateRareItem());
        inv.setItem(29, utilityMain.createGuiItem(Material.COMPASS, ChatColor.YELLOW + "Refresh"));
        inv.setItem(33, utilityMain.createGuiItem(Material.BARRIER, ChatColor.RED + "Exit"));
    }

    private void LoadBlackMarketInstanceVariables() {
        blackMarketManager.LoadItems();
        blackMarketManager.LoadPrices();
        blackMarketManager.LoadTimes();
    }

    private ItemStack CreateCommonItem() {
        if (blackMarketManager.CommonItem == null) {
            return null;
        }
        ItemStack item = new ItemStack(blackMarketManager.CommonItem);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------");
        lore.add(ChatColor.GREEN + "" + "             " + ChatColor.UNDERLINE + "Tier I" + ChatColor.GRAY + ChatColor.ITALIC + " (1h)");
        lore.add(ChatColor.COLOR_CHAR + " ");
        lore.add(ChatColor.GRAY + "Time: " + ChatColor.GREEN + utilityMain.FormatTime(blackMarketManager.CommonTime));
        lore.add(ChatColor.GRAY + "Bid: $" + ChatColor.GREEN + utilityMain.FormatPrice(blackMarketManager.CommonPrice));
        lore.add(ChatColor.GRAY + "Player: " + ChatColor.GREEN + utilityMain.getPlayerNameFromUUID(blackMarketManager.getCommonPlayerUUID()));
        lore.add(ChatColor.COLOR_CHAR + " ");
        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------");
        lore.add(ChatColor.GRAY + "Click to bid +$10,000");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack CreateUncommonItem() {
        if (blackMarketManager.UncommonItem == null) {
            return null;
        }
        ItemStack item = new ItemStack(blackMarketManager.UncommonItem);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD+ "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------");
        lore.add(ChatColor.YELLOW + "" + "             " + ChatColor.UNDERLINE + "Tier II" + ChatColor.GRAY + ChatColor.ITALIC + "  (6h)");
        lore.add(ChatColor.COLOR_CHAR + " ");
        lore.add(ChatColor.GRAY + "Time: " + ChatColor.YELLOW + utilityMain.FormatTime(blackMarketManager.UncommonTime));
        lore.add(ChatColor.GRAY + "Bid: $" + ChatColor.YELLOW + utilityMain.FormatPrice(blackMarketManager.UncommonPrice));
        lore.add(ChatColor.GRAY + "Player: " + ChatColor.YELLOW + utilityMain.getPlayerNameFromUUID(blackMarketManager.getUncommonPlayerUUID()));
        lore.add(ChatColor.COLOR_CHAR + " ");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------");
        lore.add(ChatColor.GRAY + "Click to bid +$100,000");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack CreateRareItem() {
        if (blackMarketManager.RareItem == null) {
            return null;
        }
        ItemStack item = new ItemStack(blackMarketManager.RareItem);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------");
        lore.add(ChatColor.RED + "" + "             " + ChatColor.UNDERLINE + "Tier III" + ChatColor.GRAY + ChatColor.ITALIC + " (1d)");
        lore.add(ChatColor.COLOR_CHAR + " ");
        lore.add(ChatColor.GRAY + "Time: " + ChatColor.RED + utilityMain.FormatTime(blackMarketManager.RareTime));
        lore.add(ChatColor.GRAY + "Bid: $" + ChatColor.RED + utilityMain.FormatPrice(blackMarketManager.RarePrice));
        lore.add(ChatColor.GRAY + "Player: " + ChatColor.RED + utilityMain.getPlayerNameFromUUID(blackMarketManager.getRarePlayerUUID()));
        lore.add(ChatColor.COLOR_CHAR + " ");
        lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------");
        lore.add(ChatColor.GRAY + "Click to bid +$1,000,000");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void LoadConfirmGui(Player player, String name) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 27, name);
        FillConfirmGui(inv);
        player.openInventory(inv);
    }

    private void FillConfirmGui(Inventory inv) {
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i < inv.getSize(); i++)  {
            inv.setItem(i, Black);
        }
        if (inv.getName().contains(ChatColor.GREEN + "")) {
            inv.setItem(13, CustomItem(Material.GOLD_INGOT, (short) 0, (ChatColor.YELLOW + "Bid $" + utilityMain.FormatPrice(blackMarketManager.CommonPrice + 10000)) + "?"));
        }
        else if (inv.getName().contains(ChatColor.YELLOW + "")) {
            inv.setItem(13, CustomItem(Material.GOLD_INGOT, (short) 0, (ChatColor.YELLOW + "Bid $" + utilityMain.FormatPrice(blackMarketManager.UncommonPrice + 100000)) + "?"));
        }
        else if (inv.getName().contains(ChatColor.RED + "")) {
            inv.setItem(13, CustomItem(Material.GOLD_INGOT, (short) 0, (ChatColor.YELLOW + "Bid $" + utilityMain.FormatPrice(blackMarketManager.RarePrice + 1000000)) + "?"));
        }
        inv.setItem(11, CustomItem(Material.STAINED_CLAY, (short) 13, (ChatColor.GREEN + "Confirm")));
        inv.setItem(15, CustomItem(Material.STAINED_CLAY, (short) 14, (ChatColor.RED + "Cancel")));
    }

    private ItemStack CustomItem(Material mat, short number, String name) {
        ItemStack clay = new ItemStack(mat, 1, number);
        ItemMeta meta = clay.getItemMeta();
        meta.setDisplayName(name);
        clay.setItemMeta(meta);
        return clay;
    }
}
