package BlackMarket;

import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlackMarketManager {
    MainClass mainClass;

    public BlackMarketManager(MainClass mc) {
        mainClass = mc;
    }

    UtilityMain utilityMain = new UtilityMain();

    ItemStack CommonItem = null;
    ItemStack UncommonItem = null;
    ItemStack RareItem = null;

    Integer CommonPrice = 0;
    Integer UncommonPrice = 0;
    Integer RarePrice = 0;

    String CommonPlayerUUID = null;
    String UncommonPlayerUUID = null;
    String RarePlayerUUID = null;

    Integer CommonTime = 1;
    Integer UncommonTime = 6;
    Integer RareTime = 24;

    ItemStack[] CommonItems = {
            utilityMain.createGuiItem(Material.EMERALD, ChatColor.LIGHT_PURPLE + "Cool Emerald"),
            new ItemStack(Material.PAPER)
    };
    ItemStack[] UncommonItems = {
            new ItemStack(Material.DIAMOND),
            new ItemStack(Material.BLAZE_POWDER)
    };
    ItemStack[] RareItems = {
            new ItemStack(Material.CHAINMAIL_HELMET),
            new ItemStack(Material.DIAMOND_LEGGINGS)
    };

    public void SaveBlackMarket() {
        mainClass.saveConfig();
    }

    public void NewBlackMarketItems() {
        NewSpecificBlackMarketItem("Common");
        NewSpecificBlackMarketItem("Uncommon");
        NewSpecificBlackMarketItem("Rare");
    }

    private void NewSpecificBlackMarketItem(String rarity) {
        if (rarity == "Common") {
            int r = utilityMain.getRandom().nextInt(CommonItems.length);
            CommonItem = CommonItems[r];
            mainClass.getConfig().set("CommonItem", CommonItem);
        }
        if (rarity == "Uncommon") {
            int r = utilityMain.getRandom().nextInt(UncommonItems.length);
            UncommonItem = UncommonItems[r];
            mainClass.getConfig().set("UncommonItem", UncommonItem);
        }
        if (rarity == "Rare") {
            int r = utilityMain.getRandom().nextInt(RareItems.length);
            RareItem = RareItems[r];
            mainClass.getConfig().set("RareItem", RareItem);
        }
        SaveBlackMarket();
    }

    private ItemStack getCommonItemFromConfig() {
        return mainClass.getConfig().getItemStack("CommonItem");
    }
    private ItemStack getUncommonItemFromConfig() {
        return mainClass.getConfig().getItemStack("UncommonItem");
    }
    private ItemStack getRareItemFromConfig() {
        return mainClass.getConfig().getItemStack("RareItem");
    }

    public void LoadItems() {
        if (getCommonItemFromConfig() == null) {
            NewSpecificBlackMarketItem("Common");
        }
        else {
            CommonItem = getCommonItemFromConfig();
        }
        if (getUncommonItemFromConfig() == null) {
            NewSpecificBlackMarketItem("Uncommon");
        }
        else {
            UncommonItem = getUncommonItemFromConfig();
        }
        if (getRareItemFromConfig() == null) {
            NewSpecificBlackMarketItem("Rare");
        }
        else {
            RareItem = getRareItemFromConfig();
        }
    }

    public void LoadPrices() {
        CommonPrice = mainClass.getConfig().getInt("CommonPrice");
        UncommonPrice = mainClass.getConfig().getInt("UncommonPrice");
        RarePrice = mainClass.getConfig().getInt("RarePrice");
    }

    public String getCommonPlayerUUID() {
        if (mainClass.getConfig().getString("CommonPlayerUUID") == null) {
            CommonPlayerUUID = null;
        }
        else {
            CommonPlayerUUID = mainClass.getConfig().getString("CommonPlayerUUID");
        }
        return CommonPlayerUUID;
    }
    public String getUncommonPlayerUUID() {
        if (mainClass.getConfig().getString("UncommonPlayerUUID") == null) {
            UncommonPlayerUUID = null;
        }
        else {
            UncommonPlayerUUID = mainClass.getConfig().getString("UncommonPlayerUUID");
        }
        return UncommonPlayerUUID;
    }
    public String getRarePlayerUUID() {
        if (mainClass.getConfig().getString("RarePlayerUUID") == null) {
            RarePlayerUUID = null;
        }
        else {
            RarePlayerUUID = mainClass.getConfig().getString("RarePlayerUUID");
        }
        return RarePlayerUUID;
    }

    public void BidForCommonItem(Player player) {
        Economy economy  = MainClass.getEconomy();
        CommonPrice = mainClass.getConfig().getInt("CommonPrice");
        if (economy.getBalance(player) < (CommonPrice + 10000)) {
            NotEnoughMoneyMessage(player);
            return;
        }
        CommonPrice = CommonPrice + 10000;
        CommonPlayerUUID = player.getUniqueId().toString();
        mainClass.getConfig().set("CommonPlayerUUID", CommonPlayerUUID);
        mainClass.getConfig().set("CommonPrice", CommonPrice);
        SaveBlackMarket();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
    }
    public void BidForUncommonItem(Player player) {
        Economy economy  = MainClass.getEconomy();
        UncommonPrice = mainClass.getConfig().getInt("UncommonPrice");
        if (economy.getBalance(player) < (UncommonPrice + 100000)) {
            NotEnoughMoneyMessage(player);
            return;
        }
        UncommonPrice = UncommonPrice + 100000;
        UncommonPlayerUUID = player.getUniqueId().toString();
        mainClass.getConfig().set("UncommonPlayerUUID", UncommonPlayerUUID);
        mainClass.getConfig().set("UncommonPrice", UncommonPrice);
        SaveBlackMarket();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
    }
    public void BidForRareItem(Player player) {
        Economy economy  = MainClass.getEconomy();
        RarePrice = mainClass.getConfig().getInt("RarePrice");
        if (economy.getBalance(player) < (RarePrice + 1000000)) {
            NotEnoughMoneyMessage(player);
            return;
        }
        RarePrice = RarePrice + 1000000;
        RarePlayerUUID = player.getUniqueId().toString();
        mainClass.getConfig().set("RarePlayerUUID", RarePlayerUUID);
        mainClass.getConfig().set("RarePrice", RarePrice);
        SaveBlackMarket();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
    }

    private void NotEnoughMoneyMessage(Player player) {
        player.sendMessage(ChatColor.RED + "You do not have enough money to bid");
    }

    public void LoadTimes() {
        CommonTime = mainClass.getConfig().getInt("CommonTime");
        UncommonTime = mainClass.getConfig().getInt("UncommonTime");
        RareTime = mainClass.getConfig().getInt("RareTime");
    }

    public void BlackMarketTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                LoadTimes();
                CommonTime = (CommonTime - 1);
                UncommonTime = (UncommonTime - 1);
                RareTime = (RareTime - 1);
                if (CommonTime <= 0) {
                    GivePlayerCommon();
                }
                mainClass.getConfig().set("CommonTime", CommonTime);
                if (UncommonTime <= 0) {
                    GivePlayerUncommon();
                }
                mainClass.getConfig().set("UncommonTime", UncommonTime);
                if (RareTime <= 0) {
                    GivePlayerRare();
                }
                mainClass.getConfig().set("RareTime", RareTime);
                SaveBlackMarket();
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 1200);
    }

    private void GivePlayerCommon() {
        CommonTime = 60;
        mainClass.getConfig().set("CommonTime", CommonTime);
        if (getCommonPlayerUUID() == null) {
            return;
        }
        if (Bukkit.getOfflinePlayer(UUID.fromString(getCommonPlayerUUID())) == null) {
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(getCommonPlayerUUID()));
        Economy economy  = MainClass.getEconomy();
        LoadPrices();
        if (economy.getBalance(offlinePlayer) > CommonPrice) {
            economy.withdrawPlayer(offlinePlayer, CommonPrice);
            if (offlinePlayer.isOnline()) {
                Player player = (Player) offlinePlayer;
                LoadItems();
                player.getInventory().addItem(CommonItem);
            }
            else {
                AddPlayerToPlayersToAddCommons(offlinePlayer);
            }
            Bukkit.getServer().broadcastMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "--------------------" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.DARK_RED + "     " + offlinePlayer.getName() + ChatColor.RED + " won " + ChatColor.GREEN + ChatColor.stripColor(CommonItem.getItemMeta().getDisplayName()) + "\n" + ChatColor.RED + "    from the Black Market" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "--------------------");
        }
        else if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            player.sendMessage(ChatColor.RED + "You did not have enough money to receive your Black Market item");
        }
        NewSpecificBlackMarketItem("Common");
        mainClass.getConfig().set("CommonPlayerUUID", null);
        mainClass.getConfig().set("CommonPrice", 0);
        SaveBlackMarket();
    }
    private void GivePlayerUncommon() {
        UncommonTime = 360;
        mainClass.getConfig().set("UncommonTime", UncommonTime);
        if (getUncommonPlayerUUID() == null) {
            return;
        }
        if (Bukkit.getOfflinePlayer(UUID.fromString(getUncommonPlayerUUID())) == null) {
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(getUncommonPlayerUUID()));
        Economy economy  = MainClass.getEconomy();
        LoadPrices();
        if (economy.getBalance(offlinePlayer) > UncommonPrice) {
            economy.withdrawPlayer(offlinePlayer, UncommonPrice);
            if (offlinePlayer.isOnline()) {
                Player player = (Player) offlinePlayer;
                LoadItems();
                player.getInventory().addItem(UncommonItem);
            }
            else {
                AddPlayerToPlayersToAddUncommons(offlinePlayer);
            }
            Bukkit.getServer().broadcastMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "--------------------" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.DARK_RED + "     " + offlinePlayer.getName() + ChatColor.RED + " won " + ChatColor.YELLOW + ChatColor.stripColor(UncommonItem.getItemMeta().getDisplayName()) + "\n" + ChatColor.RED + "    from the Black Market" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "--------------------");
        }
        else if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            player.sendMessage(ChatColor.RED + "You did not have enough money to receive your Black Market item");
        }
        NewSpecificBlackMarketItem("Uncommon");
        mainClass.getConfig().set("UncommonPlayerUUID", null);
        mainClass.getConfig().set("UncommonPrice", 0);
        SaveBlackMarket();
    }
    private void GivePlayerRare() {
        RareTime = 1440;
        mainClass.getConfig().set("RareTime", RareTime);
        if (getRarePlayerUUID() == null) {
            return;
        }
        if (Bukkit.getOfflinePlayer(UUID.fromString(getRarePlayerUUID())) == null) {
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(getRarePlayerUUID()));
        Economy economy  = MainClass.getEconomy();
        LoadPrices();
        if (economy.getBalance(offlinePlayer) > RarePrice) {
            economy.withdrawPlayer(offlinePlayer, RarePrice);
            if (offlinePlayer.isOnline()) {
                Player player = (Player) offlinePlayer;
                LoadItems();
                player.getInventory().addItem(RareItem);
            }
            else {
                AddPlayerToPlayersToAddRares(offlinePlayer);
            }
            Bukkit.getServer().broadcastMessage(ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "--------------------" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.DARK_RED + "     " + offlinePlayer.getName() + ChatColor.RED + " won " + ChatColor.DARK_RED + ChatColor.stripColor(RareItem.getItemMeta().getDisplayName()) + "\n" + ChatColor.RED + "    from the Black Market" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.BLACK + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "--------------------");
        }
        else if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            player.sendMessage(ChatColor.RED + "You did not have enough money to receive your Black Market item");
        }
        NewSpecificBlackMarketItem("Rare");
        mainClass.getConfig().set("RarePlayerUUID", null);
        mainClass.getConfig().set("RarePrice", 0);
        SaveBlackMarket();
    }

    List<String> PlayersToAddCommons = new ArrayList<>();
    List<ItemStack> ItemsToAddCommons = new ArrayList<>();
    List<String> PlayersToAddUncommons = new ArrayList<>();
    List<ItemStack> ItemsToAddUncommons = new ArrayList<>();
    List<String> PlayersToAddRares = new ArrayList<>();
    List<ItemStack> ItemsToAddRares = new ArrayList<>();

    public void LoadPlayersToAddItems() {
        PlayersToAddCommons = mainClass.getConfig().getStringList("PlayersToAddCommon");
        PlayersToAddUncommons = mainClass.getConfig().getStringList("PlayersToAddUncommon");
        PlayersToAddRares = mainClass.getConfig().getStringList("PlayersToAddRare");
    }

    public void LoadItemsToAddItems() {
        ItemsToAddCommons = (List<ItemStack>) mainClass.getConfig().getList("ItemsToAddCommon");
        if (ItemsToAddCommons == null) {
            ItemsToAddCommons = new ArrayList<>();
        }
        ItemsToAddUncommons = (List<ItemStack>) mainClass.getConfig().getList("ItemsToAddUncommon");
        if (ItemsToAddUncommons == null) {
            ItemsToAddUncommons = new ArrayList<>();
        }
        ItemsToAddRares = (List<ItemStack>) mainClass.getConfig().getList("ItemsToAddRare");
        if (ItemsToAddRares == null) {
            ItemsToAddRares = new ArrayList<>();
        }
    }

    private void AddPlayerToPlayersToAddCommons(OfflinePlayer player) {
        LoadPlayersToAddItems();
        LoadItemsToAddItems();
        LoadItems();
        PlayersToAddCommons.add(player.getUniqueId().toString());
        mainClass.getConfig().set("PlayersToAddCommon", PlayersToAddCommons);
        ItemsToAddCommons.add(CommonItem);
        mainClass.getConfig().set("ItemsToAddCommon", ItemsToAddCommons);
        SaveBlackMarket();
    }
    private void AddPlayerToPlayersToAddUncommons(OfflinePlayer player) {
        LoadPlayersToAddItems();
        LoadItemsToAddItems();
        LoadItems();
        PlayersToAddUncommons.add(player.getUniqueId().toString());
        mainClass.getConfig().set("PlayersToAddUncommon", PlayersToAddUncommons);
        ItemsToAddUncommons.add(UncommonItem);
        mainClass.getConfig().set("ItemsToAddUncommon", ItemsToAddUncommons);
        SaveBlackMarket();
    }
    private void AddPlayerToPlayersToAddRares(OfflinePlayer player) {
        LoadPlayersToAddItems();
        LoadItemsToAddItems();
        LoadItems();
        PlayersToAddRares.add(player.getUniqueId().toString());
        mainClass.getConfig().set("PlayersToAddRare", PlayersToAddRares);
        ItemsToAddRares.add(RareItem);
        mainClass.getConfig().set("ItemsToAddRare", ItemsToAddRares);
        SaveBlackMarket();
    }
}