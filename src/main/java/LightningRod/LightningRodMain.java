package LightningRod;

import CustomItems.CustomItemsMain;
import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LightningRodMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    CustomItemsMain customItemsMain = new CustomItemsMain();
    public LightningRodMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 3 && args[0].equalsIgnoreCase("give") && Bukkit.getPlayerExact(args[1]) != null && utilityMain.isNumber(args[2]) && sender.isOp()) {
            Player TargetPlayer = Bukkit.getPlayerExact(args[1]);
            utilityMain.GivePlayerItem(TargetPlayer, customItemsMain.LightningRod(),1);
            return true;
        }
        sender.sendMessage(ChatColor.GRAY + "Usage: /lightningrod give <Player Name> <Amount>");
        return true;
    }

    List<Player> LightningRodCooldown = new ArrayList<>();
    @EventHandler
    private void UsedLightningWand(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item != null && item.getType().equals(Material.BLAZE_ROD) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "" + ChatColor.BOLD + "Lightning Rod")) {
           if (!LightningRodCooldown.contains(player)) {
               Location location = player.getTargetBlock((Set<Material>) null, 50).getLocation();
               player.getWorld().strikeLightning(location);
               LightningRodCooldown.add(player);
               RemoveFromCooldown(player);
           } else {
               player.sendMessage(ChatColor.RED + "Lightning Rod is on cooldown");
           }
        }
    }

    private void RemoveFromCooldown(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                LightningRodCooldown.remove(player);
            }
        }.runTaskLaterAsynchronously(mainClass.getPlugin(), 100);
    }
}
