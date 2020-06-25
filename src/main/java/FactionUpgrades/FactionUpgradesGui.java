package FactionUpgrades;

import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;


public class FactionUpgradesGui implements Listener {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    FactionUpgradesDropRateUpgrade factionUpgradesDropRateUpgrade;
    public FactionUpgradesGui(MainClass mc) {
        mainClass = mc;
        factionUpgradesDropRateUpgrade = new FactionUpgradesDropRateUpgrade(mainClass);
    }

    public void StartingPoint(Player player) {
        if (!(FPlayers.getInstance().getByPlayer(player).hasFaction())) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to execute this command");
            return;
        }
        LoadGui(player);
    }

    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        if (event.getSlot() == -999) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack CurrentItem = event.getCurrentItem();
        Material type = CurrentItem.getType();
        if (event.getClickedInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Faction Upgrades")) {
            event.setCancelled(true);
            if (type.equals(Material.STAINED_GLASS_PANE)) {
                return;
            }
            if (type.equals(Material.BARRIER)) {
                player.closeInventory();
                return;
            }
            if (type.equals(Material.DOUBLE_PLANT) || type.equals(Material.EMERALD)) {
                return;
            }
            ConfirmGui(player, CurrentItem);
            return;
        }
        if (event.getClickedInventory().getName().equals(ChatColor.YELLOW + "Confirm")) {
            event.setCancelled(true);
            if (type.equals(Material.STAINED_GLASS_PANE)) {
                return;
            }
            if (CurrentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Confirm")) {
                if (CanPurchaseUpgrade(event.getInventory().getItem(13), player)) {
                    PurchaseUpgrade(player, event.getInventory().getItem(13));
                    LoadGui(player);
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                    player.sendMessage(ChatColor.RED + "You cannot afford this purchase");
                }
            }
            if (CurrentItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Cancel")) {
               LoadGui(player);
            }
        }
    }

    private void PurchaseUpgrade(Player player, ItemStack itemStack) {
        Integer price = PriceOfUpgrade(itemStack);
        Economy economy  = MainClass.getEconomy();
        economy.withdrawPlayer(player, price);
        String FactionsId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        LoadUpgradesHashmap();
        if (itemStack.getType().equals(Material.EXP_BOTTLE)) {
            if (XpUpgrade.containsKey(FactionsId)) {
                XpUpgrade.replace(FactionsId, XpUpgrade.get(FactionsId) + 1);
            }
            else {
                XpUpgrade.put(FactionsId, 1);
            }
        }
        else if (itemStack.getType().equals(Material.MOB_SPAWNER)) {
            if (DropRateUpgrade.containsKey(FactionsId)) {
                DropRateUpgrade.replace(FactionsId, DropRateUpgrade.get(FactionsId) + 1);
            }
            else {
                DropRateUpgrade.put(FactionsId, 1);
            }
            factionUpgradesDropRateUpgrade.PurchasedDropRateUpgrade(FactionsId);
        }
        else if (itemStack.getType().equals(Material.SUGAR_CANE)) {
            if (CropGrowthUpgrade.containsKey(FactionsId)) {
                CropGrowthUpgrade.replace(FactionsId, CropGrowthUpgrade.get(FactionsId) + 1);
            }
            else {
                CropGrowthUpgrade.put(FactionsId, 1);
            }
        }
        else if (itemStack.getType().equals(Material.TNT)) {
            if (TntStorageUpgrade.containsKey(FactionsId)) {
                TntStorageUpgrade.replace(FactionsId, TntStorageUpgrade.get(FactionsId) + 1);
            }
            else {
                TntStorageUpgrade.put(FactionsId, 1);
            }
        }
        else if (itemStack.getType().equals(Material.IRON_DOOR)) {
            if (FactionShieldUpgrade.containsKey(FactionsId)) {
                FactionShieldUpgrade.replace(FactionsId, FactionShieldUpgrade.get(FactionsId) + 1);
            }
            else {
                FactionShieldUpgrade.put(FactionsId, 1);
            }
        }
        else if (itemStack.getType().equals(Material.DIAMOND_CHESTPLATE)) {
            if (DamageReductionUpgrade.containsKey(FactionsId)) {
                DamageReductionUpgrade.replace(FactionsId, DamageReductionUpgrade.get(FactionsId) + 1);
            }
            else {
                DamageReductionUpgrade.put(FactionsId, 1);
            }
        }
        else if (itemStack.getType().equals(Material.BEACON)) {
            if (FactionWarpsUpgrade.containsKey(FactionsId)) {
                FactionWarpsUpgrade.replace(FactionsId, FactionWarpsUpgrade.get(FactionsId) + 1);
            }
            else {
                FactionWarpsUpgrade.put(FactionsId, 1);
            }
        }
        else if (itemStack.getType().equals(Material.BANNER)) {
            if (FactionRallyUpgrade.containsKey(FactionsId)) {
                FactionRallyUpgrade.replace(FactionsId, FactionRallyUpgrade.get(FactionsId) + 1);
            }
            else {
                FactionRallyUpgrade.put(FactionsId, 1);
            }
        }
        SaveUpgradesInConfig();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        Set<FPlayer> FactionPlayers = FPlayers.getInstance().getByPlayer(player).getFaction().getFPlayers();
        String[] SplitName = itemStack.getItemMeta().getDisplayName().split(" ");
        String RebuildString = "";
        for (int i = 0; i < (SplitName.length - 1); i++) {
            RebuildString = (RebuildString + " " + SplitName[i]);
        }
        String message = ChatColor.DARK_GREEN + player.getName() + ChatColor.GREEN + " purchased" + ChatColor.GREEN + ChatColor.stripColor(RebuildString) + " " + ChatColor.DARK_GREEN + ChatColor.stripColor(SplitName[SplitName.length - 1]);
        for (FPlayer factionPlayer : FactionPlayers) {
            if (factionPlayer.isOnline()) {
                factionPlayer.sendMessage(message);
            }
        }
    }

    private void SaveUpgradesInConfig() {
        List<String> XpUpgradeStringList = new ArrayList<>();
        for (Map.Entry element : XpUpgrade.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer level = (Integer) element.getValue();
            XpUpgradeStringList.add(FactionId + ":" + level);
        }
        List<String> DropRateUpgradeStringList = new ArrayList<>();
        for (Map.Entry element : DropRateUpgrade.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer level = (Integer) element.getValue();
            DropRateUpgradeStringList.add(FactionId + ":" + level);
        }
        List<String> CropGrowthUpgradeStringList = new ArrayList<>();
        for (Map.Entry element : CropGrowthUpgrade.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer level = (Integer) element.getValue();
            CropGrowthUpgradeStringList.add(FactionId + ":" + level);
        }
        List<String> TntStorageUpgradeStringList = new ArrayList<>();
        for (Map.Entry element : TntStorageUpgrade.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer level = (Integer) element.getValue();
            TntStorageUpgradeStringList.add(FactionId + ":" + level);
        }
        List<String> FactionShieldUpgradeStringList = new ArrayList<>();
        for (Map.Entry element : FactionShieldUpgrade.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer level = (Integer) element.getValue();
            FactionShieldUpgradeStringList.add(FactionId + ":" + level);
        }
        List<String> DamageReductionUpgradeStringList = new ArrayList<>();
        for (Map.Entry element : DamageReductionUpgrade.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer level = (Integer) element.getValue();
            DamageReductionUpgradeStringList.add(FactionId + ":" + level);
        }
        List<String> FactionWarpsUpgradeStringList = new ArrayList<>();
        for (Map.Entry element : FactionWarpsUpgrade.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer level = (Integer) element.getValue();
            FactionWarpsUpgradeStringList.add(FactionId + ":" + level);
        }
        List<String> FactionRallyUpgradeStringList = new ArrayList<>();
        for (Map.Entry element : FactionRallyUpgrade.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer level = (Integer) element.getValue();
            FactionRallyUpgradeStringList.add(FactionId + ":" + level);
        }
        mainClass.getConfig().set("XpUpgrade", XpUpgradeStringList);
        mainClass.getConfig().set("DropRateUpgrade", DropRateUpgradeStringList);
        mainClass.getConfig().set("CropGrowthUpgrade", CropGrowthUpgradeStringList);
        mainClass.getConfig().set("TntStorageUpgrade", TntStorageUpgradeStringList);
        mainClass.getConfig().set("FactionShieldUpgrade", FactionShieldUpgradeStringList);
        mainClass.getConfig().set("DamageReductionUpgrade", DamageReductionUpgradeStringList);
        mainClass.getConfig().set("FactionWarpsUpgrade", FactionWarpsUpgradeStringList);
        mainClass.getConfig().set("FactionRallyUpgrade", FactionRallyUpgradeStringList);
        mainClass.saveConfig();
    }

    private boolean CanPurchaseUpgrade(ItemStack itemStack, Player player) {
        return getBalance(player) >= PriceOfUpgrade(itemStack);
    }

    private Integer PriceOfUpgrade(ItemStack itemStack) {
        String PriceString = itemStack.getItemMeta().getLore().get(0);
        String StrippedColor = ChatColor.stripColor(PriceString);
        return Integer.parseInt(StrippedColor.replace(",", "").replace("$", ""));
    }

    public void ConfirmGui(Player player, ItemStack itemStack) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 27, ChatColor.YELLOW + "Confirm");
        FillConfirmGui(inv, itemStack);
        player.openInventory(inv);
    }

    private void FillConfirmGui(Inventory inv, ItemStack itemStack) {
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i < inv.getSize(); i++)  {
            inv.setItem(i, Black);
        }
        ItemStack GreenClay = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
        ItemStack RedClay = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
        inv.setItem(11, utilityMain.createGuiItemFromItemStack(GreenClay, ChatColor.GREEN + "Confirm"));
        inv.setItem(15, utilityMain.createGuiItemFromItemStack(RedClay, ChatColor.RED + "Cancel"));
        String[] SplitName = itemStack.getItemMeta().getDisplayName().split("Upgrades");
        String[] SplitLevel = itemStack.getItemMeta().getDisplayName().split("/");
        String NoColor = ChatColor.stripColor(SplitLevel[0]);
        int level = Integer.parseInt(NoColor.replaceAll("[^0-9]", ""));
        for (String lore : itemStack.getItemMeta().getLore()) {
            if (lore.contains("Upgrade Tokens")) {
                String[] Split = lore.split(" ");
                Integer price = Integer.parseInt(ChatColor.stripColor(Split[0].replace("$", "").replace(",", "")));
                inv.setItem(13, utilityMain.createGuiItem(itemStack.getType(), ChatColor.RED + SplitName[0] + "Upgrade" + ChatColor.DARK_RED + " (" + level + ChatColor.RED + "➵" + ChatColor.DARK_RED + (level + 1) + ")", ChatColor.GREEN + "$" + utilityMain.FormatPrice(price)));
            }
        }
    }

    private void LoadGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 54, ChatColor.RED + "" + ChatColor.BOLD + "Faction Upgrades");
        FillGui(inv);
        SetUpgradeItemsInGui(inv, player);
        player.openInventory(inv);
    }

    private void FillGui(Inventory inv) {
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack Red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Black);
        }
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, Red);
        }
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, Red);
        }
        for (int i = 9; i < 37; i = (i + 9)) {
            inv.setItem(i, Red);
        }
        for (int i = 17; i < 45; i = (i + 9)) {
            inv.setItem(i, Red);
        }
    }

    private void SetUpgradeItemsInGui(Inventory inv, Player player) {
        Integer[] XpPercentages = new Integer[] {20, 40, 60, 80, 100};
        Integer[] XpPrices = new Integer[] {1000000, 3000000, 10000000, 25000000, 50000000};
        Integer[] SpawnerDropPercentages = new Integer[] {10, 20, 30, 40, 50};
        Integer[] SpawnerPrices = new Integer[] {15000000, 40000000, 100000000, 175000000, 300000000};
        Integer[] CropGrowthPercentages = new Integer[] {25, 60, 100, 150, 200};
        Integer[] CropGrowthPrices = new Integer[] {1000000, 3000000, 10000000, 25000000, 50000000};
        Integer[] TntStoragePrices = new Integer[] {3000000, 10000000, 20000000, 35000000, 60000000};
        Integer[] TntStoragePercentages = new Integer[] {1, 3, 7, 15, 25};
        Integer[] FactionShieldPrices = new Integer[] {5000000, 20000000, 50000000, 1000000, 175000000};
        Integer[] FactionShieldPercentages = new Integer[] {2, 4, 6, 9, 12};
        Integer[] DamageReductionPrices = new Integer[] {5000000, 10000000, 25000000, 50000000, 100000000};
        Integer[] DamageReductionPercentages = new Integer[] {3, 6, 9, 12, 15};
        Integer[] FactionWarpsPrices = new Integer[] {1000000, 3000000, 6000000, 10000000, 15000000};
        Integer[] FactionWarpsPercentages = new Integer[] {4, 5, 6, 7, 8};
        Integer[] FactionRallyPrices = new Integer[] {1000000, 3000000, 10000000, 20000000, 35000000};
        Integer[] FactionRallyPercentages = new Integer[] {4, 5, 6, 7, 8};
        inv.setItem(10, MakeUpgradeItem(Material.EXP_BOTTLE, "Xp Upgrades", "Upgrade your experience intake", "for your faction", "Experience Boost", XpPercentages, XpPrices, player));
        inv.setItem(12, MakeUpgradeItem(Material.MOB_SPAWNER, "Drop Rates Upgrades", "Upgrade the drop rate of spawners","in your territory", "Drop Rates Boost", SpawnerDropPercentages, SpawnerPrices, player));
        inv.setItem(14, MakeUpgradeItem(Material.SUGAR_CANE, "Crop Growth Upgrades", "Upgrade the crop growth of sugar cane","in your territory", "Crop Growth Boost", CropGrowthPercentages, CropGrowthPrices, player));
        inv.setItem(16, MakeUpgradeItem(Material.TNT, "Tnt Storage Upgrades", "Upgrade your faction's tnt storage",null, "TNT Storage Boost", TntStoragePercentages, TntStoragePrices, player));
        inv.setItem(28, MakeUpgradeItem(Material.IRON_DOOR, "Faction Shield Upgrades", "Upgrade your faction's shield time",null, "Faction Shield Time Boost", FactionShieldPercentages, FactionShieldPrices, player));
        inv.setItem(30, MakeUpgradeItem(Material.DIAMOND_CHESTPLATE, "Damage Reduction Upgrades", "Upgrade the damage reduction faction","members receive while in territory", "Damage Reduction Boost", DamageReductionPercentages, DamageReductionPrices, player));
        inv.setItem(32, MakeUpgradeItem(Material.BEACON, "Faction Warps Upgrades", "Upgrade the amount of faction warps","your faction can have", "Faction Warps Amount Boost", FactionWarpsPercentages, FactionWarpsPrices, player));
        inv.setItem(34, MakeUpgradeItem(Material.BANNER, "Faction Rally Upgrades", "Upgrade the amount of minutes your","faction rally lasts", "Faction Rally Time Boost", FactionRallyPercentages, FactionRallyPrices, player));
        inv.setItem(45, utilityMain.createGuiItem(Material.DOUBLE_PLANT, ChatColor.GREEN + "" + ChatColor.BOLD + "$" + utilityMain.FormatPrice((int) getBalance(player))));
        inv.setItem(53, utilityMain.createGuiItem(Material.EMERALD, ChatColor.GREEN + "" + ChatColor.BOLD + "Upgrade Tokens: " + getFactionTokens(player)));
        inv.setItem(49, utilityMain.createGuiItem(Material.BARRIER, ChatColor.RED + "Exit"));
    }

    private double getBalance(Player player) {
        Economy economy  = MainClass.getEconomy();
        double balance = economy.getBalance(player);
        return balance;
    }

    private Integer getFactionTokens(Player player) {
        LoadFactionTokensHashmap();
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        Integer FactionTokenAmount = FactionTokens.get(FactionId);
        if (FactionTokenAmount == null)  {
            FactionTokenAmount = 0;
        }
        return FactionTokenAmount;
    }

    private ItemStack MakeUpgradeItem(Material material, String name, String description1, String description2, String LongName, Integer[] LevelPercentages, Integer[] prices, Player player) {
        ItemStack item = new ItemStack(material);
        if (name.equals("Faction Rally Upgrade")) {
            BannerMeta bannerMeta = (BannerMeta) item.getItemMeta();
            bannerMeta.setBaseColor(DyeColor.RED);
            item.setItemMeta(bannerMeta);
        }
        ItemMeta meta = item.getItemMeta();
        Integer CurrentLevel = getCurrentLevel(name, player);
        meta.setDisplayName(ChatColor.RED + name + ChatColor.GRAY + " (" + ChatColor.RED + CurrentLevel + ChatColor.GRAY + "/" + ChatColor.RED + LevelPercentages.length + ChatColor.GRAY + ")");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + description1);
        if (description2 != null) {
            lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + description2);
        }
        lore.add(ChatColor.COLOR_CHAR + " ");
        for (int i = 0; i < LevelPercentages.length; i++) {
            if (name.equals("Tnt Storage Upgrades")) {
                lore.add(ChatColor.RED + "" + ChatColor.BOLD + "✘" + ChatColor.GRAY + " +" + ChatColor.RED + LevelPercentages[i] + "m" + ChatColor.GRAY + " " + LongName);
            }
            else if (name.equals("Faction Shield Upgrades")) {
                lore.add(ChatColor.RED + "" + ChatColor.BOLD + "✘" + ChatColor.GRAY + " +" + ChatColor.RED + LevelPercentages[i] + "h" + ChatColor.GRAY + " " + LongName);
            }
            else if (name.equals("Faction Warps Upgrades")) {
                lore.add(ChatColor.RED + "" + ChatColor.BOLD + "✘" + ChatColor.GRAY + " +" + ChatColor.RED + LevelPercentages[i] + " warps" + ChatColor.GRAY + " " + LongName);
            }
            else if (name.equals("Faction Rally Upgrades")) {
                lore.add(ChatColor.RED + "" + ChatColor.BOLD + "✘" + ChatColor.GRAY + " +" + ChatColor.RED + LevelPercentages[i] + "m" + ChatColor.GRAY + " " + LongName);
            }
            else {
                lore.add(ChatColor.RED + "" + ChatColor.BOLD + "✘" + ChatColor.GRAY + " +" + ChatColor.RED + LevelPercentages[i] + "%" + ChatColor.GRAY + " " + LongName);
            }
        }
        lore.add(ChatColor.COLOR_CHAR + " ");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Price");
        if (CurrentLevel != 5) {
            int CurrentPrice = prices[CurrentLevel] / 5;
            lore.add(ChatColor.RED + "" + ChatColor.UNDERLINE + "$" + utilityMain.FormatPrice(CurrentPrice));
        }
        else {
            lore.add(ChatColor.RED + "" + ChatColor.UNDERLINE + "Max Level");
        }
        if (CurrentLevel >= 1) {
            int LoreSlot = (2 + CurrentLevel);
            int StartingSlot = 3;
            if (description2 == null) {
                LoreSlot = (LoreSlot - 1);
                StartingSlot = 2;
            }
            for (int i = StartingSlot; i < (LoreSlot + 1); i++) {
                lore.set(i, lore.get(i).replace(ChatColor.RED + "" + ChatColor.BOLD + "✘", ChatColor.GREEN + "" + ChatColor.BOLD + "✔"));
                String[] SplitLore = lore.get(i).split(" ");
                String NewLore = "";
                for (int a = 2; a < SplitLore.length; a++) {
                    NewLore = NewLore + " " + SplitLore[a];
                }
                NewLore = SplitLore[0] + " " + ChatColor.GREEN + ChatColor.stripColor(SplitLore[1]) + ChatColor.GRAY + NewLore;
                lore.set(i, NewLore);
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private Integer getCurrentLevel(String name, Player player) {
        LoadUpgradesHashmap();
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        Integer CurrentLevel = 0;
        if (name.equals("Xp Upgrades")) {
            if (XpUpgrade.containsKey(FactionId)) {
                CurrentLevel = XpUpgrade.get(FactionId);
            }
        }
        else if (name.equals("Drop Rates Upgrades")) {
            if (DropRateUpgrade.containsKey(FactionId)) {
                CurrentLevel = DropRateUpgrade.get(FactionId);
            }
        }
        else if (name.equals("Crop Growth Upgrades")) {
            if (CropGrowthUpgrade.containsKey(FactionId)) {
                CurrentLevel = CropGrowthUpgrade.get(FactionId);
            }
        }
        else if (name.equals("Tnt Storage Upgrades")) {
            if (TntStorageUpgrade.containsKey(FactionId)) {
                CurrentLevel = TntStorageUpgrade.get(FactionId);
            }
        }
        else if (name.equals("Faction Shield Upgrades")) {
            if (FactionShieldUpgrade.containsKey(FactionId)) {
                CurrentLevel = FactionShieldUpgrade.get(FactionId);
            }
        }
        else if (name.equals("Damage Reduction Upgrades")) {
            if (DamageReductionUpgrade.containsKey(FactionId)) {
                CurrentLevel = DamageReductionUpgrade.get(FactionId);
            }
        }
        else if (name.equals("Faction Warps Upgrades")) {
            if (FactionWarpsUpgrade.containsKey(FactionId)) {
                CurrentLevel = FactionWarpsUpgrade.get(FactionId);
            }
        }
        else if (name.equals("Faction Rally Upgrades")) {
            if (FactionRallyUpgrade.containsKey(FactionId)) {
                CurrentLevel = FactionRallyUpgrade.get(FactionId);
            }
        }
        return CurrentLevel;
    }

    HashMap<String, Integer> XpUpgrade = new HashMap<>();
    HashMap<String, Integer> DropRateUpgrade = new HashMap<>();
    HashMap<String, Integer> CropGrowthUpgrade = new HashMap<>();
    HashMap<String, Integer> TntStorageUpgrade = new HashMap<>();
    HashMap<String, Integer> FactionShieldUpgrade = new HashMap<>();
    HashMap<String, Integer> DamageReductionUpgrade = new HashMap<>();
    HashMap<String, Integer> FactionWarpsUpgrade = new HashMap<>();
    HashMap<String, Integer> FactionRallyUpgrade = new HashMap<>();
    private void LoadUpgradesHashmap() {
        List<String> XpUpgradeStringFromConfig = mainClass.getConfig().getStringList("XpUpgrade");
        for (String string : XpUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            XpUpgrade.put(FactionId, Level);
        }
        List<String> DropRateStringFromConfig = mainClass.getConfig().getStringList("DropRateUpgrade");
        for (String string : DropRateStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            DropRateUpgrade.put(FactionId, Level);
        }
        List<String> CropGrowthUpgradeStringFromConfig = mainClass.getConfig().getStringList("CropGrowthUpgrade");
        for (String string : CropGrowthUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            CropGrowthUpgrade.put(FactionId, Level);
        }
        List<String> TntStorageUpgradeStringFromConfig = mainClass.getConfig().getStringList("TntStorageUpgrade");
        for (String string : TntStorageUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            TntStorageUpgrade.put(FactionId, Level);
        }
        List<String> FactionShieldUpgradeStringFromConfig = mainClass.getConfig().getStringList("FactionShieldUpgrade");
        for (String string : FactionShieldUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            FactionShieldUpgrade.put(FactionId, Level);
        }
        List<String> DamageReductionUpgradeStringFromConfig = mainClass.getConfig().getStringList("DamageReductionUpgrade");
        for (String string : DamageReductionUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            DamageReductionUpgrade.put(FactionId, Level);
        }
        List<String> FactionWarpsUpgradeStringFromConfig = mainClass.getConfig().getStringList("FactionWarpsUpgrade");
        for (String string : FactionWarpsUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            FactionWarpsUpgrade.put(FactionId, Level);
        }
        List<String> FactionRallyUpgradeStringFromConfig = mainClass.getConfig().getStringList("FactionRallyUpgrade");
        for (String string : FactionRallyUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            FactionRallyUpgrade.put(FactionId, Level);
        }
    }

    HashMap<String, Integer> FactionTokens = new HashMap<>();
    private void LoadFactionTokensHashmap() {
        List<String> FactionTokenStringFromConfig = mainClass.getConfig().getStringList("FactionTokens");
        for (String string : FactionTokenStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            FactionTokens.put(FactionId, Level);
        }
    }
}
