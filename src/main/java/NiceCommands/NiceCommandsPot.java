package NiceCommands;

import Main.MainClass;
import Utility.UtilityMain;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NiceCommandsPot extends CommandExecute implements CommandExecutor {
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        GivePots((Player) sender);
        return true;
    }

    private void GivePots(Player player) {
        Economy economy = MainClass.getEconomy();
        int AmountOfPotsToGive = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack == null) {
                AmountOfPotsToGive = AmountOfPotsToGive + 1;
            }
        }
        ItemStack potions = new ItemStack(Material.POTION, AmountOfPotsToGive, (short) 16421);
        ItemStack SinglePotion = new ItemStack(Material.POTION, 1, (short) 16421);
        double price = ShopGuiPlusApi.getItemStackPriceBuy(player, potions);
        economy.withdrawPlayer(player, price);
        for (int i = 0; i < AmountOfPotsToGive; i++) {
            player.getInventory().addItem(SinglePotion);
        }
        player.sendMessage(ChatColor.GREEN + "You bought " + ChatColor.DARK_GREEN + AmountOfPotsToGive + ChatColor.GREEN + " instant health II for " + ChatColor.DARK_GREEN + "$" + utilityMain.FormatPrice((int) price));
    }
}
