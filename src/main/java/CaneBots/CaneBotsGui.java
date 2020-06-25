package CaneBots;

import CustomItems.CustomItemsMain;
import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_8_R3.CommandExecute;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CaneBotsGui extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    public CaneBotsGui(MainClass mc) {
        mainClass = mc;
    }
    CustomItemsMain customItemsMain = new CustomItemsMain();
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.isOp()) {
            return false;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            if (Bukkit.getPlayerExact(args[1]) != null && Bukkit.getPlayerExact(args[1]).isOnline() && utilityMain.isNumber(args[2])) {
                Player TargetPlayer = Bukkit.getPlayerExact(args[1]);
                utilityMain.GivePlayerItem(TargetPlayer, customItemsMain.CaneBot(), Integer.parseInt(args[2]));
                return true;
            }
        }
        HelpMessage(sender);
        return true;
    }

    private void HelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Usage: /canebot give <Player Name> amount");
    }

    @EventHandler
    private void PlacesCaneBot(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (player.getItemInHand() != null) {
                ItemStack item = new ItemStack(player.getItemInHand());
                item.setAmount(1);
                if (item.equals(customItemsMain.CaneBot())) {
                    PlaceCaneBot(player, event.getClickedBlock().getLocation().add(.5,1,.5));
                }
            }
        }
    }

    @EventHandler
    private void RemoveCaneBotFromHashMaps(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Villager)) {
            return;
        }
        String id = event.getEntity().getUniqueId().toString();
        for (int i = 0; i < CaneBots.size(); i++) {
            String string = CaneBots.get(i);
            String Id = string.split(":")[0];
            if (Id.equals(id)) {
                CaneBots.remove(i);
                HarvesterHoes.remove(i);
                SaveCaneBotsInConfig();
                return;
            }
        }
    }

    @EventHandler
    private void RightClickedCaneBot(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (!(entity instanceof Villager)) {
            return;
        }
        if (isCaneBot(entity)) {
            event.setCancelled(true);
            ClickedCaneBots.put(player, entity);
            LoadGui(player);
        }
    }

    @EventHandler
    private void RemovePlayerFromClickedCanBots(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Cane Bot")) {
            ClickedCaneBots.remove(player);
        }
    }

    @EventHandler
    private void ClickCaneBot(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        ItemStack CurrentItem = event.getCurrentItem();
        Material type = CurrentItem.getType();
        Player player = (Player) event.getWhoClicked();
        if (type.equals(Material.STAINED_GLASS_PANE)) {
            return;
        }
        if (type.equals(Material.GOLD_INGOT)) {
            GiveCaneBotBalance(player);
        }
    }

    private void GiveCaneBotBalance(Player player) {
        Entity CaneBot = ClickedCaneBots.get(player);
        String id = CaneBot.getUniqueId().toString();
        for (String string : CaneBots) {
            String[] SplitString = string.split(":");
            String Id = SplitString[0];
            if (Id.equals(id)) {
                int balance = Integer.parseInt(SplitString[1]);
                Economy economy  = MainClass.getEconomy();
                economy.depositPlayer(player, balance);
                CaneBots.remove(string);
                CaneBots.add(Id + ":" + 0 + ":" + SplitString[2] + ":" + SplitString[3]);
                SaveCaneBotsInConfig();
                player.closeInventory();
                LoadGui(player);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                player.sendMessage(ChatColor.GREEN + "Collected " + ChatColor.DARK_GREEN + "$" + utilityMain.FormatPrice(balance));
                return;
            }
        }
    }

    HashMap<Player, Entity> ClickedCaneBots = new HashMap<>();

    private void LoadGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 45, ChatColor.GREEN + "" + ChatColor.BOLD + "Cane Bot");
        FillGui(inv);
        FillCustomItemsInGui(inv, player);
        player.openInventory(inv);
    }

    private void FillCustomItemsInGui(Inventory inv, Player player) {
        Entity CaneBot = ClickedCaneBots.get(player);
        inv.setItem(36, utilityMain.createGuiItem(Material.GOLD_INGOT, ChatColor.GOLD + "$" + utilityMain.FormatPrice(getMoney(CaneBot)), ChatColor.YELLOW + "Click to collect"));
        Integer RaiusLevel = getRadiusLevel(CaneBot);
        inv.setItem(20, utilityMain.createGuiItem(Material.ENDER_PORTAL_FRAME, ChatColor.RED + "" + ChatColor.BOLD + "Upgrade Radius " + ChatColor.GRAY + "(" + ChatColor.RED + "" + ChatColor.BOLD + RaiusLevel + ChatColor.GRAY + ")", ChatColor.GRAY + "" + ChatColor.ITALIC + "Upgrade the radius in which", ChatColor.GRAY + "" + ChatColor.ITALIC + "your cane bot harvests crops", ChatColor.COLOR_CHAR + " ", ChatColor.GOLD + "" + ChatColor.BOLD + "Next Upgrade: " + ChatColor.UNDERLINE + "$" + utilityMain.FormatPrice(getRadiusPrice(RaiusLevel + 1)), ChatColor.RED + getRadius(RaiusLevel).toString() + "x" + getRadius(RaiusLevel) + " ➵ " + ChatColor.RED + getRadius(RaiusLevel + 1) + "x" + getRadius(RaiusLevel + 1)));
        ItemStack HarvesterHoe = getHarvesterHoe(CaneBot);
        if (HarvesterHoe == null) {
            inv.setItem(22, utilityMain.createGuiItemFromItemStack(new ItemStack(Material.INK_SACK, 1, DyeColor.SILVER.getData()), ChatColor.RED + "Harvester Hoe", ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to add your harvester hoe"));
        } else {
            inv.setItem(22, HarvesterHoe);
        }
        Integer SpeedLevel = getSpeedLevel(CaneBot);
        inv.setItem(24, utilityMain.createGuiItem(getBootsFromSpeedLevel(SpeedLevel),ChatColor.RED + "" + ChatColor.BOLD + "Upgrade Speed " + ChatColor.GRAY + "(" + ChatColor.RED + "" + ChatColor.BOLD + SpeedLevel + ChatColor.GRAY + ")", ChatColor.GRAY + "" + ChatColor.ITALIC + "Upgrade the speed in which", ChatColor.GRAY + "" + ChatColor.ITALIC + "your cane bot moves", ChatColor.COLOR_CHAR + " ", ChatColor.GOLD + "" + ChatColor.BOLD + "Next Upgrade: " + ChatColor.UNDERLINE + "$" + utilityMain.FormatPrice(getSpeedPrice(SpeedLevel + 1)), ChatColor.RED + "+" + getSpeed(SpeedLevel).toString() + "% ➵ +" + ChatColor.RED + getSpeed(SpeedLevel + 1) + "%"));
        inv.setItem(44, utilityMain.createGuiItem(Material.BARRIER, ChatColor.RED + "Pickup"));
    }

    private Material getBootsFromSpeedLevel(Integer level) {
        switch (level) {
            case 2:
                return Material.CHAINMAIL_BOOTS;
            case 3:
                return Material.IRON_BOOTS;
            case 4:
                return Material.GOLD_BOOTS;
            case 5:
                return Material.DIAMOND_BOOTS;
            default:
                return Material.LEATHER_BOOTS;
        }
    }

    private Integer getSpeed(Integer level) {
        switch (level) {
            case 2:
                return 15;
            case 3:
                return 35;
            case 4:
                return 60;
            case 5:
                return 100;
            default:
                return 0;
        }
    }

    private Integer getSpeedPrice(Integer level) {
        switch (level) {
            case 2:
                return 1000000;
            case 3:
                return 2000000;
            case 4:
                return 5000000;
            case 5:
                return 10000000;
        }
        return 0;
    }

    private Integer getSpeedLevel(Entity entity) {
        String id = entity.getUniqueId().toString();
        for (String string : CaneBots) {
            String Id = string.split(":")[0];
            if (id.equals(Id)) {
                return Integer.parseInt(string.split(":")[2]);
            }
        }
        return 0;
    }

    private ItemStack getHarvesterHoe(Entity entity) {
        String id = entity.getUniqueId().toString();
        for (int i = 0; i < CaneBots.size(); i++) {
            String Id = CaneBots.get(i).split(":")[0];
            if (Id.equals(id)) {
                return HarvesterHoes.get(i);
            }
        }
        return null;
    }

    private Integer getRadius(Integer level) {
        switch (level) {
            case 2:
                return 3;
            case 3:
                return 5;
            default:
                return 1;
        }
    }

    private Integer getRadiusPrice(Integer level) {
        if (level == 2) {
            return 10000000;
        }
        return 25000000;
    }

    private Integer getRadiusLevel(Entity entity) {
        String id = entity.getUniqueId().toString();
        for (String string : CaneBots) {
            String Id = string.split(":")[0];
            if (id.equals(Id)) {
                return Integer.parseInt(string.split(":")[3]);
            }
        }
        return 0;
    }

    private Integer getMoney(Entity entity) {
        String id = entity.getUniqueId().toString();
        for (String string : CaneBots) {
            String Id = string.split(":")[0];
            if (id.equals(Id)) {
                return Integer.parseInt(string.split(":")[1]);
            }
        }
        return 0;
    }

    private void FillGui(Inventory inv) {
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack Red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Black);
        }
        for (int i = 9; i < 37; i+=9) {
            inv.setItem(i, Red);
        }
        for (int i = 17; i < 45; i+=9) {
            inv.setItem(i, Red);
        }
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, Red);
        }
        for (int i = 36; i < 45; i++) {
            inv.setItem(i, Red);
        }
        inv.setItem(13, Red);
        inv.setItem(21, Red);
        inv.setItem(23, Red);
        inv.setItem(31, Red);
    }

    private boolean isCaneBot(Entity entity) {
        String id = entity.getUniqueId().toString();
        for (String string : CaneBots) {
            String Id = string.split(":")[0];
            if (id.equals(Id)) {
                return true;
            }
        }
        return false;
    }

    List<String> CaneBots = new ArrayList<>(); // UniqueId:money:Speed:Radius
    List<ItemStack> HarvesterHoes = new ArrayList<>();
    private void PlaceCaneBot(Player player, Location location) {
        ItemStack itemStack = player.getItemInHand();
        itemStack.setAmount(itemStack.getAmount() - 1);
        if (itemStack.getAmount() <= 0) {
            itemStack = null;
        }
        player.setItemInHand(itemStack);
        Entity SandBot = player.getWorld().spawnEntity(location, EntityType.VILLAGER);
        SandBot.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Cane Bot");
        SandBot.setCustomNameVisible(true);
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) SandBot).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        nmsEntity.c(tag);
        tag.setBoolean("NoAI", true);
        tag.setBoolean("Silent", true);
        nmsEntity.f(tag);
        CaneBots.add(SandBot.getUniqueId() + ":" + "0" + ":" + "1" + ":" + "1");
        HarvesterHoes.add(null);
        SaveCaneBotsInConfig();
    }

    private void SaveCaneBotsInConfig() {
        mainClass.getConfig().set("CaneBots", CaneBots);
        mainClass.getConfig().set("HarvesterHoes", HarvesterHoes);
        mainClass.saveConfig();
    }

    public void LoadCaneBotsFromConfig() {
        CaneBots = mainClass.getConfig().getStringList("CaneBots");
        HarvesterHoes = (List<ItemStack>) mainClass.getConfig().get("HarvesterHoes");
        if (HarvesterHoes.isEmpty()) {
            HarvesterHoes = new ArrayList<>();
        }
    }

}
