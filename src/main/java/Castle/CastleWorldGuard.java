package Castle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CastleWorldGuard implements Listener {

    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Location BlockLocation = block.getLocation();
        Player player = event.getPlayer();
        if (player.isOp()) {
            return;
        }
        if (!(block.getType().equals(Material.LADDER)) && isInCastleArea(BlockLocation)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can only place ladders near the castle");
        }
    }

    @EventHandler
    private void projectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            Player player = (Player) projectile.getShooter();
            if (player.isOp()) {
                return;
            }
            if (isInCastleArea(player.getLocation())) {
                event.setCancelled(true);
                ItemStack itemStack = player.getItemInHand();
                itemStack.setAmount(itemStack.getAmount() + 1);
                player.sendMessage(ChatColor.RED + "You cannot throw enderpearls near the castle");
            }
        }
    }

    List<Material> BreakableBlocks = new ArrayList<>(Arrays.asList(Material.LADDER, Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK));
    @EventHandler
    private void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player.isOp()) {
            return;
        }
        if (isInCastleArea(block.getLocation())) {
            if (!(BreakableBlocks.contains(block.getType()))) {
                event.setCancelled(true);
                player.sendMessage(BreakableBlocks.toString());
                player.sendMessage(ChatColor.RED + "You cannot break that type of block near the castle");
            }
        }
    }

    private boolean isInCastleArea(Location location) {
        if (!(location.getWorld().getName().equals("world"))) {
            return false;
        }
        int x = (int) location.getX();
        int z = (int) location.getZ();
        if ((x < 430 ||  x > 665) || (z < -130 || z > 25)) {
            return false;
        }
        return true;
    }
}
