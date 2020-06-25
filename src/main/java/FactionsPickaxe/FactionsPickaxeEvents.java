package FactionsPickaxe;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;

public class FactionsPickaxeEvents implements Listener {
    @EventHandler
    private void playerMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(player.getItemInHand().equals(Material.AIR) || !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasDisplayName()) {
            return;
        }

        String name = player.getItemInHand().getItemMeta().getDisplayName();
        String splitName = "";
        int strength = 0;
        boolean isNetherrack = false;
        if(name.equals(ChatColor.stripColor(name))) {
            return;
        }

        splitName = ChatColor.stripColor(name);
        splitName = splitName.replaceAll("[^0-9]", "");
        // All the splits
        splitName = splitName.substring(splitName.length() / 2);
        try {
            strength = Integer.parseInt(splitName);
        } catch (NumberFormatException e) {
            return;
        }
        if(!name.contains(ChatColor.RED + "Factions Pickaxe ")) {
            return;
        }
        // Decides whether Item in hand is a trench pickaxe
        Location center = block.getLocation();

        if(!isInFactionTerritory(block, player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You must be in faction territory to use this!");
            return;
        }

        int startx = center.getBlockX() - ((strength - 1)/2), starty = center.getBlockY() - ((strength - 1)/2), startz = center.getBlockZ() - ((strength - 1)/2);
        int endx = center.getBlockX() + ((strength - 1)/2), endy = center.getBlockY() + ((strength - 1)/2), endz = center.getBlockZ() + ((strength - 1)/2);
        if(starty < 0) {
            starty = 0;
        }
        if(endy > 256) {
            endy = 256;
        }
        if(block.getType().equals(Material.NETHERRACK)) {
            isNetherrack = true;
        }
        if(!FPlayers.getInstance().getByPlayer(player).hasFaction()) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this item");
        } else {
            for (int x = startx; x <= endx; x++) {
                for (int y = starty; y <= endy; y++) {
                    for (int z = startz; z <= endz; z++) {
                        Block setBlock = player.getWorld().getBlockAt(x, y, z);
                        if (breakable(setBlock, isNetherrack, player)) {
                            setBlock.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    private boolean breakable(Block block, boolean isNetherrack, Player player) {
        if(!((!isNetherrack || block.getType().equals(Material.NETHERRACK)) && !block.getType().equals(Material.BEDROCK))) {
            return false;
        }
        return isInFactionTerritory(block, player);
    }

    private boolean isInFactionTerritory(Block block, Player player) {
        FLocation fBlock = new FLocation(block.getLocation());
        Faction fPlayer = FPlayers.getInstance().getByPlayer(player).getFaction();
        Set<FLocation> testForChunks = fPlayer.getAllClaims();
        if(testForChunks.contains(fBlock)) {
            return true;
        } else {
            return false;
        }
    }
}
