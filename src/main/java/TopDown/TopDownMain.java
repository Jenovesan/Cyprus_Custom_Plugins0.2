package TopDown;

import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TopDownMain extends CommandExecute implements Listener, CommandExecutor {
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("cyprus.topdown") || !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return true;
        }
        Player player = (Player) sender;
        if (utilityMain.enemyIsNearby(player, 100)) {
            sender.sendMessage(ChatColor.RED + "There is an enemy nearby");
            return true;
        }
        if (utilityMain.isInEnemyTerritory(player, player.getLocation())) {
            sender.sendMessage(ChatColor.RED + "You cannot do this here");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("top")) {
            Top(player);
        } else {
            Down(player);
        }
        return true;
    }

    private void Top(Player player) {
        Location location = player.getLocation();
        int Yval = (int) location.getY();
        for (int i = Yval; i < 256; i++) {
            Location NewLoc = new Location(location.getWorld(), location.getX(), i, location.getZ());
            if (!NewLoc.getBlock().getType().equals(Material.AIR) && NewLoc.add(0, 1,0).getBlock().getType().equals(Material.AIR) && NewLoc.add(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                player.teleport(location.add(0, (i - location.getY()) + 1, 0));
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "No teleportable areas found");
    }

    private void Down(Player player) {
        Location location = player.getLocation();
        int Yval = (int) location.getY();
        for (int i = Yval; i > 1; i--) {
            Location NewLoc = new Location(location.getWorld(), location.getX(), i, location.getZ());
            if (!NewLoc.getBlock().getType().equals(Material.AIR) && NewLoc.subtract(0, 1,0).getBlock().getType().equals(Material.AIR) && NewLoc.subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                player.teleport(location.subtract(0, (location.getY() - i) + 2, 0));
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "No teleportable areas found");
    }


}
