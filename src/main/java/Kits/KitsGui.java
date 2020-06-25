package Kits;

import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitsGui extends CommandExecute implements CommandExecutor {
    MainClass mainClass;
    public KitsGui(MainClass mc) {
        mainClass = mc;
    }
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.isOp()) {
            LoadGui((Player) sender);
            return true;
        } else {
            if (args.length == 0) {
                LoadGui((Player) sender);
                return true;
            } else if (args[0].equalsIgnoreCase("reset")){
                Player TargetPlayer = null;
                if (args.length == 2 && Bukkit.getPlayerExact(args[1]) != null) {
                    TargetPlayer = Bukkit.getPlayerExact(args[1]);
                } else if (sender instanceof Player) {
                    TargetPlayer = (Player) sender;
                } if (TargetPlayer != null) {
                    ResetKitCooldowns(TargetPlayer);
                    sender.sendMessage(ChatColor.DARK_GREEN + TargetPlayer.getName() + ChatColor.GREEN + " cooldowns reset");
                }
                return true;
            }
        }
        if (sender.isOp()) {
            sender.sendMessage(ChatColor.GOLD + "Usage: /kit" + "\n" + "/kit reset <Player Name>");
        }
        return true;
    }

    private void ResetKitCooldowns(Player player) {
        UUID uuid = player.getUniqueId();
        mainClass.kitsMain.Traveler.remove(uuid);
        mainClass.kitsMain.Novice.remove(uuid);
        mainClass.kitsMain.Elite.remove(uuid);
        mainClass.kitsMain.Legion.remove(uuid);
        mainClass.kitsMain.Spartan.remove(uuid);
        mainClass.kitsMain.Titan.remove(uuid);
        mainClass.kitsMain.Olympian.remove(uuid);

        mainClass.kitsMain.Juggernaut.remove(uuid);
        mainClass.kitsMain.Ravager.remove(uuid);
        mainClass.kitsMain.Viking.remove(uuid);
        mainClass.kitsMain.Tank.remove(uuid);
        mainClass.kitsMain.Raiding.remove(uuid);
        mainClass.kitsMain.Enchanter.remove(uuid);
        mainClass.kitsMain.Archer.remove(uuid);
        mainClass.kitsMain.Bard.remove(uuid);
        mainClass.kitsMain.SOTW.remove(uuid);
        mainClass.kitsMain.Tools.remove(uuid);

        mainClass.kitsMain.SaveKitsInConfig();
    }

    public void LoadGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 54, ChatColor.RED + "" + ChatColor.BOLD + "Kits");
        FillGui(inv);
        FillCustomItems(inv, player.getUniqueId());
        player.openInventory(inv);
    }

    private void FillCustomItems(Inventory inv, UUID uuid) {
        inv.setItem(10, utilityMain.createGuiItem(Material.RABBIT_FOOT, ChatColor.RED + "" + ChatColor.BOLD + "Traveler Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Traveler, uuid), GetSecondLoreLine(mainClass.kitsMain.Traveler, uuid)));
        inv.setItem(11, utilityMain.createGuiItem(Material.FEATHER, ChatColor.RED + "" + ChatColor.BOLD + "Novice Kit",ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Novice, uuid), GetSecondLoreLine(mainClass.kitsMain.Novice, uuid)));
        inv.setItem(12, utilityMain.createGuiItem(Material.GHAST_TEAR, ChatColor.RED + "" + ChatColor.BOLD + "Elite Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Elite, uuid), GetSecondLoreLine(mainClass.kitsMain.Elite, uuid)));
        inv.setItem(13, utilityMain.createGuiItem(Material.DOUBLE_PLANT, ChatColor.RED + "" + ChatColor.BOLD + "Legion Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Legion, uuid), GetSecondLoreLine(mainClass.kitsMain.Legion, uuid)));
        inv.setItem(14, utilityMain.createGuiItem(Material.NETHER_STALK, ChatColor.RED + "" + ChatColor.BOLD + "Spartan Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Spartan, uuid), GetSecondLoreLine(mainClass.kitsMain.Spartan, uuid)));
        inv.setItem(15, utilityMain.createGuiItem(Material.NETHER_STAR, ChatColor.RED + "" + ChatColor.BOLD + "Titan Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Titan, uuid), GetSecondLoreLine(mainClass.kitsMain.Titan, uuid)));
        inv.setItem(16, utilityMain.createGuiItem(Material.PRISMARINE_SHARD, ChatColor.RED + "" + ChatColor.BOLD + "Olympian Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Olympian, uuid), GetSecondLoreLine(mainClass.kitsMain.Olympian, uuid)));

        inv.setItem(28, utilityMain.createGuiItem(Material.DIAMOND_SWORD, ChatColor.RED + "" + ChatColor.BOLD + "Juggernaut Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Juggernaut, uuid), GetSecondLoreLine(mainClass.kitsMain.Juggernaut, uuid)));
        inv.setItem(29, utilityMain.createGuiItem(Material.IRON_SWORD, ChatColor.RED + "" + ChatColor.BOLD + "Ravager Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Ravager, uuid), GetSecondLoreLine(mainClass.kitsMain.Ravager, uuid)));
        inv.setItem(30, utilityMain.createGuiItem(Material.GOLD_AXE, ChatColor.RED + "" + ChatColor.BOLD + "Viking Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Viking, uuid), GetSecondLoreLine(mainClass.kitsMain.Viking, uuid)));
        inv.setItem(31, utilityMain.createGuiItem(Material.DIAMOND_CHESTPLATE, ChatColor.RED + "" + ChatColor.BOLD + "Tank Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Tank, uuid), GetSecondLoreLine(mainClass.kitsMain.Tank, uuid)));
        inv.setItem(32, utilityMain.createGuiItemFromItemStack(new ItemStack(Material.MONSTER_EGG, 1, (short) 50), ChatColor.RED + "" + ChatColor.BOLD + "Raiding Kit",  ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED +GetTime(mainClass.kitsMain.Raiding, uuid), GetSecondLoreLine(mainClass.kitsMain.Raiding, uuid)));
        inv.setItem(33, utilityMain.createGuiItem(Material.EXP_BOTTLE, ChatColor.RED + "" + ChatColor.BOLD + "Enchanter Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Enchanter, uuid), GetSecondLoreLine(mainClass.kitsMain.Enchanter, uuid)));
        inv.setItem(34, utilityMain.createGuiItem(Material.BOW, ChatColor.RED + "" + ChatColor.BOLD + "Archer Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Archer, uuid), GetSecondLoreLine(mainClass.kitsMain.Archer, uuid)));
        inv.setItem(39, utilityMain.createGuiItem(Material.GOLD_HELMET, ChatColor.RED + "" + ChatColor.BOLD + "Bard Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Bard, uuid), GetSecondLoreLine(mainClass.kitsMain.Bard, uuid)));
        inv.setItem(40, utilityMain.createGuiItem(Material.SUGAR_CANE, ChatColor.RED + "" + ChatColor.BOLD + "SOTW Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.SOTW, uuid), GetSecondLoreLine(mainClass.kitsMain.SOTW, uuid)));
        inv.setItem(41, utilityMain.createGuiItem(Material.DIAMOND_PICKAXE, ChatColor.RED + "" + ChatColor.BOLD + "Tools Kit", ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Available In: " + ChatColor.RED + GetTime(mainClass.kitsMain.Tools, uuid), GetSecondLoreLine(mainClass.kitsMain.Tools, uuid)));
    }

    private String GetTime(HashMap<UUID, Integer> kit, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        String time;
        if (!hasAccess(kit, player)) {
            return "No Access!";
        } else {

            if (kit.containsKey(uuid)) {
                time = ChatColor.RED + utilityMain.FormatTime(kit.get(uuid));
            } else {
                time = ChatColor.GREEN + "Now";
            }
        }
        return time;
    }

    private String GetSecondLoreLine(HashMap<UUID, Integer> kit, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (!hasAccess(kit, player)) {
            return ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Puchase with " + ChatColor.RED + "" + ChatColor.UNDERLINE + "/buy";
        }
        return ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.WHITE + "Left-Click to claim!";
    }

    private Boolean hasAccess(HashMap<UUID, Integer> kit, Player player) {
        if (kit.equals(mainClass.kitsMain.Traveler)) {
            return true;
        } if (kit.equals(mainClass.kitsMain.Novice)) {
            return player.hasPermission("kit.novice");
        } else if (kit.equals(mainClass.kitsMain.Elite)) {
            return player.hasPermission("kit.elite");
        } else if (kit.equals(mainClass.kitsMain.Legion)) {
            return player.hasPermission("kit.legion");
        } else if (kit.equals(mainClass.kitsMain.Spartan)) {
            return player.hasPermission("kit.spartan");
        } else if (kit.equals(mainClass.kitsMain.Titan)) {
            return player.hasPermission("kit.titan");
        } else if (kit.equals(mainClass.kitsMain.Olympian)) {
            return player.hasPermission("kit.olympian");
        } else if (kit.equals(mainClass.kitsMain.Juggernaut)) {
            return player.hasPermission("kit.juggernaut");
        } else if (kit.equals(mainClass.kitsMain.Ravager)) {
            return player.hasPermission("kit.ravager");
        } else if (kit.equals(mainClass.kitsMain.Viking)) {
            return player.hasPermission("kit.viking");
        } else if (kit.equals(mainClass.kitsMain.Tank)) {
            return player.hasPermission("kit.tank");
        } else if (kit.equals(mainClass.kitsMain.Raiding)) {
            return player.hasPermission("kit.raiding");
        } else if (kit.equals(mainClass.kitsMain.Enchanter)) {
            return player.hasPermission("kit.enchanter");
        } else if (kit.equals(mainClass.kitsMain.Archer)) {
            return player.hasPermission("kit.archer");
        } else if (kit.equals(mainClass.kitsMain.Bard)) {
            return player.hasPermission("kit.bard");
        } else if (kit.equals(mainClass.kitsMain.SOTW)) {
            return player.hasPermission("kit.sotw");
        } else if (kit.equals(mainClass.kitsMain.Tools)) {
            return player.hasPermission("kit.tools");
        }
        return null;
    }

    private void FillGui(Inventory inv) {
        ItemStack Black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack Red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Black);
        }
        List<Integer> ints = new ArrayList<>(Arrays.asList(4, 21, 22, 23, 27, 38, 49, 35, 42));
        for (int i = 0; i < inv.getSize(); i++) {
            if (ints.contains(i)) {
                inv.setItem(i, Red);
            }
        }
    }
}
