package CustomFactionCommands;

import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CustomFactionCommandsFChest implements Listener {
    MainClass mainClass;
    public CustomFactionCommandsFChest(MainClass mc) {
        mainClass = mc;
}

    public void StartingPoint(Player player, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reset") && player.isOp()) {
            FChestReset(player);
            player.sendMessage(ChatColor.GREEN + "F Chests reset");
            return;
        } if (args.length == 2 && args[0].equalsIgnoreCase("view") && player.isOp()) {
            if (Bukkit.getPlayerExact(args[1]) != null) {
                LoadFChest(Bukkit.getPlayer(args[1]));
                PlayersViewingAFChest.add(player);
            } else {
                player.sendMessage(ChatColor.GRAY + "/f chest view <Player Name>");
            }
            return;
        }
        LoadFChest(player);
    }

    Set<Player> PlayersViewingAFChest = new HashSet<>();

    public void LoadFChest(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 36, ChatColor.GREEN + "" + ChatColor.BOLD + "Faction Chest");
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        inv.setContents(FChests.getOrDefault(FactionId, inv).getContents());
        player.openInventory(inv);
    }

    private void FChestReset(Player player) {
        FChests.put(FPlayers.getInstance().getByPlayer(player).getFactionId(), Bukkit.createInventory(null, 36, ChatColor.GREEN + "" + ChatColor.BOLD + "Faction Chest"));
    }

    @EventHandler
    private void InventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null && player.getOpenInventory().getTopInventory().getName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Faction Chest")) {
            if (PlayersViewingAFChest.contains(player)) {
                PlayersViewingAFChest.remove(player);
                return;
            }
            FChests.put(FactionId, event.getInventory());
        }
        SaveFChests();
        UpdateFChest(player);
    }

    @EventHandler
    private void InventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (PlayersViewingAFChest.contains(player)) {
            player.sendMessage(ChatColor.RED + "You cannot change another faction's f chest");
            event.setCancelled(true);
            return;
        }
        UpdateFChest(player);
    }

    private void UpdateFChest(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null && player.getOpenInventory().getTopInventory().getName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Faction Chest")) {
                    FChests.put(FPlayers.getInstance().getByPlayer(player).getFactionId(), player.getOpenInventory().getTopInventory());
                    SaveFChests();
                    UpdateFChestsForPlayersWithFChestOpen(player);
                }
            }
        }.runTaskLaterAsynchronously(mainClass.getPlugin(), 1);
    }

    HashMap<String, Inventory> FChests = new HashMap<>();

    private void UpdateFChestsForPlayersWithFChestOpen(Player PlayerWhoChangedFChest) {
        Faction PlayerWhoChangedFChestFaction = FPlayers.getInstance().getByPlayer(PlayerWhoChangedFChest).getFaction();
        String FactionId = PlayerWhoChangedFChestFaction.getId();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null && player.getOpenInventory().getTopInventory().getName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Faction Chest") && !player.equals(PlayerWhoChangedFChest) && PlayerWhoChangedFChestFaction.equals(FPlayers.getInstance().getByPlayer(player).getFaction())) {
                player.getOpenInventory().getTopInventory().setContents(FChests.get(FactionId).getContents());
            }
        }
    }

    private void SaveFChests() {
        List<String> factions = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<String, Inventory> element : FChests.entrySet()) {
            String FactionId = element.getKey();
            for (ItemStack itemStack : element.getValue()) {
                factions.add(FactionId);
                items.add(itemStack);
            }
        }
        mainClass.getConfig().set("FChestFactions", factions);
        mainClass.getConfig().set("FChestItems", items);
        mainClass.saveConfig();
    }

    public void LoadFChests() {
        List<String> factions = mainClass.getConfig().getStringList("FChestFactions");
        List<ItemStack> items = (List<ItemStack>) mainClass.getConfig().get("FChestItems");
        for (int i = 0; i < factions.size(); i++) {
            String FactionId = factions.get(i);
            ItemStack itemStack = items.get(i);
            Inventory inventory = FChests.getOrDefault(FactionId, Bukkit.createInventory(null, 36, ChatColor.GREEN + "" + ChatColor.BOLD + "Faction Chest"));
            if (itemStack == null) {
                itemStack = new ItemStack(Material.AIR);
            }
            inventory.setItem(i - ((i / 36) * 36), itemStack);
            FChests.put(FactionId, inventory);
        }
    }
}
