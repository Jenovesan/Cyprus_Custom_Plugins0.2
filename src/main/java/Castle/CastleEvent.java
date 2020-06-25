package Castle;

import Main.MainClass;
import Utility.UtilityMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;


public class CastleEvent {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public CastleEvent(MainClass mc) {
        mainClass = mc;
    }

    private void PickNextCastleTime() {
        mainClass.getConfig().set("TimeTilCastle", (utilityMain.getRandom().nextInt(72) + 72)); //Between 12 hours and 24 hours);
        mainClass.saveConfig();
    }

    public void PickNextCastleTimeIfNull() {
        if (mainClass.getConfig().get("TimeTilCastle") == null) {
            PickNextCastleTime();
        }
    }

    public void TimeTilCastleTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int TimeTilCastle = mainClass.getConfig().getInt("TimeTilCastle");
                TimeTilCastle = (TimeTilCastle - 1);
                if (TimeTilCastle < 1) {
                    PickNextCastleTime();
                    StartEvent();
                }
                else {
                    if (TimeTilCastle == 1) {
                        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-----------------------------" + "\n" + ChatColor.COLOR_CHAR + "." + ChatColor.RED + "Castle can be seiged in " + ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "10 minutes" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "-----------------------------");
                    }
                    mainClass.getConfig().set("TimeTilCastle", TimeTilCastle);
                    mainClass.saveConfig();
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);  //Should be 12000
    }

    private void StartEvent(){
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------" + "\n" + ChatColor.COLOR_CHAR + "." + ChatColor.RED + "Castle can now be seiged!" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------");
        mainClass.getConfig().set("CastleIsHappening", true);
        mainClass.getConfig().set("CastleTime", 30);
        mainClass.getConfig().set("GateNumber", 1);
        mainClass.getConfig().set("GateHealth", 25);
        mainClass.saveConfig();
        CastleTimer();
    }

    private void CastleTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int CastleTime = mainClass.getConfig().getInt("CastleTime");
                CastleTime = (CastleTime - 1);
                if (CastleTime == 15) {
                    Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------" + "\n" + ChatColor.COLOR_CHAR + "." + ChatColor.RED + "Castle ending in " + ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "15 minutes" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------");
                }
                else if (CastleTime == 5) {
                    Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------" + "\n" + ChatColor.COLOR_CHAR + "." + ChatColor.RED + "Castle ending in " + ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "5 minutes" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------");
                }
                else if (CastleTime == 1) {
                    Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------" + "\n" + ChatColor.COLOR_CHAR + "." + ChatColor.RED + "Castle ending in " + ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "1 minute" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------");
                }
                else if (CastleTime < 1) {
                    EndEvent();
                    this.cancel();
                }
                mainClass.getConfig().set("CastleTime", CastleTime);
                mainClass.saveConfig();
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);  //Should be 1200
    }

    private void EndEvent() {
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "----------------------------" + "\n" + ChatColor.COLOR_CHAR + "." + ChatColor.RED + "The castle can no longer be seiged" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "----------------------------");
        mainClass.getConfig().set("CastleIsHappening", false);
    }

    public boolean isHappening() {
        boolean CastleState = mainClass.getConfig().getBoolean("CastleIsHappening");
        return CastleState;
    }

    public void RunCastleTimerOnStartupIfEnabled() {
        if (isHappening()) {
            CastleTimer();
        }
    }
}