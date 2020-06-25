package Staff;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class StaffTrack extends CommandExecute implements Listener, CommandExecutor  { //Work In progress
    MainClass mainClass;
    public StaffTrack(MainClass mc) {
        mainClass = mc;
    }

    HashMap<Player, Player> Tracked = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0) {
            SendHelpMessage(sender);
            return true;
        }
        if (args.length == 1 && sender instanceof Player) {
            if (Bukkit.getPlayerExact(args[0]) != null) {
                Player TargetPlayer = Bukkit.getPlayerExact(args[0]);
                Tracked.put((Player) sender, TargetPlayer);
                sender.sendMessage(Tracked.toString());
                sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + args[0] + " has been tracked");
                return true;
            }
        }
        SendHelpMessage(sender);
        return true;
    }

    private void SendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Track:" + "\n" + "/track <player name> - tracks the given player");
    }

    @EventHandler
    private void entityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player damagee = (Player) event.getEntity();
        damager.sendMessage(Tracked.toString());
        if (Tracked.containsValue(damager)) {
            for (Map.Entry<Player, Player> element : Tracked.entrySet()) {
                Player tracker = element.getKey();
                Player tracked = element.getValue();
                if (tracked.equals(damager)) {
                    tracker.sendMessage(ChatColor.YELLOW + damager.getName() + ": " + ChatColor.GOLD + "" + ChatColor.BOLD + damager.getLocation().distance(damagee.getLocation()) + " blocks " + ChatColor.YELLOW + getPing(damager) + "ping");
                }
            }
        }
    }

    private Integer getDistance(Player player1, Player player2) {
        double x = player1.getLocation().distance(player2.getLocation());
        int y = (int) (x * 100);
        return y;
    }

    private Integer getPing(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        return entityPlayer.ping;
    }

}
