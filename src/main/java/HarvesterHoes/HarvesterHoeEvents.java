package HarvesterHoes;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Main.MainClass;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.md_5.bungee.api.ChatColor;

public class HarvesterHoeEvents implements Listener{
    MainClass mainClass;
    public HarvesterHoeEvents(MainClass mc) {
        mainClass = mc;
    }
    public int CaneGrowthHeight = 4;
    public int DifficultyToUpgrade = 5;
    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player EventUser = event.getPlayer();
        if (EventUser.getItemInHand() != null) {
            if (EventUser.getItemInHand().hasItemMeta()) {
                if (EventUser.getItemInHand().getItemMeta().hasDisplayName()) {
                    if (EventUser.getInventory().getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Harvester Hoe")) {
                        Block block = event.getBlock();
                        Material BlockType = block.getType();
                        event.setCancelled(true);
                        if (BlockType.equals(Material.SUGAR_CANE_BLOCK) && event.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
                            int AmountOfCane = 0;
                            for (int i = 0; i < (CaneGrowthHeight - 1); i++) {
                                Block CheckIfCaneBlock = (block.getLocation().add(0, i, 0)).getBlock();
                                if (CheckIfCaneBlock.getType().equals(Material.SUGAR_CANE_BLOCK)) {
                                    AmountOfCane++;
                                    event.setCancelled(true);
                                }
                                else {
                                    break;
                                }
                            }
                            for (int i = (AmountOfCane - 1); i > -1; i--) {
                                block.getLocation().add(0,i,0).getBlock().setType(Material.AIR);
                            }
                            int TotalCane = AmountOfCane + ExtraCane(AmountOfCane, EventUser, AmountOfCane);
                            ItemStack SugarCaneFromHarvesterHoe = new ItemStack(Material.SUGAR_CANE, TotalCane);
                            if (!(EventUser.getItemInHand().getItemMeta().getLore().contains(ChatColor.GREEN + "" + ChatColor.BOLD + "Auto Sell"))) {
                                EventUser.getInventory().addItem(SugarCaneFromHarvesterHoe);
                                if (EventUser.getInventory().firstEmpty() == -1) {
                                    EventUser.sendMessage(ChatColor.RED + "You inventory is full. Do " + ChatColor.DARK_RED + "/scane" + ChatColor.RED + " to sell the cane in your inventory");
                                }
                            }
                            else {
                                GivePlayerMoney(EventUser, TotalCane);
                            }
                            AddHoeExp(TotalCane, EventUser);
                        }
                    }
                }
            }
        }
    }

    private void GivePlayerMoney(Player player, Integer TotalCane) {
        Economy economy  = MainClass.getEconomy();
        Integer SellBoost = mainClass.sellBoostEvents.getSellBoostPercentage(player);
        double CanePrice = ShopGuiPlusApi.getItemStackPriceSell(player, new ItemStack(Material.SUGAR_CANE, TotalCane));
        economy.depositPlayer(player, CanePrice * ((double) SellBoost / 100 + 1));
    }

    public void DrawBars(Player EventUser, ItemStack HarvesterHoe) {
        int ExpPerBar = (((HarvesterHoeExpForLevel(GetHarvesterHoeLevel(EventUser))) - (HarvesterHoeExpForLevel(GetHarvesterHoeLevel(EventUser) - 1))) / 50);
        int ExpThisLevel = (GetCurrentExpAmount(HarvesterHoe) - (HarvesterHoeExpForLevel(GetHarvesterHoeLevel(EventUser) - 1)));
        int AmountOfBars = ExpThisLevel / ExpPerBar;
        ItemMeta HarvesterHoeItemMeta = HarvesterHoe.getItemMeta();
        List<String> HarvesterHoeLoreList = HarvesterHoeItemMeta.getLore();
        List<String> ExpBarList = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            if (i < AmountOfBars) {
                ExpBarList.add(ChatColor.RED + "|");
            }
            else {
                ExpBarList.add(ChatColor.GRAY + "|");
            }
        }
        String ExpBarString = String.join("", ExpBarList);
        String FinalExpBarString = new StringBuilder().append(ChatColor.YELLOW + "Level " + GetHarvesterHoeLevel(EventUser) + " ").append(ExpBarString).append(ChatColor.YELLOW + " Level " + Integer.toString(GetHarvesterHoeLevel(EventUser) + 1)).toString();
        HarvesterHoeLoreList.remove(1);
        HarvesterHoeLoreList.add(1, FinalExpBarString);
        HarvesterHoeItemMeta.setLore(HarvesterHoeLoreList);
        HarvesterHoe.setItemMeta(HarvesterHoeItemMeta);
    }

    public int HarvesterHoeExpForLevel(int x) {
        int i = (int) (1000 * (DifficultyToUpgrade * (Math.pow(x, 2))));
        return  i;
    }

    private int GetCurrentExpAmount(ItemStack HarvesterHoe) {
        ItemMeta ItemMetaForHarvesterHoe = HarvesterHoe.getItemMeta();
        List<String> HarvesterHoeLoreList = ItemMetaForHarvesterHoe.getLore();
        String HarvesterHoeExpString = (ChatColor.stripColor(HarvesterHoeLoreList.get(4).replace("Cane Broken: ", "")));
        int HarvesterHoeExp = Integer.parseInt(ChatColor.stripColor(HarvesterHoeExpString));
        return HarvesterHoeExp;
    }

    private void AddHoeExp(int AmountOfCaneGathered, Player EventUser) {
        ItemStack EventUserHarvesterHoe = EventUser.getItemInHand();
        ItemMeta ItemMetaForHarvesterHoe = EventUserHarvesterHoe.getItemMeta();
        List<String> HarvesterHoeLoreList = ItemMetaForHarvesterHoe.getLore();
        int HarvesterHoeExp = GetCurrentExpAmount(EventUserHarvesterHoe);
        int NewExpAmount = HarvesterHoeExp + AmountOfCaneGathered;
        HarvesterHoeLoreList.remove(4);
        HarvesterHoeLoreList.add(4, ChatColor.GRAY + "Cane Broken: " + ChatColor.RED + Integer.toString(NewExpAmount));
        ItemMetaForHarvesterHoe.setLore(HarvesterHoeLoreList);
        EventUserHarvesterHoe.setItemMeta(ItemMetaForHarvesterHoe);
        if (CheckForHarvesterHoeLevelUp(NewExpAmount, EventUser)) {
            LevelUpHarvesterHoe(EventUser, EventUserHarvesterHoe);
            UpdateDropRatesLore(EventUser, EventUser.getInventory().getItemInHand());
        }
        UpdateDropRatesLore(EventUser, EventUserHarvesterHoe);
        DrawBars(EventUser, EventUserHarvesterHoe);
    }
    private void LevelUpHarvesterHoe(Player EventUser, ItemStack HarvesterHoe) {
        ItemMeta HarvesterHoeMeta = HarvesterHoe.getItemMeta();
        HarvesterHoeMeta.setDisplayName(ChatColor.RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Harvester Hoe " + ChatColor.GRAY + "(" + ChatColor.RED + Integer.toString(GetHarvesterHoeLevel(EventUser) + 1) + ChatColor.GRAY + ")");
        HarvesterHoe.setItemMeta(HarvesterHoeMeta);
        if (GetHarvesterHoeLevel(EventUser) < 14) {
            EventUser.sendMessage(ChatColor.GREEN + "Your Harvester Hoe leveled Up!");
        }
        else {
            EventUser.sendMessage(ChatColor.RED + "Your Harvester has reached max level");
        }
    }

    private boolean CheckForHarvesterHoeLevelUp(int HarvesterHoeExp, Player EventUser) {
        if (HarvesterHoeExp > (HarvesterHoeExpForLevel(GetHarvesterHoeLevel(EventUser)))) {
            if (GetHarvesterHoeLevel(EventUser) < 15) {
                EventUser.getWorld().playSound(EventUser.getLocation(), Sound.LEVEL_UP, 10, 1);
                EventUser.getItemInHand().setDurability((short) 0);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    private int GetHarvesterHoeLevel(Player EventUser) {
        int HarvesterHoeLevel = 1;
        String HarvesterHoeName = EventUser.getInventory().getItemInHand().getItemMeta().getDisplayName();
        String Simplified = ChatColor.stripColor(HarvesterHoeName.replace("Harvester Hoe", "").replace(" ", "").replace("(", "").replace(")", ""));
        HarvesterHoeLevel = Integer.parseInt(Simplified);
        return HarvesterHoeLevel;
    }

    private void UpdateDropRatesLore(Player EventUser, ItemStack HarvesterHoe) {
        int HarvesterHoeLevel = GetHarvesterHoeLevel(EventUser);
        ItemMeta HarvesterHoeMeta = HarvesterHoe.getItemMeta();
        List<String> HarvesterHoeLore = HarvesterHoeMeta.getLore();
        HarvesterHoeLore.remove(3);
        switch (HarvesterHoeLevel) {
            case 1:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "5%"));
                break;
            case 2:
                HarvesterHoeLore.add(3, (ChatColor.GRAY+ "Double Yield: " + ChatColor.RED + "15%"));
                break;
            case 3:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "30%"));
                break;
            case 4:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "55%"));
                break;
            case 5:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Double Yield: " + ChatColor.RED + "100%"));
                break;
            case 6:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "5%"));
                break;
            case 7:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "15%"));
                break;
            case 8:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "30%"));
                break;
            case 9:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "55%"));
                break;
            case 10:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Triple Yield: " + ChatColor.RED + "100%"));
                break;
            case 11:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "5%"));
                break;
            case 12:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "15%"));
                break;
            case 13:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "30%"));
                break;
            case 14:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "55%"));
                break;
            case 15:
                HarvesterHoeLore.add(3, (ChatColor.GRAY + "Quadruple Yield: " + ChatColor.RED + "100%"));
                break;
        }
        HarvesterHoeMeta.setLore(HarvesterHoeLore);
        HarvesterHoe.setItemMeta(HarvesterHoeMeta);
    }

    private int ExtraCane(int AmountOfCaneBroken, Player EventUser, int CaneBroken) {
        Random rand = new Random();
        int RandomNumber = rand.nextInt(100);
        int HarvesterHoeLevel = GetHarvesterHoeLevel(EventUser);
        int CaneAddAmount = 0;
        if (HarvesterHoeLevel < 6) {
            switch(HarvesterHoeLevel) {
                case 1:
                    if (RandomNumber <= 4) {
                        CaneAddAmount = 1;
                    }
                    break;
                case 2:
                    if (RandomNumber <= 14) {
                        CaneAddAmount = 1;
                    }
                    break;
                case 3:
                    if (RandomNumber <= 29) {
                        CaneAddAmount = 1;
                    }
                    break;
                case 4:
                    if (RandomNumber <= 54) {
                        CaneAddAmount = 1;
                    }
                    break;
                case 5:
                    CaneAddAmount = 1;
                    break;
            }
        }
        else if (HarvesterHoeLevel > 5 && HarvesterHoeLevel < 11) {
            CaneAddAmount = 1;
            switch(HarvesterHoeLevel) {
                case 6:
                    if (RandomNumber <= 4) {
                        CaneAddAmount = 2;
                    }
                    break;
                case 7:
                    if (RandomNumber <= 14) {
                        CaneAddAmount = 2;
                    }
                    break;
                case 8:
                    if (RandomNumber <= 29) {
                        CaneAddAmount = 2;
                    }
                    break;
                case 9:
                    if (RandomNumber <= 54) {
                        CaneAddAmount = 2;
                    }
                    break;
                case 10:
                    CaneAddAmount = 2;
                    break;
            }
        }
        else if (HarvesterHoeLevel > 10) {
            CaneAddAmount = 2;
            switch(HarvesterHoeLevel) {
                case 11:
                    if (RandomNumber <= 4) {
                        CaneAddAmount = 3;
                    }
                    break;
                case 12:
                    if (RandomNumber <= 14) {
                        CaneAddAmount = 3;
                    }
                    break;
                case 13:
                    if (RandomNumber <= 29) {
                        CaneAddAmount = 3;
                    }
                    break;
                case 14:
                    if (RandomNumber <= 54) {
                        CaneAddAmount = 3;
                    }
                    break;
                case 15:
                    CaneAddAmount = 3;
                    break;
            }
        }
        return CaneAddAmount * CaneBroken;
    }
}

