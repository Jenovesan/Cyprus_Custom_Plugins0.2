package CustomEnchants;

import Utility.UtilityMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.List;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.avaje.ebean.validation.NotNull;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;

public class CustomEnchantsCommands extends CommandExecute implements InventoryHolder, Listener, CommandExecutor {

    UtilityMain utilityMain = new UtilityMain();

    public final int XpNeededForCe = 315;   //should be 4020
    public Material[] ItemsAbleToBeCombined = new Material[] {Material.BOW, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE, Material.DIAMOND_AXE,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_SPADE, Material.IRON_AXE,
            Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.GOLD_SWORD, Material.GOLD_PICKAXE, Material.GOLD_SPADE, Material.GOLD_AXE,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS};
    public int WeaponCeAmount = 3;
    public int ArmorCeAmount = 2;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            LoadGUI((Player) sender);
        }
        else {
            if (sender.isOp()) {
                if (args[0].equals("give") && args.length > 2) {
                    try {
                        Player TargetPlayer = Bukkit.getPlayerExact(args[2]);
                        if (TargetPlayer != null) {
                            if (getCeBook(args[1]) != null) {
                                Bukkit.getPlayerExact(args[2]).getInventory().addItem(getCeBook(args[1]));
                            }
                            else {
                                sender.sendMessage(ChatColor.RED + args[1] + " is not a custom enchant");
                            }
                        }
                        else {
                            if (getCeBook(args[1] + " " + args[2]) != null) {
                                Bukkit.getPlayerExact(args[3]).getInventory().addItem(getCeBook(args[1] + " " + args[2]));
                            }
                            else {
                                sender.sendMessage(ChatColor.RED + args[1] + " " + args[2] + " is not a custom enchant");
                            }
                        }
                    } catch (Exception e) {}
                }
                else {
                    sender.sendMessage(ChatColor.GRAY + "Usage: /ce give <CE Name> <Player Name>");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "You need to be opped to use this command");
            }
        }

        return true;
    }

    @EventHandler
    private void UseRarityCustomEnchantBook(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (itemStack != null && itemStack.getType().equals(Material.BOOK) && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            ItemStack NewStack = new ItemStack(itemStack);
            NewStack.setAmount(itemStack.getAmount() - 1);
            boolean executed = false;
            if (itemStack.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "" + ChatColor.BOLD + "Legendary Book")) {
                GivePlayerCeBook(player, true, "Legendary");
                executed = true;
            } else if (itemStack.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "" + ChatColor.BOLD + "Rare Book")) {
                GivePlayerCeBook(player, true, "Rare");
                executed = true;
            } if (executed) {
                if (NewStack.getAmount() < 1) {
                    player.setItemInHand(null);
                } else {
                    player.setItemInHand(NewStack);
                }
            }
        }
    }



    public void LoadGUI(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(this, 27, ChatColor.RED + "" + ChatColor.BOLD + "Enchanter");
        AddEnchanterItems(inv, player);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getSlot() == -999) { //Do not trigger if player drops item
            return;
        }
        Player p = (Player) e.getWhoClicked();
        ItemStack ClickedItem = e.getCurrentItem();
        ItemStack CursorItem = e.getCursor();
        if (e.getInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Enchanter") || e.getInventory().getName().equals(ChatColor.AQUA + "" + ChatColor.BOLD  + "CE LIST")) {
            e.setCancelled(true);
            final Inventory inv;
            inv = Bukkit.createInventory(this, 54, ChatColor.AQUA + "" + ChatColor.BOLD  + "CE LIST");
            if (ClickedItem.hasItemMeta()) {
                String ItemName = ClickedItem.getItemMeta().getDisplayName();
                if (ItemName.equals(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------") || ItemName.equals(ChatColor.GRAY + "" + ChatColor.BOLD + "Common")) { //Enchanter
                    if (p.getTotalExperience() >= XpNeededForCe) {
                        int xp = p.getTotalExperience();
                        int amount = -XpNeededForCe;
                        p.setTotalExperience(0);
                        p.setLevel(0);
                        p.giveExp(Math.max(xp + amount, 0));
                        GivePlayerCeBook(p, false, null);
                        inv.setItem(10, utilityMain.createGuiItem(Material.EXP_BOTTLE, ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------", ChatColor.YELLOW + "             Levels: " + p.getLevel(), ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------"));
                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 10, 1);
                    }
                    else if (p.getTotalExperience() < XpNeededForCe) {
                        p.sendMessage(ChatColor.RED + "You do not have enough xp! You only have " + (p.getLevel()) + " levels");
                    }
                }
                if (ClickedItem.getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------") || ItemName.equals(ChatColor.GRAY + "" + ChatColor.BOLD + "Common Enchants")) { //Ce List (Commons)
                    p.playSound(p.getLocation(), Sound.STEP_WOOL, 100, 1);
                    AddCeListItems(inv, "Common");
                    p.openInventory(inv);
                }
                else if (ItemName.equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Uncommon Enchants")) { //Uncommon
                    AddCeListItems(inv, "Uncommon");
                    p.openInventory(inv);
                }
                else if (ItemName.equals(ChatColor.BLUE + "" + ChatColor.BOLD + "Rare Enchants")) { //Rare
                    AddCeListItems(inv, "Rare");
                    p.openInventory(inv);
                }
                else if (ItemName.equals(ChatColor.YELLOW + "" + ChatColor.BOLD + "Legendary Enchants")) { //Legendary
                    AddCeListItems(inv, "Legendary");
                    p.openInventory(inv);
                }
                else if (ItemName.equals(ChatColor.RED + "Back")) {
                    LoadGUI(p);
                }
            }
        }
        if (e.getInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Forge")) {
            e.setCancelled(true);
            ItemStack ForgeItem = e.getInventory().getItem(29);
            if (ClickedItem.getType().equals(Material.ENCHANTED_BOOK) && e.getSlot() != 33) {
                for (ItemStack item : p.getInventory().getContents()) {
                    if (item != null) {
                        if (item.equals(ForgeItem)) {
                            ItemMeta itemMeta = item.getItemMeta();
                            List<String> itemLore = itemMeta.getLore();
                            for (int i = 0; i < itemLore.size(); i++) {
                                if (itemLore.get(i).equals(ClickedItem.getItemMeta().getDisplayName())) {
                                    itemLore.set(i, unBoldCeBook(e.getInventory().getItem(33).getItemMeta().getDisplayName()));
                                    itemMeta.setLore(itemLore);
                                    item.setItemMeta(itemMeta);
                                    e.getInventory().setItem(33, null);
                                    p.closeInventory();
                                    for (int a = 0; a < 20; a++) {
                                        p.getWorld().playEffect(p.getEyeLocation(), Effect.FLAME, 5);
                                        p.getWorld().playEffect(p.getLocation(), Effect.FLAME, 5);
                                    }
                                    p.playSound(p.getLocation(), Sound.BLAZE_HIT, 10, 1);
                                }
                            }
                        }
                    }
                }
            }
            else if (ClickedItem.getType().equals(Material.BARRIER)) {
                p.closeInventory();
            }
        }
        if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {//Adding Ce's onto armor/weapons
            if (e.getCursor().hasItemMeta() && e.getCursor().getItemMeta().hasDisplayName() && getCeBook(ChatColor.stripColor(e.getCursor().getItemMeta().getDisplayName())) != null) { //Check if it is actually a ce book
                if (!(e.getSlotType().equals(InventoryType.SlotType.ARMOR))) {
                    for (int i = 0; i < ItemsAbleToBeCombined.length; i++) {
                        if (e.getCurrentItem().getType().equals(ItemsAbleToBeCombined[i])) {
                            ItemStack GearToAdd = e.getCurrentItem();
                            ItemMeta GearToAddMeta = GearToAdd.getItemMeta();
                            List<String> GearToAddLore = new ArrayList<>();
                            if (GearToAddMeta.hasLore()) {
                                GearToAddLore = GearToAdd.getItemMeta().getLore();
                                if (GearToAddLore.contains(e.getCursor().getItemMeta().getDisplayName())) {
                                    p.sendMessage(ChatColor.RED + "You cannot add the same custom enchant twice on the same piece of gear");
                                    return;
                                }
                            }
                            if (CanAddCe(e.getCurrentItem(), e.getCursor(), e.getCursor().getItemMeta().getDisplayName())) {
                                if (GearToAddLore.size() < AmountOfPossibleCes(ItemsAbleToBeCombined[i])) {
                                    GearToAddLore.add(unBoldCeBook(e.getCursor().getItemMeta().getDisplayName()));
                                    GearToAddMeta.setLore(GearToAddLore);
                                    GearToAdd.setItemMeta(GearToAddMeta);
                                    e.getClickedInventory().setItem(e.getSlot(), GearToAdd);
                                    p.setItemOnCursor(null);
                                    e.setCancelled(true);
                                    for (int a = 0; a < 5; a++) {
                                        p.getWorld().playEffect(p.getEyeLocation(), Effect.LAVA_POP, 5);
                                    }
                                    p.playSound(p.getLocation(), Sound.ANVIL_USE, 10, 1);
                                }
                                else {
                                    p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1, 1);
                                    final Inventory inv;
                                    inv = Bukkit.createInventory(this, 45, ChatColor.RED + "" + ChatColor.BOLD + "Forge");
                                    AddForgeItems(inv, p, GearToAdd, CursorItem);
                                    p.setItemOnCursor(null);
                                    e.setCancelled(true);
                                    p.openInventory(inv);
                                }
                            }
                            else {
                                p.sendMessage(ChatColor.RED + "You cannot add this custom enchant to this item");
                            }
                            break;
                        }
                        if (i == (ItemsAbleToBeCombined.length - 1)) {
                            p.sendMessage(ChatColor.RED + "You cannot add custom enchants to this type of item");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void inventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof  Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Forge")) {
            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 1);
            if (event.getInventory().getItem(33) != null) {
                player.getInventory().addItem(event.getInventory().getItem(33));
            }
        }
    }

    private boolean CanAddCe(ItemStack Gear, ItemStack CeBook, String NameOfCe) {
        String GearIsTypeOf = TypeOfGear(Gear.getType());
        if (CeBook.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Bounty Hunter")) {
            return Gear.getType().equals(Material.GOLD_SWORD);
        }
        else return GearIsTypeOf.equals(TypeOfCe(NameOfCe));
    }

    private String TypeOfCe(String NameOfCe) {
        List<String> ArmorCes = new ArrayList<String>(
                Arrays.asList(
                        "Water Breathing", "Resistance", "Crapple", "Patching", "Contaminator", "Riposte", "Saturation", "Purification", "Night Vision", "Strength", "Fire Resistance", "Speed", "Regeneration", "Health Boost", "Reforged", "Second Life", "Last Stand", "Reinforced", "Odds", "Ethereal", "Gapple","Tank","Demise"));
        List<String> WeaponCes = new ArrayList<String>(
                Arrays.asList(
                        "Frostbite", "Looter", "Starvation", "Combo", "Swap","Splitter", "Cripple", "Backstab", "Reaper", "Lifesteal", "Bounty Hunter"));
        List<String> BowCes = new ArrayList<String>(
                Arrays.asList(
                        "Piercing", "Explosive Shot","Toxin", "Longshot"));
        List<String> ToolCes = new ArrayList<String>(
                Arrays.asList(
                        "Haste", "Obsidian Breaker"));
        if (ArmorCes.contains(ChatColor.stripColor(NameOfCe))) {
            return "Armor";
        }
        else if (WeaponCes.contains(ChatColor.stripColor(NameOfCe))) {
            return "Weapons";
        }
        else if (BowCes.contains(ChatColor.stripColor(NameOfCe))) {
            return "Bows";
        }
        else {
            return "Tools";
        }
    }

    private String TypeOfGear(Material Gear) {
        List<Material> Armors = new ArrayList<Material>(
                Arrays.asList(
                        Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
                        Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
                        Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
                        Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
                        Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS));
        List<Material> Weapons = new ArrayList<Material>(
                Arrays.asList(Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.IRON_SWORD, Material.IRON_AXE, Material.GOLD_SWORD, Material.GOLD_AXE));
        List<Material> Tools = new ArrayList<Material>(
                Arrays.asList(Material.DIAMOND_SPADE, Material.DIAMOND_PICKAXE, Material.IRON_SPADE, Material.IRON_PICKAXE, Material.GOLD_SPADE, Material.GOLD_PICKAXE));
        List<Material> Bows = new ArrayList<Material>(
                Arrays.asList(Material.BOW));
        if (Armors.contains(Gear)) {
            return "Armor";
        }
        else if (Weapons.contains(Gear)) {
            return "Weapons";
        }
        else if (Bows.contains(Gear)) {
            return "Bows";
        }
        else {
            return "Tools";
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }

    private int AmountOfPossibleCes(Material Gear) {
        if (Gear.equals(Material.DIAMOND_SWORD) || Gear.equals(Material.DIAMOND_AXE) || Gear.equals(Material.BOW) || Gear.equals(Material.IRON_SWORD) || Gear.equals(Material.IRON_AXE) || Gear.equals(Material.GOLD_SWORD) || Gear.equals(Material.GOLD_AXE)) {
            return WeaponCeAmount;
        }
        else {
            return ArmorCeAmount;
        }
    }

    private void CheckRarityAndSpawnFireWork(String BookName, Player player) {
        if (BookName.contains(ChatColor.BLUE + "")) {
            SpawnFireWork(player.getLocation(), "Rare", 1);
        }
        else if (BookName.contains(ChatColor.YELLOW + "")) {
            SpawnFireWork(player.getLocation(), "Legendary", 5);
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "----------------------" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.YELLOW + " " + player.getName().toString() + " found " + BookName + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "----------------------");
        }
    }

    private static void SpawnFireWork(Location loc, String Rarity, int Amount) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc.subtract(0,1,0), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(1 / 30);
        if (Rarity == "Rare") {
            fwm.addEffect(FireworkEffect.builder().withColor(Color.BLUE).flicker(true).build());
        } else {
            fwm.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).flicker(true).build());
            loc.getWorld().playSound(loc, Sound.ENDERDRAGON_GROWL,10, 1);
        }
        fw.setFireworkMeta(fwm);
        fw.detonate();
        for (int i = 0; i < Amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc.subtract(0,1,0), EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    public void AddEnchanterItems(Inventory inv, Player player) {
        utilityMain.FillCyprusGuiSize27(inv);
        inv.setItem(10, utilityMain.createGuiItem(Material.EXP_BOTTLE, ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------", ChatColor.YELLOW + "             Levels: " + player.getLevel(), ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------"));
        inv.setItem(13, utilityMain.createGuiItem(Material.ENCHANTMENT_TABLE, ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------", ChatColor.GREEN + "            Enchanter", ChatColor.GRAY + "      Gives a random CE", ChatColor.GRAY + "             Cost: " + ChatColor.GREEN + "15", ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------")); //should be 45 levels
        inv.setItem(16, utilityMain.createGuiItem(Material.EMPTY_MAP, ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------", ChatColor.AQUA + "              CE List", ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-------------------"));
    }

    private void AddForgeItems(Inventory inv, Player player, ItemStack item, ItemStack CursorItem) {
        ItemStack Fill = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        for (int i = 0; i < inv.getSize(); i++) { //Fills Inventory with glass
            inv.setItem(i, Fill);
        }
        Material Book = Material.ENCHANTED_BOOK;
        inv.setItem(29, item);
        inv.setItem(31, utilityMain.createGuiItem(Material.BARRIER, ChatColor.RED + "Close Forge"));
        inv.setItem(33, CursorItem);
        if (item.getItemMeta().getLore().size() == 2) { //For Armor/tools
            inv.setItem(12, utilityMain.createGuiItem(Book, item.getItemMeta().getLore().get(0), ChatColor.YELLOW + "" + ChatColor.ITALIC + "Click to replace this custom enchant"));
            inv.setItem(14, utilityMain.createGuiItem(Book, item.getItemMeta().getLore().get(1), ChatColor.YELLOW + "" + ChatColor.ITALIC + "Click to replace this custom enchant"));
        }
        else { //For Weapons
            inv.setItem(11, utilityMain.createGuiItem(Book, item.getItemMeta().getLore().get(0), ChatColor.YELLOW + "" + ChatColor.ITALIC + "Click to replace this custom enchant"));
            inv.setItem(13, utilityMain.createGuiItem(Book, item.getItemMeta().getLore().get(1), ChatColor.YELLOW + "" + ChatColor.ITALIC + "Click to replace this custom enchant"));
            inv.setItem(15, utilityMain.createGuiItem(Book, item.getItemMeta().getLore().get(2), ChatColor.YELLOW + "" + ChatColor.ITALIC + "Click to replace this custom enchant"));

        }
    }

    public void AddCeListItems(Inventory inv, String Rarity) {
        ItemStack Fill = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemStack GrayDye = new ItemStack(Material.INK_SACK, 1, (byte) 8);
        ItemMeta GrayDyeMeta = GrayDye.getItemMeta();
        GrayDyeMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Common Enchants");
        GrayDyeMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, false);
        GrayDyeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        GrayDye.setItemMeta(GrayDyeMeta);
        ItemStack GreenDye = new ItemStack(Material.INK_SACK, 1, (byte) 10);
        ItemMeta GreenDyeMeta = GreenDye.getItemMeta();
        GreenDyeMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Uncommon Enchants");
        GreenDyeMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, false);
        GreenDyeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        GreenDye.setItemMeta(GreenDyeMeta);
        ItemStack LightBlueDye = new ItemStack(Material.INK_SACK, 1, (byte) 12);
        ItemMeta LightBlueDyeMeta = LightBlueDye.getItemMeta();
        LightBlueDyeMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Rare Enchants");
        LightBlueDyeMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, false);
        LightBlueDyeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        LightBlueDye.setItemMeta(LightBlueDyeMeta);
        ItemStack YellowDye = new ItemStack(Material.INK_SACK, 1, (byte) 11);
        ItemMeta YellowDyeMeta = YellowDye.getItemMeta();
        YellowDyeMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Legendary Enchants");
        YellowDyeMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, false);
        YellowDyeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        YellowDye.setItemMeta(YellowDyeMeta);
        for (int i = 0; i < inv.getSize(); i++) { //Fills Inventory
            inv.setItem(i, Fill);
        }
        inv.setItem(0, utilityMain.createGuiItem(Material.ARROW, ChatColor.RED + "Back"));
        inv.setItem(3, GrayDye);
        inv.setItem(4, GreenDye);
        inv.setItem(5, LightBlueDye);
        inv.setItem(6, YellowDye);
        if (Rarity == "Common") {
            inv.setItem(20, getCeBook("Water Breathing"));
            inv.setItem(21, getCeBook("Resistance"));
            inv.setItem(22, getCeBook("Crapple"));
            inv.setItem(23, getCeBook("Frostbite"));
            inv.setItem(24, getCeBook("Piercing"));
            inv.setItem(28, getCeBook("Patching"));
            inv.setItem(29, getCeBook("Looter"));
            inv.setItem(30, getCeBook("Contaminator"));
            inv.setItem(31, getCeBook("Explosive Shot"));
            inv.setItem(32, getCeBook("Saturation"));
            inv.setItem(33, getCeBook("Starvation"));
            inv.setItem(34, getCeBook("Fire Resistance"));
            inv.setItem(38, getCeBook("Purification"));
            inv.setItem(39, getCeBook("Haste"));
            inv.setItem(40, getCeBook("Obsidian Breaker"));
            inv.setItem(41, getCeBook("Night Vision"));
            inv.setItem(42, getCeBook("Swap"));
        }
        else if (Rarity == "Uncommon") {
            inv.setItem(21, getCeBook("Strength"));
            inv.setItem(22, getCeBook("Riposte"));
            inv.setItem(23, getCeBook("Speed"));
            inv.setItem(28, getCeBook("Regeneration"));
            inv.setItem(29, getCeBook("Health Boost"));
            inv.setItem(30, getCeBook("Combo"));
            inv.setItem(31, getCeBook("Reforged"));
            inv.setItem(32, getCeBook("Toxin"));
            inv.setItem(33, getCeBook("Splitter"));
            inv.setItem(34, getCeBook("Second Life"));
            inv.setItem(39, getCeBook("Cripple"));
            inv.setItem(40, getCeBook("Longshot"));
            inv.setItem(41, getCeBook("Backstab"));
        }
        else if (Rarity == "Rare") {
            inv.setItem(22, getCeBook("Reaper"));
            inv.setItem(29, getCeBook("Lifesteal"));
            inv.setItem(30, getCeBook("Last Stand"));
            inv.setItem(31, getCeBook("Reinforced"));
            inv.setItem(32, getCeBook("Odds"));
            inv.setItem(33, getCeBook("Ethereal"));
            inv.setItem(40, getCeBook("Gapple"));
        }
        else if (Rarity == "Legendary") {
            inv.setItem(30, getCeBook("Bounty Hunter"));
            inv.setItem(31, getCeBook("Tank"));
            inv.setItem(32, getCeBook("Demise"));
        }
    }

    private ItemStack getCeBook(String title) {
        for (ItemStack itemStack : CommonBooks) {
            if (title.equals(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()))) {
                return itemStack;
            }
        }
        for (ItemStack itemStack : UncommonBooks) {
            if (title.equals(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()))) {
                return itemStack;
            }
        }
        for (ItemStack itemStack : RareBooks) {
            if (title.equals(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()))) {
                return itemStack;
            }
        }
        for (ItemStack itemStack : LegendaryBooks) {
            if (title.equals(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()))) {
                return itemStack;
            }
        }
        return null;
    }

    private void GivePlayerCeBook(Player player, boolean PresetRarity, String rarity) {
        ItemStack CeBook = RandomCe();
        Random random = new Random();
        if (PresetRarity) {
            if (rarity.equals("Legendary")) {
                CeBook = LegendaryBooks[random.nextInt(LegendaryBooks.length)];
            } else if (rarity.equals("Rare")) {
                CeBook = RareBooks[random.nextInt(RareBooks.length)];
            }
        }
        utilityMain.GivePlayerItem(player, CeBook, 1);
        if (!CeBook.getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
            player.sendMessage(ChatColor.BOLD + "You Found: " + CeBook.getItemMeta().getDisplayName());
        }
        CheckRarityAndSpawnFireWork(CeBook.getItemMeta().getDisplayName(), player); //Spawns fireworks
    }

    Material Book = Material.ENCHANTED_BOOK;
    private ItemStack[] CommonBooks = {
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Water Breathing", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent water breathing", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Resistance", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent resistance", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Crapple", ChatColor.YELLOW + "" + ChatColor.ITALIC + "3 extra seconds of regeneration 2", ChatColor.YELLOW + "" + ChatColor.ITALIC + "after eating a golden apple", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Frostbite", ChatColor.YELLOW + "" + ChatColor.ITALIC + "5% chance to give enemies slowness", ChatColor.YELLOW + "" + ChatColor.ITALIC + "1 for 4 seconds", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Piercing", ChatColor.YELLOW + "" + ChatColor.ITALIC + "3x damage with bows", ChatColor.GOLD + "" + ChatColor.ITALIC + "Bows"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Patching", ChatColor.YELLOW + "" + ChatColor.ITALIC + "50% chance to get back placed obsidian", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Looter", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Upon a kill, the enemy's armor and", ChatColor.YELLOW + "" + ChatColor.ITALIC + "main hand item automatically get", ChatColor.YELLOW + "" + ChatColor.ITALIC + "added to the users inventory", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Contaminator", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Debuffs you effect enemies with", ChatColor.YELLOW + "" + ChatColor.ITALIC + "are buffed by 1 level", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Explosive Shot", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Upon an arrow impact, the arrow deals", ChatColor.YELLOW + "" + ChatColor.ITALIC + "1 heart of damage to nearby players", ChatColor.GOLD + "" + ChatColor.ITALIC + "Bows"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Saturation", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent saturation", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Starvation", ChatColor.YELLOW + "" + ChatColor.ITALIC + "5% chance to give enemies", ChatColor.YELLOW +  "" + ChatColor.ITALIC + "hunger 3 for 6 seconds", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
            utilityMain.createGuiItem(Book,ChatColor.GRAY + "" + ChatColor.BOLD + "Fire Resistance", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent fire resistance", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Purification", ChatColor.YELLOW + "" + ChatColor.ITALIC + "When a potion is thrown, the user", ChatColor.YELLOW + "" + ChatColor.ITALIC + "has a 25% chance to clear all", ChatColor.YELLOW + "" + ChatColor.ITALIC + "non-permanent debuffs", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Haste", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent haste 2", ChatColor.GOLD + "" + ChatColor.ITALIC + "Tools"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Obsidian Breaker", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Instantly breaks obsidian", ChatColor.GOLD + "" + ChatColor.ITALIC + "Tools"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Night Vision", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent night vision", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book, ChatColor.GRAY + "" + ChatColor.BOLD + "Swap", ChatColor.YELLOW + "" + ChatColor.ITALIC + "1% to switch the enemy's mainhand", ChatColor.YELLOW + "" + ChatColor.ITALIC + "item with a random item in their hotbar", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapon"),
    };

    private ItemStack[] UncommonBooks = {
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Strength", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent strength", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Riposte", ChatColor.YELLOW + "" + ChatColor.ITALIC + "3% chance to deal received damage", ChatColor.YELLOW + "" + ChatColor.ITALIC + "to the enemy and knock them back", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Speed", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent speed 2", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Regeneration", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Permanent regeneration", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Health Boost", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Extra 2 hearts", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Combo", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Deal 3% extra damage per hit", ChatColor.YELLOW + "" + ChatColor.ITALIC + "in a combo", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Reforged", ChatColor.YELLOW + "" + ChatColor.ITALIC + "A kill repairs all armor", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Toxin", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Enemies hit with an arrow receive", ChatColor.YELLOW + "" + ChatColor.ITALIC + "slowness 1, poison 1, and", ChatColor.YELLOW + "" + ChatColor.ITALIC + "weakness 2 for 5 seconds", ChatColor.GOLD + "" + ChatColor.ITALIC + "Bows"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Splitter", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Armor breaks 10% faster, but", ChatColor.YELLOW + "" + ChatColor.ITALIC + "the user receives weakness 1", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Second Life", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Absorption for 5 seconds", ChatColor.YELLOW + "" + ChatColor.ITALIC + "when brought to 1 heart", ChatColor.YELLOW + "" + ChatColor.ITALIC + "or less", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Cripple", ChatColor.YELLOW + "" + ChatColor.ITALIC + "3% chance to effect an enemy", ChatColor.YELLOW + "" + ChatColor.ITALIC + "with weakness 4 for 3 seconds", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Longshot", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Arrows deal +2% damage per", ChatColor.YELLOW + "" + ChatColor.ITALIC + "block traveled", ChatColor.GOLD + "" + ChatColor.ITALIC + "Bows"),
            utilityMain.createGuiItem(Book,ChatColor.GREEN + "" + ChatColor.BOLD + "Backstab", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Enemies hit in the back are", ChatColor.YELLOW + "" + ChatColor.ITALIC + "dealt 25% extra damage", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
    };

    private ItemStack[] RareBooks = {
            utilityMain.createGuiItem(Book,ChatColor.BLUE + "" + ChatColor.BOLD + "Reaper", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Enemies at 4 hearts or less", ChatColor.YELLOW + "" + ChatColor.ITALIC + "are dealt 20% extra damage", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
            utilityMain.createGuiItem(Book,ChatColor.BLUE + "" + ChatColor.BOLD + "Lifesteal", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Heal 10% of damage dealt", ChatColor.GOLD + "" + ChatColor.ITALIC + "Weapons"),
            utilityMain.createGuiItem(Book,ChatColor.BLUE + "" + ChatColor.BOLD + "Last Stand", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Strength 2 at 4 hearts or less", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.BLUE + "" + ChatColor.BOLD + "Reinforced", ChatColor.YELLOW + "" + ChatColor.ITALIC + "10% chance to add 1 durability to item", ChatColor.YELLOW + "" + ChatColor.ITALIC + "after being hit", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.BLUE + "" + ChatColor.BOLD + "Odds", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Percentages for giving debuffs", ChatColor.YELLOW + "" + ChatColor.ITALIC + "to enemies are increased by 3%", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.BLUE + "" + ChatColor.BOLD + "Ethereal", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Gains absorption for 9 seconds", ChatColor.YELLOW + "" + ChatColor.ITALIC + "and strength 2 for 3 seconds", ChatColor.YELLOW + "" + ChatColor.ITALIC + "after the user's pearl lands", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.BLUE + "" + ChatColor.BOLD + "Gapple", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Enchanted golden apples have", ChatColor.YELLOW + "" + ChatColor.ITALIC + "a 10 second lower cooldown", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
    };

    private ItemStack[] LegendaryBooks = {
            utilityMain.createGuiItem(Book,ChatColor.YELLOW + "" + ChatColor.BOLD + "Bounty Hunter", ChatColor.YELLOW + "" + ChatColor.ITALIC + "If bounty hunter is not on cooldown,", ChatColor.YELLOW + "" + ChatColor.ITALIC + "the enemy hit will be bountied, and", ChatColor.YELLOW + "" + ChatColor.ITALIC + "bountied players are dealt 20% extra", ChatColor.YELLOW + "" + ChatColor.ITALIC + "damage from all players for 10 seconds.", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Bounty Hunter has a cooldown of", ChatColor.YELLOW + "" + ChatColor.ITALIC + "30 seconds", ChatColor.GOLD + "" + ChatColor.ITALIC + "Gold Sword"),
            utilityMain.createGuiItem(Book,ChatColor.YELLOW + "" + ChatColor.BOLD + "Tank", ChatColor.YELLOW + "" + ChatColor.ITALIC + "Health boost 5, but user's", ChatColor.YELLOW + "" + ChatColor.ITALIC + "attack damage is reduced by 30%", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
            utilityMain.createGuiItem(Book,ChatColor.YELLOW + "" + ChatColor.BOLD + "Demise", ChatColor.YELLOW + "" + ChatColor.ITALIC + "2% chance to effect enemies,", ChatColor.YELLOW + "" + ChatColor.ITALIC + "in 5 block radius, wither 3", ChatColor.YELLOW + "" + ChatColor.ITALIC + "for 3.5 seconds when hit", ChatColor.GOLD + "" + ChatColor.ITALIC + "Armor"),
    };

    private ItemStack RandomCe() {
        Random rand = new Random();
        int RandomToChoseRarity = rand.nextInt(100);
        if (RandomToChoseRarity  < 52) {
            int RandomIntBasedOffCeAmount = rand.nextInt(CommonBooks.length);
            return CommonBooks[RandomIntBasedOffCeAmount];
        }
        else if (RandomToChoseRarity > 51 && RandomToChoseRarity < 84) {
            int RandomIntBasedOffCeAmount = rand.nextInt(UncommonBooks.length);
            return UncommonBooks[RandomIntBasedOffCeAmount];
        }
        else if (RandomToChoseRarity > 83 && RandomToChoseRarity < 99) {
            int RandomIntBasedOffCeAmount = rand.nextInt(RareBooks.length);
            return RareBooks[RandomIntBasedOffCeAmount];
        }
        else {
            int RandomIntBasedOffCeAmount = rand.nextInt(LegendaryBooks.length);
            return LegendaryBooks[RandomIntBasedOffCeAmount];
        }
    }

    private String unBoldCeBook(String name) {
        String stripped = (ChatColor.stripColor(name));
        if (name.contains(ChatColor.GRAY + "")) {
            return (ChatColor.GRAY + stripped);
        }
        if (name.contains(ChatColor.GREEN + "")) {
            return (ChatColor.GREEN + stripped);
        }
        if (name.contains(ChatColor.BLUE + "")) {
            return (ChatColor.BLUE + stripped);
        }
        else {
            return (ChatColor.YELLOW + stripped);
        }
    }
}
