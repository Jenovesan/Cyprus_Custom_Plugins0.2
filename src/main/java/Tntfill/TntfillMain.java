package Tntfill;

import FactionUpgrades.FactionUpgradesFTtntUpgrade;
import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayers;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TntfillMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    FactionUpgradesFTtntUpgrade factionUpgradesFTtntUpgrade;
    public TntfillMain(MainClass mc) {
        mainClass = mc;
        factionUpgradesFTtntUpgrade = new FactionUpgradesFTtntUpgrade(mainClass);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(utilityMain.isInFaction((Player) sender))) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "You must be in a faction to execute this command");
        }
        LoadGui((Player) sender);
        return false;
    }

    private void LoadGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 27, ChatColor.RED + "" + ChatColor.BOLD + "Tntfill");
        utilityMain.FillCyprusGuiSize27(inv);
        FillGui(inv);
        player.openInventory(inv);
    }

    private void FillGui(Inventory inv) {
        inv.setItem(10, AmountItem(64));
        inv.setItem(12, RadiusItem(50));
        inv.setItem(14, AddOrReplaceItem("Add"));
        inv.setItem(16, utilityMain.createGuiItem(Material.STONE_BUTTON, ChatColor.RED + "Click to fill"));
    }

    String title = ChatColor.RED + "";
    String desc = ChatColor.YELLOW + "" + ChatColor.ITALIC;
    private ItemStack AmountItem(Integer amount) {
        ItemStack itemStack = utilityMain.createGuiItem(Material.TNT, title + "Fill Amount", desc + "Click to change the fill amount");
        itemStack.setAmount(amount);
        return itemStack;
    }
    private ItemStack RadiusItem(Integer amount) {
        ItemStack itemStack = utilityMain.createGuiItem(Material.PAPER, title + "Radius", desc + "Left click to decrease the fill radius", desc + "Right click to increase the radius");
        itemStack.setAmount(amount);
        return itemStack;
    }
    private ItemStack AddOrReplaceItem(String option) {
        if (option.equalsIgnoreCase("Add")) {
            return utilityMain.createGuiItem(Material.REDSTONE_TORCH_ON, title + "Add", desc + "Click to change between add or replace tnt");
        }
        else {
            return utilityMain.createGuiItem(Material.TORCH, title + "Replace", desc + "Click to change between add or replace tnt");
        }
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
        ItemStack item = event.getCurrentItem();
        Material type = item.getType();
        if (!(event.getInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Tntfill"))) {
            return;
        }
        event.setCancelled(true);
        if (type.equals(Material.STAINED_GLASS_PANE)) {
            return;
        }
        if (type.equals(Material.TNT)) {
            ChangeTntFillAmount(item);
        }
        else if (type.equals(Material.PAPER)) {
            ChangeRadius(item, event.getClick());
        }
        else if (type.equals(Material.REDSTONE_TORCH_ON) || type.equals(Material.TORCH)) {
            ChangeFillType(item);
        }
        else if (type.equals(Material.STONE_BUTTON)) {
            Tntfill(player, event.getInventory());
            player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            return;
        }
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
    }

    private void ChangeTntFillAmount(ItemStack itemStack) {
        if (itemStack.getAmount() == 64) {
            itemStack.setAmount(1);
        }
        else {
            itemStack.setAmount(64);
        }
    }

    private void ChangeRadius(ItemStack itemStack, ClickType clickType) {
        int size = itemStack.getAmount();
        if (clickType.equals(ClickType.LEFT)) {
            if (size < 10) {
                return;
            }
            itemStack.setAmount(size - 5);
        }
        else if (clickType.equals(ClickType.RIGHT)){
            if (size > 45) {
                return;
            }
            itemStack.setAmount(size + 5);
        }
    }

    private void ChangeFillType(ItemStack itemStack) {
        String NewName;
        if (itemStack.getType().equals(Material.REDSTONE_TORCH_ON)) {
            itemStack.setType(Material.TORCH);
            NewName = "Replace";
        }
        else {
            itemStack.setType(Material.REDSTONE_TORCH_ON);
            NewName = "Add";
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + NewName);
        itemStack.setItemMeta(meta);
    }

    private void Tntfill(Player player, Inventory inventory) {
        int amount = inventory.getItem(10).getAmount();
        int radius = inventory.getItem(12).getAmount();
        String mode = "Add";
        if (inventory.getItem(14).getType().equals(Material.TORCH)) {
            mode = "Replace";
        }
        List<Block> Dispensers = new ArrayList<>();
        for (int x = -radius; x < (radius + 1); x++) {
            for (int y = -radius; y < (radius + 1); y++) {
                for (int z = -radius; z < (radius + 1); z++) {
                    Location location = player.getLocation().add(x,y,z);
                    if (location.getBlock().getType().equals(Material.DISPENSER)) {
                        Dispensers.add(location.getBlock());
                    }
                }
            }
        }
        int TotalTnt = Dispensers.size() * amount;
        if (TotalTnt > factionUpgradesFTtntUpgrade.getTntAmount(FPlayers.getInstance().getByPlayer(player).getFactionId())) {
            player.sendMessage(ChatColor.RED + "Your faction storage does not contain enough tnt");
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        if (mode.equals("Add")) {
            for (Block block : Dispensers) {
                InventoryHolder dispenser = (InventoryHolder) block.getState();
                dispenser.getInventory().addItem(new ItemStack(Material.TNT, amount));
            }
        }
        else {
            for (Block block : Dispensers) {
                InventoryHolder dispenser = (InventoryHolder) block.getState();
                dispenser.getInventory().setItem(0, new ItemStack(Material.TNT, amount));
                for (int i = 1; i < 9; i++) {
                    dispenser.getInventory().setItem(i, null);
                }
            }
        }
        factionUpgradesFTtntUpgrade.ChangeTntAmount(FPlayers.getInstance().getByPlayer(player).getFaction(), -TotalTnt);
        player.sendMessage(ChatColor.GREEN + "Filled " + ChatColor.DARK_GREEN + Dispensers.size() + ChatColor.GREEN + " dispensers for a total of " + ChatColor.DARK_GREEN + TotalTnt + ChatColor.GREEN + " tnt");
    }
}
