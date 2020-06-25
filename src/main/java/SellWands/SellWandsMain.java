package SellWands;

import Conquest.ConquestMain;
import CustomItems.CustomItemsMain;
import FactionUpgrades.FactionUpgradesFTtntUpgrade;
import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayers;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SellWandsMain extends CommandExecute implements Listener, CommandExecutor {
    CustomItemsMain customItemsMain = new CustomItemsMain();
    UtilityMain utilityMain = new UtilityMain();
    FactionUpgradesFTtntUpgrade factionUpgradesFTtntUpgrade;
    MainClass mainClass;
    ConquestMain conquestMain = mainClass.conquestMain;
    public SellWandsMain(MainClass mc) {
        mainClass = mc;
        factionUpgradesFTtntUpgrade = new FactionUpgradesFTtntUpgrade(mc);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.isOp()) {
            return false;
        }
        if (args.length < 2 || Bukkit.getPlayerExact(args[1]) == null || !Bukkit.getPlayerExact(args[1]).isOnline() || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(HelpMessage);
            return true;
        }
        if (args.length == 2) {
            Player TargetPlayer = Bukkit.getPlayerExact(args[1]);
            utilityMain.GivePlayerItem(TargetPlayer, customItemsMain.SellWand(null), 1);
            return true;
        } else if (args.length == 3) {
            Player TargetPlayer = Bukkit.getPlayerExact(args[1]);
            if (utilityMain.isNumber(args[2])) {
                utilityMain.GivePlayerItem(TargetPlayer, customItemsMain.SellWand(Integer.parseInt(args[2])), 1);
                return true;
            }
        }
        sender.sendMessage(HelpMessage);
        return true;
    }
    String HelpMessage = ChatColor.GRAY + "Usage: /sellwand give <Player Name> Uses";

    @EventHandler
    private void CheckIfClickedChest(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST) || event.getClickedBlock().getType().equals(Material.CHEST))) {
            return;
        }
        if (player.getItemInHand() == null || !player.getItemInHand().getType().equals(Material.BLAZE_ROD) || player.getItemInHand().getItemMeta() == null || !player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "" + ChatColor.BOLD + "Sell Wand")) {
            return;
        }
        Chest chest = (Chest) event.getClickedBlock().getState();
        event.setCancelled(true);
        SellContents(player, chest.getBlockInventory().getContents(), chest);
        if (hasUses(player.getItemInHand().getItemMeta().getDisplayName())) {
            RemoveUse(player, player.getItemInHand());
        }
    }

    private boolean hasUses(String name) {
        return name.contains(ChatColor.GOLD + "");
    }

    private void RemoveUse(Player player, ItemStack wand) {
        String name = wand.getItemMeta().getDisplayName();
        int uses = Integer.parseInt(ChatColor.stripColor(name).replaceAll("[^0-9.]", ""));
        int NewUse = uses - 1;
        if (NewUse == 0) {
            player.setItemInHand(null);
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            return;
        }
        player.setItemInHand(customItemsMain.SellWand(NewUse));
    }

    private void SellContents(Player player, ItemStack[] contents, Chest chest) {
        Economy economy  = MainClass.getEconomy();
        int TotalValue = 0;
        int TotalTnt = 0;
        for (ItemStack itemStack : contents) {
            if (itemStack != null && itemStack.getType().equals(Material.TNT)) {
                int amount = itemStack.getAmount();
                factionUpgradesFTtntUpgrade.ChangeTntAmount(FPlayers.getInstance().getByPlayer(player).getFaction(), amount);
                TotalTnt = TotalTnt + amount;
                itemStack.setType(Material.AIR);
            } else {
                double price = ShopGuiPlusApi.getItemStackPriceSell(player, itemStack);
                if (price != -1) {
                    assert itemStack != null;
                    if (FPlayers.getInstance().getByPlayer(player).getFaction().equals(conquestMain.conquestCaptor)) {
                        price = price * 1.5;
                    }
                    itemStack.setType(Material.AIR);
                    TotalValue = TotalValue + ((int) price);
                }
            }
        }
        chest.getBlockInventory().setContents(contents);
        Integer SellBoost = mainClass.sellBoostEvents.getSellBoostPercentage(player);
        double TotalValueWithSellBoost = TotalValue * ((double) SellBoost / 100 + 1);
        player.sendMessage("Sell Boost: " + SellBoost);
        economy.depositPlayer(player, TotalValueWithSellBoost);
        chest.update();
        if (TotalValue > 0) {
            player.sendMessage(ChatColor.GREEN + "You gained " + ChatColor.DARK_GREEN + "$" + utilityMain.FormatPrice((int) TotalValueWithSellBoost));
        }
        if (TotalTnt > 0) {
            player.sendMessage(ChatColor.RED + "You gained " + ChatColor.DARK_RED + utilityMain.FormatPrice(TotalTnt) + " tnt");
        }
    }
}
