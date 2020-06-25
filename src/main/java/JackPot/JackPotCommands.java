package JackPot;

import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class JackPotCommands implements Listener {
    MainClass mainClass;
    JackPotEvents jackPotEvents;
    public JackPotCommands(MainClass mc) {
        mainClass = mc;
        jackPotEvents = new JackPotEvents(mainClass);
    }
    UtilityMain utilityMain = new UtilityMain();

    public void StartingPoint(Player player, String[] args) {
        if (!(player.isOp())) {
            LoadJackPotGui(player);
            return;
        }
        if (args.length == 0) {
            LoadJackPotGui(player);
            return;
        }
        if (args.length != 2) {
            UsageMessage(player);
            return;
        }
        if (player.isOp()) {
            if (args[0].equalsIgnoreCase("win")) {
                if (Bukkit.getPlayerExact(args[1]) != null) {
                    jackPotEvents.MakePlayerWinJackpot(Bukkit.getPlayerExact(args[1]));
                }
                else {
                    UsageMessage(player);
                }
            }
            if (isNumber(args[1])) {
                if (args[0].equalsIgnoreCase("add")) {
                    jackPotEvents.changeJackPotTotal(Long.parseLong(args[1]));
                    player.sendMessage(ChatColor.GREEN + "$" + args[1] + " added to the jackpot");
                }
                else if (args[0].equalsIgnoreCase("remove")) {
                    jackPotEvents.changeJackPotTotal(-(Long.parseLong(args[1])));
                    player.sendMessage(ChatColor.GREEN + "$" + args[1] + " removed from the jackpot");
                }
                else if (args[0].equalsIgnoreCase("set")) {
                    jackPotEvents.setJackPotTotal(Long.parseLong(args[1]));
                    player.sendMessage(ChatColor.GREEN + "Jackpot set to: $" + args[1]);
                }
                else {
                    UsageMessage(player);
                }
            }
        }
    }


    private void UsageMessage(Player player) {
        if (player.isOp()) {
            player.sendMessage(ChatColor.GRAY + "/jackpot <Add/Remove/Set> <Amount>" + "\n" + "/jackpot win <Player>");
        }
    }

    private boolean isNumber(String PotentialNumber) {
        try {
            Integer.parseInt(PotentialNumber);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private void LoadJackPotGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 27, ChatColor.GOLD + "" + ChatColor.BOLD +  "Jackpot");
        AddJackPotGuiItems(inv, player);
        player.openInventory(inv);
    }

    public void AddJackPotGuiItems(Inventory inv, Player player) {
        utilityMain.FillCyprusGuiSize27(inv);
        inv.setItem(10, utilityMain.createGuiItem(Material.PAPER, ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-----------------------", ChatColor.GOLD + " " + ChatColor.UNDERLINE + "What Increases The JackPot", ChatColor.COLOR_CHAR + ".", ChatColor.YELLOW + "  Player Death ➵ +$10,000", ChatColor.YELLOW + "  Store Purchase ➵ +$100,000", ChatColor.YELLOW + "  New Player Joins ➵ +$10,000", ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-----------------------"));
        inv.setItem(13, utilityMain.createGuiItem(Material.GOLD_INGOT, ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------", ChatColor.YELLOW + " " + ChatColor.UNDERLINE + "Current Jackpot Amount", ChatColor.COLOR_CHAR + ".", ChatColor.YELLOW + "          $" + utilityMain.FormatPrice((int) jackPotEvents.getJackPotTotal()), ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------"));
        inv.setItem(16, utilityMain.createGuiItem(Material.TRIPWIRE_HOOK, ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "------------------", ChatColor.YELLOW + "  Purchase crate keys", ChatColor.YELLOW + " for a chance to win at", ChatColor.RED + "   " + ChatColor.UNDERLINE + "store.cyprusmc.com", ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "------------------"));
    }


    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof  Player)) {
            return;
        }
        if (event.getInventory().getName().equals(ChatColor.GOLD + "" + ChatColor.BOLD + "Jackpot")) {
            event.setCancelled(true);
        }
    }
}
