package BlackMarket;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class BlackMarketPlayerEvents implements Listener {
    MainClass mainClass;
    BlackMarketManager blackMarketManager;
    BlackMarketCommandsAndGui blackMarketCommandsAndGui;
    public BlackMarketPlayerEvents(MainClass mc) {
        mainClass = mc;
        blackMarketManager = new BlackMarketManager(mainClass);
        blackMarketCommandsAndGui = new BlackMarketCommandsAndGui(mainClass);
    }
    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getSlot() == -999) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getName().equals(ChatColor.WHITE + "" + "☠ " + ChatColor.RED  + "" + ChatColor.BOLD +  "Black Market " + ChatColor.WHITE + "☠")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
                player.closeInventory();
            }
            if (event.getCurrentItem().getType().equals(Material.COMPASS)) {
                Refresh(player);
            }
            else if (event.getSlot() == 10) {
                blackMarketCommandsAndGui.LoadConfirmGui(player, ChatColor.GREEN + "Confirm");
            }
            else if (event.getSlot() == 13) {
                blackMarketCommandsAndGui.LoadConfirmGui(player, ChatColor.YELLOW + "Confirm");
            }
            else if (event.getSlot() == 16) {
                blackMarketCommandsAndGui.LoadConfirmGui(player, ChatColor.RED + "Confirm");
            }
        }
        else if (event.getInventory().getName().equals(ChatColor.GREEN + "Confirm")) {
            event.setCancelled(true);
            blackMarketCommandsAndGui.LoadConfirmGui(player, event.getInventory().getName());
            if (!(event.getCurrentItem().hasItemMeta())) {
                return;
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.GREEN + "")) { //Clicked confirm
                if (event.getInventory().getName().contains(ChatColor.GREEN + "")) {
                    BidCommonItem(player);
                    blackMarketCommandsAndGui.LoadBlackMarketGui(player);
                }
                else if (event.getInventory().getName().contains(ChatColor.YELLOW + "")) {
                    BidUncommonItem(player);
                    blackMarketCommandsAndGui.LoadBlackMarketGui(player);
                }
                else if (event.getInventory().getName().contains(ChatColor.RED + "")) {
                    BidRareItem(player);
                    blackMarketCommandsAndGui.LoadBlackMarketGui(player);
                }
            }
            else if (event.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.RED + "")){ //Clicked exit
                blackMarketCommandsAndGui.LoadBlackMarketGui(player);
            }
        }
    }

    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        Player player =  event.getPlayer();
        AddCommonItemsWhenJoin(player);
        AddUncommonItemsWhenJoin(player);
        AddRareItemsWhenJoin(player);
    }

    private void AddCommonItemsWhenJoin(Player person) {
        blackMarketManager.LoadPlayersToAddItems();
        blackMarketManager.LoadItemsToAddItems();
        List<String> playersToAddCommons = blackMarketManager.PlayersToAddCommons;
        List<ItemStack> itemsToAddCommons = blackMarketManager.ItemsToAddCommons;
        for (int i = 0; i < playersToAddCommons.size(); i++) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playersToAddCommons.get(i)));
            if (player.equals(person)) {
                person.getInventory().addItem(itemsToAddCommons.get(i));
                playersToAddCommons.remove(playersToAddCommons.get(i));
                itemsToAddCommons.remove(itemsToAddCommons.get(i));
                mainClass.getConfig().set("PlayersToAddCommon", playersToAddCommons);
                mainClass.getConfig().set("ItemsToAddCommon", itemsToAddCommons);
                blackMarketManager.SaveBlackMarket();
                break;
            }
        }
    }
    private void AddUncommonItemsWhenJoin(Player person) {
        blackMarketManager.LoadPlayersToAddItems();
        blackMarketManager.LoadItemsToAddItems();
        List<String> playersToAddUncommons = blackMarketManager.PlayersToAddUncommons;
        List<ItemStack> itemsToAddUncommons = blackMarketManager.ItemsToAddUncommons;
        for (int i = 0; i < playersToAddUncommons.size(); i++) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playersToAddUncommons.get(i)));
            if (player.equals(person)) {
                person.getInventory().addItem(itemsToAddUncommons.get(i));
                playersToAddUncommons.remove(playersToAddUncommons.get(i));
                itemsToAddUncommons.remove(itemsToAddUncommons.get(i));
                mainClass.getConfig().set("PlayersToAddUncommon", playersToAddUncommons);
                mainClass.getConfig().set("ItemsToAddUncommon", itemsToAddUncommons);
                blackMarketManager.SaveBlackMarket();
                break;
            }
        }
    }
    private void AddRareItemsWhenJoin(Player person) {
        blackMarketManager.LoadPlayersToAddItems();
        blackMarketManager.LoadItemsToAddItems();
        List<String> playersToAddRares = blackMarketManager.PlayersToAddRares;
        List<ItemStack> itemsToAddRares = blackMarketManager.ItemsToAddRares;
        for (int i = 0; i < playersToAddRares.size(); i++) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playersToAddRares.get(i)));
            if (player.equals(person)) {
                person.getInventory().addItem(itemsToAddRares.get(i));
                playersToAddRares.remove(playersToAddRares.get(i));
                itemsToAddRares.remove(itemsToAddRares.get(i));
                mainClass.getConfig().set("PlayersToAddRare", playersToAddRares);
                mainClass.getConfig().set("ItemsToAddRare", itemsToAddRares);
                blackMarketManager.SaveBlackMarket();
                break;
            }
        }
    }

    private void BidCommonItem(Player player) {
        blackMarketManager.BidForCommonItem(player);
        blackMarketCommandsAndGui.LoadBlackMarketGui(player);
    }
    private void BidUncommonItem(Player player) {
        blackMarketManager.BidForUncommonItem(player);
        blackMarketCommandsAndGui.LoadBlackMarketGui(player);
    }
    private void BidRareItem(Player player) {
        blackMarketManager.BidForRareItem(player);
        blackMarketCommandsAndGui.LoadBlackMarketGui(player);
    }

    private void Refresh(Player player) {
        blackMarketCommandsAndGui.LoadBlackMarketGui(player);
    }
}
