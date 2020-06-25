package Key;

import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KeyMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public KeyMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0 && sender.hasPermission("cyprus.key")) {
            LoadGui((Player) sender);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reset") && sender.isOp()) {
            UsedKey.clear();
            SaveUsedKeyInConfig();
            sender.sendMessage(ChatColor.GREEN + "Reclaims have been reset");
        }
        return false;
    }

    @EventHandler
    private void GiveKey(InventoryClickEvent event) {
        if (event.getSlot() == -999) {
            return;
        }
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Material type = item.getType();
        if (!inventory.getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Reclaim")) {
            return;
        }
        if (type.equals(Material.TRIPWIRE_HOOK) && !UsedKey.contains(player.getUniqueId())) {
            GiveKey(player);
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
        }
        event.setCancelled(true);
    }

    Set<UUID> UsedKey = new HashSet<>();
    private void GiveKey(Player player) {
        mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "cc give physical mithril 1 " + player.getName());
        UsedKey.add(player.getUniqueId());
        SaveUsedKeyInConfig();
        LoadGui(player);
    }

    private void LoadGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 27, ChatColor.RED + "" + ChatColor.BOLD + "Reclaim");
        utilityMain.FillCyprusGuiSize27(inv);
        inv.setItem(13, utilityMain.createGuiItem(Material.TRIPWIRE_HOOK, ChatColor.AQUA + "" + ChatColor.BOLD + "Mithril Key", getLoreString(player)));
        player.openInventory(inv);
    }

    private String getLoreString(Player player) {
        if (!player.hasPermission("cyprus.key")) {
            return ChatColor.RED + "You do not have permission";
        } else {
            if (UsedKey.contains(player.getUniqueId()))  {
                return ChatColor.GRAY + "" + ChatColor.ITALIC + "You have already redeemed your Mithril Key";
            }
            return ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to receive the Mithril Key";
        }
    }


    private void SaveUsedKeyInConfig() {
        List<String> uuids = new ArrayList<>();
        for (UUID uuid : UsedKey) {
            uuids.add(uuid.toString());
        }
        mainClass.getConfig().set("UsedKey", uuids);
        mainClass.saveConfig();
    }

    public void LoadUsedKeyFromConfig() {
        List<String> uuids = mainClass.getConfig().getStringList("UsedKey");
        for (String string : uuids) {
            UsedKey.add(UUID.fromString(string));
        }
    }
}
