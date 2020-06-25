package Staff;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffReportCommand extends CommandExecute implements CommandExecutor {
    StaffMain staffMain = new StaffMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player reporter = (Player) sender;
        if (args.length > 1 && Bukkit.getPlayerExact(args[0]) != null && Bukkit.getPlayerExact(args[0]).isOnline()) {
            staffMain.SendStaffMessage(ChatColor.DARK_RED + "Report by " + reporter.getName() + ": " + ChatColor.RED + getReason(args));
            sender.sendMessage(ChatColor.GREEN + "Report Sent!");
            return true;
        }
        sender.sendMessage(ChatColor.GRAY + "Usage: /report <Player Name> <Reason>");
        return false;
    }

    private String getReason(String[] args) {
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg).append(" ");
            }
            return sb.toString();
        }
        return "";
    }
}
