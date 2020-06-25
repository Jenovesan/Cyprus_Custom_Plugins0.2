package ChunkBusters;

import CustomItems.CustomItemsMain;
import Utility.UtilityMain;
import com.massivecraft.factions.*;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChunkBustersMain extends CommandExecute implements Listener, CommandExecutor {
    UtilityMain utilityMain = new UtilityMain();
    CustomItemsMain customItemsMain = new CustomItemsMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 2 && Bukkit.getPlayerExact(args[0]) != null && utilityMain.isNumber(args[1]) && sender.isOp()) {
            Player TargetPlayer = Bukkit.getPlayerExact(args[0]);
            utilityMain.GivePlayerItem(TargetPlayer, customItemsMain.ChunkBuster(), Integer.parseInt(args[1]));
            sender.sendMessage(ChatColor.GREEN + "Gave Chunk Buster");
            return true;
        }
        sender.sendMessage(ChatColor.GRAY + "Usage: /chunkbuster <Player Name> <Amount>");
        return true;
    }

    @EventHandler
    private void UsedChunkBuster(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        Block block = event.getClickedBlock();
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
        if (item.getType().equals(Material.ENDER_PORTAL_FRAME) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Chunk Buster")) {
            if (IsInFactionTerritory(player, faction, block.getLocation())) {
                UseChunkBuster(block.getChunk(), block.getY());
                event.setCancelled(true);;
                RemoveAChunkBusterFromHand(player);
            } else {
                player.sendMessage(ChatColor.RED + "You can only use chunk busters in your territory");
                event.setCancelled(true);
                player.setItemInHand(player.getItemInHand());
            }
        }
    }


    private void RemoveAChunkBusterFromHand(Player player) {
        ItemStack item = player.getItemInHand();
        ItemStack NewItem = new ItemStack(item);
        NewItem.setAmount(NewItem.getAmount() - 1);
        if (NewItem.getAmount() > 0) {
            player.setItemInHand(NewItem);
        } else {
            player.setItemInHand(null);
        }
    }

    private void UseChunkBuster(Chunk chunk, Integer YLoc) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y <= YLoc; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x,y,z);
                    if (!block.getType().equals(Material.MOB_SPAWNER) && !block.getType().equals(Material.BEDROCK)) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    private boolean IsInFactionTerritory(Player player, Faction faction, Location location) {
        FLocation fLocation = new FLocation(location);
        Faction FactionAtLocation = Board.getInstance().getFactionAt(fLocation);
        return faction.equals(FactionAtLocation) && utilityMain.isInFaction(player);
    }

}
