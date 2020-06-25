package SupplyDrop;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SupplyDropTimer {
    MainClass mainClass;
    SupplyDropMain supplyDropMain;
    public SupplyDropTimer(MainClass mc) {
        mainClass = mc;
        supplyDropMain = new SupplyDropMain(mainClass);
    }

    void AnnounceMinutesUntilSupplyDrop(int Minutes) {
        String TimeIncrement = " minutes";
        if (Minutes == 1) {
            TimeIncrement = " minute";
        }
        Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.RED + "  Supply Drop in " + Minutes + TimeIncrement + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------");
    }
    public void TimmerRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int TimeUntilSupplyDrop = mainClass.getConfig().getInt("TimeUntilSupplyDrop");
                if (TimeUntilSupplyDrop != 1) {
                    mainClass.getConfig().set("TimeUntilSupplyDrop", (TimeUntilSupplyDrop - 1));
                    mainClass.saveConfig();
                    if (TimeUntilSupplyDrop - 1 == 10 || TimeUntilSupplyDrop - 1 == 5 || TimeUntilSupplyDrop - 1 == 1) {
                        AnnounceMinutesUntilSupplyDrop(TimeUntilSupplyDrop - 1);
                    }
                }
                else {
                    mainClass.getConfig().set("TimeUntilSupplyDrop", mainClass.getConfig().getInt("TotalTimeForSupplyDrop"));
                    mainClass.saveConfig();
                    if (mainClass.getConfig().getBoolean("State")) {
                        supplyDropMain.SummonSupplyDrop();
                    }
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 1200);
    }
}
