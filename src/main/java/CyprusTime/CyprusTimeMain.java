package CyprusTime;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CyprusTimeMain extends CommandExecute implements CommandExecutor {
    MainClass mainClass;
    public CyprusTimeMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender.isOp() && args.length == 1) {
            if (args[0].equalsIgnoreCase("sotw")) {
                time = Time.SOTW;
                SaveTimeInConfig();
                sender.sendMessage(ChatColor.GREEN + "Time set to " + ChatColor.DARK_GREEN + time.toString());
                return true;
            } else if (args[0].equalsIgnoreCase("grace")) {
                time = Time.GRACE;
                SaveTimeInConfig();
                sender.sendMessage(ChatColor.GREEN + "Time set to " + ChatColor.DARK_GREEN + time.toString());
                return true;
            } else if (args[0].equalsIgnoreCase("tnt")) {
                time = Time.TNT;
                SaveTimeInConfig();
                sender.sendMessage(ChatColor.GREEN + "Time set to " + ChatColor.DARK_GREEN + time.toString());
                return true;
            } else if (args[0].equalsIgnoreCase("state")) {
                sender.sendMessage(ChatColor.GREEN + "The time is " + ChatColor.DARK_GREEN + time.toString());
                return true;
            }
        }
        sender.sendMessage(ChatColor.GRAY + "Cyprustime Time:" + "\n" + "/cyprus state" + "\n" + "/cyprustime sotw" + "\n" + "/cyprustime grace" + "\n" + "/cyprustime tnt");
        return true;
    }

    public Time time = Time.SOTW;

    public enum Time {
        SOTW,
        GRACE,
        TNT
    }

    private void SaveTimeInConfig() {
        if (time.equals(Time.SOTW)) {
            mainClass.getConfig().set("Time", "sotw");
        } else if (time.equals(Time.GRACE)) {
            mainClass.getConfig().set("Time", "grace");
        } else if (time.equals(Time.TNT)) {
            mainClass.getConfig().set("Time", "tnt");
        }
        mainClass.saveConfig();
    }

    public void LoadTimeFromConfig() {
        String TimeString = mainClass.getConfig().getString("Time");
        if (TimeString == null) {
            return;
        }
        switch (TimeString) {
            case "sotw":
                time = Time.SOTW;
                break;
            case "grace":
                time = Time.GRACE;
                break;
            case "tnt":
                time = Time.TNT;
                break;
        }
    }
}
