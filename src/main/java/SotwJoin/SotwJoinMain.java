package SotwJoin;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SotwJoinMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    public SotwJoinMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 1 && sender.isOp()) {
            if (args[0].equalsIgnoreCase("enable")) {
                Enabled = true;
                sender.sendMessage(ChatColor.RED + "Sotw join has been enabled");
            } else if (args[0].equalsIgnoreCase("disable")) {
                Enabled = false;
                sender.sendMessage(ChatColor.RED + "Sotw join has been disabled");
            } else {
                sender.sendMessage(ChatColor.GRAY + "/sotwjoin <Enable/Disable>");
            }
        }
        return true;
    }

    Boolean Enabled = false;

    @EventHandler
    private void PlayerChats(PlayerCommandPreprocessEvent event) {
        if (Enabled && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use commands at this time");
        }
    }

    public void SotwJoinTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Enabled) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.isOp()) {
                            player.teleport(Bukkit.getWorld("firerealm").getSpawnLocation());
                        }
                    }
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 20);
    }
}
