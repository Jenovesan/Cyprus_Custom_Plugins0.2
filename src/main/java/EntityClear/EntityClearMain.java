package EntityClear;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;


public class EntityClearMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    public EntityClearMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender.isOp()) {
            ClearEntities();
        }

        return false;
    }

    private void SendFinalEntityClearMessage(Integer TotalEntitiesRemoved) {
        Bukkit.getServer().broadcastMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "(" + ChatColor.RED + "⚝" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ")" + ChatColor.RED + " Successfully" + ChatColor.GRAY + " removed " + ChatColor.RED + TotalEntitiesRemoved + ChatColor.GRAY + " entities!");
    }

    public void ClearEntitiesTime() {
        new BukkitRunnable() {
            @Override
            public void run() {
                ClearEntitiesCountdown();
            }
        }.runTaskTimer(mainClass, 0, 6000);
    }

    private void ClearEntitiesCountdown() {
        new BukkitRunnable() {
            Integer countdown = 61;
            @Override
            public void run() {
                countdown--;
                if (countdown.equals(60) || countdown.equals(20) || countdown.equals(3) || countdown.equals(2) || countdown.equals(1)) {
                    Bukkit.getServer().broadcastMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "(" + ChatColor.RED + "⚝" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ")" + ChatColor.GRAY + " All entities will be cleared in " + ChatColor.RED + countdown + ChatColor.GRAY + " seconds");
                } if (countdown < 1) {
                    ClearEntities();
                    this.cancel();
                }
            }
        }.runTaskTimer(mainClass, 0, 20);
    }

    Integer TrackClearTimes = 0;
    private void ClearEntities() {
        int TotalEntitiesRemoved = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof TNTPrimed) && !(entity instanceof FallingBlock) && !(entity instanceof EnderPearl) && !(entity instanceof Blaze) && !(entity instanceof Villager) && !(entity instanceof ArmorStand) && !(entity instanceof Player)) {
                    entity.remove();
                    TotalEntitiesRemoved++;
                } if (TrackClearTimes >= 3 && entity instanceof Blaze) {
                    entity.remove();
                    TotalEntitiesRemoved++;
                    TrackClearTimes = 0;
                }
            }
        }
        TrackClearTimes++;
        SendFinalEntityClearMessage(TotalEntitiesRemoved);
    }
}
