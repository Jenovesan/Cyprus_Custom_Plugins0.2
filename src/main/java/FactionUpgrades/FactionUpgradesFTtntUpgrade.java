package FactionUpgrades;

import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FactionUpgradesFTtntUpgrade implements Listener{
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public FactionUpgradesFTtntUpgrade(MainClass mc) {
        mainClass = mc;
    }

    public void StartingPoint(Player player, String[] args) {
        if (args.length == 0 || args.length > 3) {
            ReturnHelpMessage(player);
            return;
        }
        if (!(player.isOp()) && !(utilityMain.isInFaction(player))) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to execute this command");
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                ReturnHelpMessage(player);
                return;
            }
            if (args[0].equalsIgnoreCase("bal")) {
                Balance(player);
                return;
            }
            if (args[0].equalsIgnoreCase("d")) {
                DepositTnt(player);
                return;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("w") && utilityMain.isNumber(args[1])) {
                Withdraw(player, Integer.parseInt(args[1]));
                return;
            }
            else if (player.isOp() && args[0].equalsIgnoreCase("view")) {
                if (Factions.getInstance().isTagTaken(args[1])) {
                    player.sendMessage(ChatColor.YELLOW + args[1] + "'s tnt storage balance is " + ChatColor.GOLD + getTntAmount(Factions.getInstance().getByTag(args[1]).getId()));
                    return;
                }
                else if (Bukkit.getPlayerExact(args[1]) != null) {
                    Player target = Bukkit.getPlayerExact(args[1]).getPlayer();
                    player.sendMessage(ChatColor.YELLOW + args[1] + "'s faction's tnt storage balance is " + ChatColor.GOLD + getTntAmount(FPlayers.getInstance().getByPlayer(target).getFactionId()));
                    return;
                }
            }
        }
        if (args.length == 3 && player.isOp() && Bukkit.getPlayerExact(args[1]) != null) {
            Faction TargetPlayerFaction = FPlayers.getInstance().getByPlayer(Bukkit.getPlayerExact(args[1])).getFaction();
            if (!utilityMain.isInFaction(player)) {
                player.sendMessage(ChatColor.RED + "You must be in a faction to receive the tnt");
                return;
            }
            if (!utilityMain.isNumber(args[2])) {
                ReturnHelpMessage(player);
                return;
            }
            else if (args[0].equalsIgnoreCase("give")) {
                ChangeTntAmount(TargetPlayerFaction, Integer.parseInt(args[2]));
                player.sendMessage(ChatColor.GREEN + "Gave " + ChatColor.DARK_GREEN + args[1] + " " + args[2] + ChatColor.GREEN + " tnt");
                return;
            }
            else if (args[0].equalsIgnoreCase("take")) {
                ChangeTntAmount(TargetPlayerFaction, -Integer.parseInt(args[2]));
                player.sendMessage(ChatColor.GREEN + "Took " + ChatColor.DARK_GREEN + args[2] + ChatColor.GREEN + " tnt from " + ChatColor.DARK_GREEN + args[1]);
                return;
            }
        }
        ReturnHelpMessage(player);
    }

    private void Withdraw(Player player, Integer amount) {
        Integer amountOfTntPlayerCanHold = AmountOfTntPlayerCanHold(player);
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        if (amount > getTntAmount(FactionId)) {
            player.sendMessage(ChatColor.RED + "Your faction's tnt storage does not contain that amount of tnt");
            return;
        }
        if (amount <= amountOfTntPlayerCanHold) {
            player.getInventory().addItem(new ItemStack(Material.TNT, amount));
            ChangeTntAmount(FPlayers.getInstance().getByPlayer(player).getFaction(), -amount);
            player.sendMessage(ChatColor.GREEN + "You have withdrawn " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " tnt from your faction's tnt storage, leaving your faction with " + ChatColor.DARK_GREEN + getTntAmount(FactionId) + ChatColor.GREEN + " tnt");
        }
        else {
            player.sendMessage(ChatColor.RED + "Your inventory cannot hold " + amount + " tnt. Thus, you have withdrawn " + amountOfTntPlayerCanHold + " tnt from your faction's tnt storage into your inventory");
            player.getInventory().addItem(new ItemStack(Material.TNT, amountOfTntPlayerCanHold));
            ChangeTntAmount(FPlayers.getInstance().getByPlayer(player).getFaction(), -amountOfTntPlayerCanHold);
        }
    }

    public void ChangeTntAmount(Faction faction, Integer amount) {
        String FactionId = faction.getId();
        int NewTntAmount = getTntAmount(FactionId) + amount;
        LoadFactionsTntAmountHashmapFromConfig();
        FactionsTntAmount.put(FactionId, NewTntAmount);
        if (FactionsTntAmount.get(FactionId) > getMaxStorageSize(FactionId)) {
            FactionsTntAmount.replace(FactionId, getMaxStorageSize(FactionId));
        }
        SaveFactionsTntAmountHashmapInConfig();
    }

    private Integer AmountOfTntPlayerCanHold(Player player) {
        int AmountOfTnt = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null) {
                if (itemStack.getType().equals(Material.TNT)) {
                    AmountOfTnt = + (64 - itemStack.getAmount());
                }
            }
            else {
                AmountOfTnt = (AmountOfTnt + 64);
            }
        }
        return AmountOfTnt;
    }

    private void DepositTnt(Player player) {
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        Integer AmountOfTntInPlayersInventory = getAmountOfTntInInventory(player);
        Integer AmountOfTntFactionHas = getTntAmount(FactionId);
        Integer MaxAmountOfTnt = getMaxStorageSize(FactionId);
        Integer PotentialNewTntAmount = AmountOfTntFactionHas + AmountOfTntInPlayersInventory;
        if (PotentialNewTntAmount > MaxAmountOfTnt) {
            player.sendMessage(ChatColor.RED + "You faction's tnt storage cannot hold that much tnt");
            return;
        }
        ChangeTntAmount(FPlayers.getInstance().getByPlayer(player).getFaction(), PotentialNewTntAmount);
        player.getInventory().remove(Material.TNT);
        player.sendMessage(ChatColor.GREEN + "Added " + ChatColor.DARK_GREEN + AmountOfTntInPlayersInventory + ChatColor.GREEN + " tnt to your faction's tnt storage to now total " + ChatColor.DARK_GREEN + PotentialNewTntAmount + ChatColor.GREEN + " tnt");
    }


    private Integer getAmountOfTntInInventory(Player player) {
        int TotalTntInInventory = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null) {
                if (itemStack.getType().equals(Material.TNT)) {
                    TotalTntInInventory = TotalTntInInventory + itemStack.getAmount();
                }
            }
        }
        return TotalTntInInventory;
    }

    private void ReturnHelpMessage(CommandSender player) {
        player.sendMessage(ChatColor.GRAY + "f tnt usage:" + "\n" + "/f tnt bal - shows your faction's tnt balance" + "\n" + "/f tnt d - deposit tnt in your inventory into faction's tnt storage" + "\n" + "/f tnt w <amount> - withdraw tnt from your faction's tnt storage");
        if (player.isOp()) {
            player.sendMessage(ChatColor.GOLD + "/f tnt give <player name> <amount> - gives the faction tnt" + "\n" + "/f tnt take <player name> <amount> - takes tnt from the faction" + "\n" + "/f tnt view <faction name or player name> - shows that faction's tnt balance");
        }
    }

    private void Balance(Player player) {
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        player.sendMessage(ChatColor.DARK_RED + "Tnt Storage: " + ChatColor.RED + utilityMain.FormatPrice(getTntAmount(FactionId)) + "/" + utilityMain.FormatPrice(getMaxStorageSize(FactionId)));
    }

    private Integer getMaxStorageSize(String FactionId) {
        LoadTntStorageUpgradeHashmapFromConfig();
        int TntStorageAmount = 250000;
        if (TntStorageUpgrade.containsKey(FactionId)) {
            Integer level = TntStorageUpgrade.get(FactionId);
            switch (level) {
                case 1:
                    TntStorageAmount = 1000000;
                    break;
                case 2:
                    TntStorageAmount = 3000000;
                    break;
                case 3:
                    TntStorageAmount = 7500000;
                    break;
                case 4:
                    TntStorageAmount = 15000000;
                    break;
                case 5:
                    TntStorageAmount = 25000000;
                    break;
            }
        }
        return TntStorageAmount;
    }

    public Integer getTntAmount(String FactionsId) {
        LoadFactionsTntAmountHashmapFromConfig();
        return FactionsTntAmount.getOrDefault(FactionsId, 0);
    }

    HashMap<String, Integer> TntStorageUpgrade = new HashMap<>();
    private void LoadTntStorageUpgradeHashmapFromConfig() {
        List<String> TntStorageUpgradeStringFromConfig = mainClass.getConfig().getStringList("TntStorageUpgrade");
        for (String string : TntStorageUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            TntStorageUpgrade.put(FactionId, Level);
        }
    }

    HashMap<String, Integer> FactionsTntAmount = new HashMap<>();
    private void LoadFactionsTntAmountHashmapFromConfig() {
        List<String> FactionsTntAmountStringFromConfig = mainClass.getConfig().getStringList("FactionsTntAmount");
        for (String string : FactionsTntAmountStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer amount = Integer.parseInt(SplitString[1]);
            FactionsTntAmount.put(FactionId, amount);
        }
    }

    private void SaveFactionsTntAmountHashmapInConfig() {
        List<String> FactionsTntAmountStringList = new ArrayList<>();
        for (Map.Entry element : FactionsTntAmount.entrySet()) {
            String FactionId = (String) element.getKey();
            Integer amount = (Integer) element.getValue();
            FactionsTntAmountStringList.add(FactionId + ":" + amount);
        }
        mainClass.getConfig().set("FactionsTntAmount", FactionsTntAmountStringList);
        mainClass.saveConfig();
    }
}
