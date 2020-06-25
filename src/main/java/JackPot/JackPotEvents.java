package JackPot;

import Main.MainClass;
import Utility.UtilityMain;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class JackPotEvents implements Listener {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public JackPotEvents(MainClass mc) {
        mainClass = mc;
    }

    long JackPotTotal;

    public long getJackPotTotal() {
        JackPotTotal = mainClass.getConfig().getLong("JackPotTotal");
        return JackPotTotal;
    }

    public void setJackPotTotal(long Amount) {
        JackPotTotal = Amount;
        SaveJackPotTotalInConfig();
    }

    public void LoadJackPotTotalFromConfig() {
        JackPotTotal = mainClass.getConfig().getLong("JackPotTotal");
    }

    public void changeJackPotTotal(long Amount) {
        JackPotTotal = (JackPotTotal + Amount);
        SaveJackPotTotalInConfig();
    }

    private void SaveJackPotTotalInConfig() {
        mainClass.getConfig().set("JackPotTotal", JackPotTotal);
        mainClass.saveConfig();
    }

    public void MakePlayerWinJackpot(Player player) {
        for (int i = 0; i < 3; i++) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------" + "\n" + ChatColor.COLOR_CHAR + " " + "\n" + ChatColor.GOLD + ChatColor.BOLD + "     " + player.getName() + ChatColor.YELLOW + " Won The" + "\n" + "   " + ChatColor.UNDERLINE + "Lottery For A Total Of" + "\n" + ChatColor.RESET + ChatColor.COLOR_CHAR + " " + "\n" + ChatColor.YELLOW + ChatColor.BOLD + "         " + utilityMain.FormatPrice((int) getJackPotTotal()) + "\n"  + ChatColor.COLOR_CHAR + " " + "\n" + ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------");
        }
        for (Player OnlinePlayer : Bukkit.getOnlinePlayers()) {
            OnlinePlayer.playSound(OnlinePlayer.getLocation(), Sound.ENDERDRAGON_DEATH, 100, 100);
        }
        Economy economy  = MainClass.getEconomy();
        economy.depositPlayer(player, getJackPotTotal());
        JackPotTotal = 0;
        SaveJackPotTotalInConfig();
    }

    @EventHandler
    private void playerDeath(PlayerDeathEvent event) {
        changeJackPotTotal(10000);
    }

    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!(player.hasPlayedBefore())) {
            changeJackPotTotal(10000);
        }
    }
}
