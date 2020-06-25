package SandBots;

import CustomItems.CustomItemsMain;
import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_8_R3.CommandExecute;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SandBotsMain extends CommandExecute implements Listener, CommandExecutor {           //Uses BukkitAPI, MilkBowlAPI, MassivecraftAPI, Uses SpiggotAPI
    UtilityMain utilityMain = new UtilityMain();
    CustomItemsMain customItemsMain = new CustomItemsMain();
    MainClass mainClass;
    public SandBotsMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {                 //Registers the command a player types in and tells the program what to do based off of the command the player typed
        if (args.length == 3 && (args[0].equalsIgnoreCase("give") && sender.isOp())) {
            if (Bukkit.getPlayerExact(args[1]) != null && utilityMain.isNumber(args[2])) {
                Player TargetPlayer = Bukkit.getPlayerExact(args[1]);
                if (!TargetPlayer.isOnline()) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not online");
                }
                Integer amount = Integer.parseInt(args[2]);
                utilityMain.GivePlayerItem(TargetPlayer, customItemsMain.SandBot(), amount);
                return true;
            }
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            RemoveSandBots((Player) sender, false);
            return true;
        }
        sender.sendMessage(ChatColor.GRAY + "Usage: " + "\n" + "/sandbot give <Player Name> <Amount>");
        return true;
    }

    @EventHandler
    private void PlayerInteract(PlayerInteractEvent event) {            //Tests if a player right clicks the ground with a sand bot egg. If so, it calls the PlaceSandBot method
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (player.getItemInHand() != null) {
                ItemStack item = new ItemStack(player.getItemInHand());
                item.setAmount(1);
                if (item.equals(customItemsMain.SandBot())) {
                    PlaceSandBot(player, event.getClickedBlock().getLocation().add(.5,1,.5));
                }
            }
        }
    }

    @EventHandler
    private void PlayerInteractWithEntity(PlayerInteractEntityEvent event) {            //Tests if a player right clicks an entity. If they right clicked the sand bot entity, it calls the LoadGui method
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (!(entity instanceof Villager)) {
            return;
        }
        if (entity.getCustomName() == null) {
            return;
        }
        if (!entity.getCustomName().contains(ChatColor.YELLOW + " Sand Bot")) {
            return;
        }
        event.setCancelled(true);
        LoadGui(player, entity.getCustomName());
    }

    @EventHandler
    private void InventoryClick(InventoryClickEvent event) {            //Tests which item a player clicks in the sand bot gui, and calls the appropriate method
        if (event.getSlot() == -999) {
            return;
        }
        if (!event.getClickedInventory().getName().contains(ChatColor.YELLOW + " Sand Bot")) {
            return;
        }
        event.setCancelled(true);
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) {
            return;
        }
        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (type.equals(Material.GOLD_INGOT)) {
            AddMoneyToBank(player, inventory);
        } else if (type.equals(Material.STAINED_CLAY)) {
            ChangeStatus(player, inventory);
        } else if (type.equals(Material.BARRIER)) {
            RemoveSandBots(player, true);
            player.closeInventory();
        }
    }

    @EventHandler
    private void EntityDeath(EntityDeathEvent event) {          //If the player kills a sandbot, it removes all data regarding the killed sandbot
        Entity entity = event.getEntity();
        if (!(entity instanceof Villager)) {
            return;
        }
        if (entity.getCustomName() == null) {
            return;
        }
        if (!entity.getCustomName().contains(ChatColor.YELLOW + " Sand Bot")) {
            return;
        }
        RemoveSpawningBlocksData(entity.getCustomName());
        RemoveSandBotFromData(entity.getCustomName());
        SaveSandBotsInConfig();
    }

    private void ChangeStatus(Player player, Inventory inv) {           //If a player enables or disables the sandbot, it calls the ChangeStatusInDataMethod method and either registers the sandbot as disabled or enabled
        ItemStack clay = inv.getItem(13);
        String SandBotName = inv.getName();
        if (clay.equals(GreenClay)) {
            inv.setItem(13, RedClay);
            RemoveSpawningBlocksData(SandBotName);
            player.sendMessage(ChatColor.RED + "Sand bot " + ChatColor.DARK_RED + "disabled");
            ChangeStatusInData(false, SandBotName);
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            return;
        }
        inv.setItem(13, GreenClay);
        AddBlocksToSpawningBlocks(Objects.requireNonNull(getSandBot(SandBotName)));
        ChangeStatusInData(true, SandBotName);
        player.sendMessage(ChatColor.GREEN + "Sand bot " + ChatColor.DARK_GREEN + "enabled");
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
    }

    private void ChangeStatusInData(Boolean status, String name) {          //Enabled or disabled the sandbot in the sandbot data
        for (String string : SandBots) {
            String[] SplitString = string.split(":");
            String Name = SplitString[0];
            String balance = SplitString[1];
            if (Name.equals(name)) {
                SandBots.add(name + ":" + balance + ":" + status);
                SandBots.remove(string);
                break;
            }
        }
        SaveSandBotsInConfig();
    }

    private void RemoveSandBots(Player player, Boolean GiveSandBotBack) {           //Removes the sandbot from the game and calls the method to remove it from the data as well
        String FactionName = FPlayers.getInstance().getByPlayer(player).getFaction().getTag();
        String SandBotName = ChatColor.GREEN + FactionName + ChatColor.YELLOW + " Sand Bot";
        Economy economy  = MainClass.getEconomy();
        if (hasSandBot(SandBotName)) {
            Entity SandBot = getSandBot(SandBotName);
            int BalanceToAdd = (getSandBotBalance(SandBotName) / 2);
            economy.depositPlayer(player, BalanceToAdd);
            player.sendMessage(ChatColor.GREEN + "You have received " + ChatColor.DARK_GREEN + "$" + utilityMain.FormatPrice(BalanceToAdd) + ChatColor.GREEN + " from your faction");
            assert SandBot != null;
            SandBot.remove();
            RemoveSandBotFromData(SandBotName);
            SaveSandBotsInConfig();
            RemoveSpawningBlocksData(SandBotName);
            if (GiveSandBotBack) {
                utilityMain.GivePlayerItem(player, customItemsMain.SandBot(), 1);
            }
            utilityMain.MessageFactionMembers(Factions.getInstance().getByTag(FactionName), ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " removed your faction's sand bot");
            return;
        }
        player.sendMessage(ChatColor.RED + "Your faction does not have a Sand Bot");
    }

    private void RemoveSandBotFromData(String name) {           //Removes the sandbot from the data
        List<String> StringsToRemove = new ArrayList<>();
        for (String string : SandBots) {
            String Name = string.split(":")[0];
            if (Name.equals(name)) {
                StringsToRemove.add(string);
            }
        }
        for (String string : StringsToRemove) {
            SandBots.remove(string);
        }
        SaveSandBotsInConfig();
    }

    private void AddMoneyToBank(Player player, Inventory inventory) {           //Adds money to the sandbot's balance
        Economy economy  = MainClass.getEconomy();
        if (economy.getBalance(player) < 100000) {
            player.sendMessage(ChatColor.RED + "You cannot afford to do this");
            return;
        }
        economy.withdrawPlayer(player, 100000);
        for (String string : SandBots) {
            String[] SplitString = string.split(":");
            int MoneyInBank = Integer.parseInt(SplitString[1]);
            boolean Enabled = Boolean.parseBoolean(SplitString[2]);
            inventory.setItem(10, utilityMain.createGuiItem(Material.GOLD_INGOT, ChatColor.GOLD + "$" + utilityMain.FormatPrice(MoneyInBank + 100000), ChatColor.YELLOW + "Click to add $100,000"));
            if (SplitString[0].equals(inventory.getName())) {
                SandBots.add(inventory.getName() + ":" + (MoneyInBank + 100000) + ":" + Enabled);
                SandBots.remove(string);
                SaveSandBotsInConfig();
                break;
            }
        }
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
        player.sendMessage(ChatColor.GREEN + "Added " + ChatColor.DARK_GREEN + "$100,000" + ChatColor.GREEN + " to your sand bot's bank");
    }

    List<String> SandBots = new ArrayList<>();

    private Boolean isEnabled(String name) {            //Returns the state of the sandbot (Enabled or Disabled)
        for (String string : SandBots) {
            String[] SplitString = string.split(":");
            String Name = SplitString[0];
            if (name.equals(Name)) {
                return Boolean.valueOf(SplitString[2]);
            }
        }
        return false;
    }

    private void SaveSandBotsInConfig() {           //Saves the data in a file
        mainClass.getConfig().set("SandBots", SandBots);
        mainClass.saveConfig();
    }

    public void LoadSandBotsFromConfig() {          //Loads the sandbot data from the file upon the program's startup
        List<String> NewSandBots = new ArrayList<>();
        for (String string : mainClass.getConfig().getStringList("SandBots")) {
            String[] SplitString = string.split(":");
            String name = SplitString[0];
            String balance = SplitString[1];
            NewSandBots.add(name + ":" + balance + ":" + false);
        }
        SandBots = NewSandBots;
    }

    private boolean hasSandBot(String name) {           //returns whether a faction has a sandbot currently (True or False)
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getCustomName() != null) {
                    if (entity.getCustomName().equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Entity getSandBot(String name) {            //Gets the sandbot in the world based off of a name
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getCustomName() != null) {
                    if (entity.getCustomName().equals(name)) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }

    private void LoadGui(Player player, String name) {          //Loads the sandbot gui
        final Inventory inv;
        inv = Bukkit.createInventory(player, 27, name);
        utilityMain.FillCyprusGuiSize27(inv);
        AddGuiItems(inv);
        player.openInventory(inv);
    }

    private Integer getSandBotBalance(String name) {            //Returns a sandbot's balance
        for (String string : SandBots) {
            String[] SplitString = string.split(":");
            String Name = SplitString[0];
            Integer balance = Integer.parseInt(SplitString[1]);
            if (Name.equals(name)) {
                return balance;
            }
        }
        return 0;
    }

    ItemStack GreenClay = utilityMain.createGuiItemFromItemStack(new ItemStack(Material.STAINED_CLAY, 1, (byte) 5), ChatColor.GREEN + "Enable", ChatColor.YELLOW + "Click to disable");
    ItemStack RedClay = utilityMain.createGuiItemFromItemStack(new ItemStack(Material.STAINED_CLAY, 1, (byte) 14), ChatColor.RED + "Disabled", ChatColor.YELLOW + "Click to enable");
    private void AddGuiItems(Inventory inventory) {         //Adds items to the sandbot gui
        inventory.setItem(10, utilityMain.createGuiItem(Material.GOLD_INGOT, ChatColor.GOLD + "$" + utilityMain.FormatPrice(getSandBotBalance(inventory.getName())), ChatColor.YELLOW + "Click to add $100,000"));
        Boolean Enabled = isEnabled(inventory.getName());
        if (Enabled) {
            inventory.setItem(13, GreenClay);
        } else {
            inventory.setItem(13, RedClay);
        }
        inventory.setItem(16, utilityMain.createGuiItem(Material.BARRIER, ChatColor.RED + "Pickup", ChatColor.YELLOW + "You will only receive half the money in the bank"));
    }

    private void PlaceSandBot(Player player, Location location) {           //Places a sandbot and adds all necessary data regarding the new placed sandbot
        if (!utilityMain.isInFaction(player)) {
            player.sendMessage(ChatColor.RED + "You must in a faction to use sand bots");
            return;
        }
        String FactionName = FPlayers.getInstance().getByPlayer(player).getFaction().getTag();
        String name = ChatColor.GREEN + FactionName + ChatColor.YELLOW + " Sand Bot";
        if (hasSandBot(name)) {
            player.sendMessage(ChatColor.RED + "Your faction can only have one sand bot at a time" + "\n" + "Do /sandbot remove to remove all your faction's sandbot");
            return;
        }
        ItemStack itemStack = player.getItemInHand();
        itemStack.setAmount(itemStack.getAmount() - 1);
        if (itemStack.getAmount() <= 0) {
            itemStack = null;
        }
        player.setItemInHand(itemStack);
        Entity SandBot = player.getWorld().spawnEntity(location, EntityType.VILLAGER);
        SandBot.setCustomName(name);
        SandBot.setCustomNameVisible(true);
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) SandBot).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        nmsEntity.c(tag);
        tag.setBoolean("NoAI", true);
        tag.setBoolean("Silent", true);
        nmsEntity.f(tag);
        SandBots.add(name + ":" + "0" + ":" + "false");
        SaveSandBotsInConfig();
    }

    List<String> SpawningBlocks = new ArrayList<>();

    private void RemoveSpawningBlocksData(String name) {            //Removes the spawning block data
        List<String> StringsToRemove = new ArrayList<>();
        for (String string : SpawningBlocks) {
            String Name = string.split(":")[0];
            if (Name.equals(name)) {
                StringsToRemove.add(string);
            }
        }
        for (String string : StringsToRemove) {
            SpawningBlocks.remove(string);
        }
    }

    private void RemoveBalance(String name) {           //Removes the sandbot balance from data
        for (String string : SandBots) {
            String[] SplitString = string.split(":");
            String Name = SplitString[0];
            int balance = Integer.parseInt(SplitString[1]);
            String state = SplitString[2];
            if (Name.equals(name)) {
                SandBots.add(Name + ":" + (balance - 1) + ":" + state);
                SandBots.remove(string);
                SaveSandBotsInConfig();
                return;
            }
        }
    }

    public void SpawnSand() {           //Spawns the right block beneath each Block in the SpawningBlock list every 0.5 seconds starting 3 seconds after the program starts
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String string : SpawningBlocks) {
                    String[] SplitString = string.split(":");
                    String SandBotName = SplitString[0];
                    Integer balance = getSandBotBalance(SandBotName);
                    if (balance > 0) {
                        Location location = new Location(Bukkit.getWorld(SplitString[1]), Integer.parseInt(SplitString[2]), Integer.parseInt(SplitString[3]), Integer.parseInt(SplitString[4]));
                        Block block = location.getBlock();
                        Location TestForAir = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ());
                        if (TestForAir.getBlock().getType().equals(Material.AIR)) {
                            RemoveBalance(SandBotName);
                            byte data = block.getData();
                            switch (data) {
                                case 2:
                                    location.subtract(0,1,0).getBlock().setType(Material.SAND);
                                    location.getBlock().setData((byte) 1);
                                    break;
                                case 4:
                                    location.subtract(0,1,0).getBlock().setType(Material.SAND);
                                    break;
                                case 6:
                                    location.subtract(0,1,0).getBlock().setType(Material.GRAVEL);
                                    break;
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 60, 10);
    }

    private void AddBlocksToSpawningBlocks(Entity SandBot) {            //Gets the wanted blocks around the sandbot, turns those blocks into a string which allows us to easily use the block's data later on, and then adds those strings to the SpawningBlocks list
        Location location = SandBot.getLocation();
        Integer XLoc = (int) Math.floor(location.getX());
        Integer YLoc = (int) Math.floor(location.getY());
        Integer ZLoc = (int) Math.floor(location.getZ());
        Integer HorizontalRadius = 25;
        Integer VerticalRadius = 5;
        for (int x = XLoc - HorizontalRadius; x < XLoc + HorizontalRadius; x++) {
            for (int y = YLoc - VerticalRadius; y < YLoc + VerticalRadius; y++) {
                for (int z = ZLoc - HorizontalRadius; z < ZLoc + HorizontalRadius; z++) {
                    Block block = new Location(SandBot.getWorld(), x, y, z).getBlock();
                    if (block.getType().equals(Material.STONE) && (block.getData() == 2 || block.getData() == 4 || block.getData() == 6)) {
                        SpawningBlocks.add(SandBot.getName() + ":" + location.getWorld().getName() + ":" + x + ":" + y + ":" + z);
                    }
                }
            }
        }
    }
}
