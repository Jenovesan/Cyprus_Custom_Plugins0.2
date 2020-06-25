package NiceCommands;

import Main.MainClass;
import Utility.UtilityMain;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NiceCommandsCane extends CommandExecute implements CommandExecutor {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public NiceCommandsCane(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;
        Scane(player);
        return true;
    }

    private void Scane(Player player) {
        Economy economy  = MainClass.getEconomy();
        int SellBoost = mainClass.sellBoostEvents.getSellBoostPercentage(player);
        double value = 0;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack != null && itemStack.getType().equals(Material.SUGAR_CANE)) {
                double price = ShopGuiPlusApi.getItemStackPriceSell(player, itemStack);
                value = value + price;
                inv.setItem(i, null);
            }
        }
        double FinalValue = value * ((double) SellBoost / 100 + 1);
        economy.depositPlayer(player, FinalValue);
        player.sendMessage(ChatColor.GREEN + "Sold cane for: " + ChatColor.DARK_GREEN + "$" + utilityMain.FormatPrice((int) FinalValue));
    }
}
