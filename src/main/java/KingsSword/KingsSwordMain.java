package KingsSword;

import CustomItems.CustomItemsMain;
import Utility.UtilityMain;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KingsSwordMain extends CommandExecute implements CommandExecutor {
    UtilityMain utilityMain = new UtilityMain();
    CustomItemsMain customItemsMain = new CustomItemsMain();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.isOp() || args.length != 1 || Bukkit.getPlayerExact(args[0]) == null) {
            sender.sendMessage(ChatColor.GRAY + "Usage: /kingswordgive <Player Name>");
            return true;
        }
        Player TargetPlayer = Bukkit.getPlayerExact(args[0]);
        utilityMain.GivePlayerItem(TargetPlayer, customItemsMain.KingsSword(), 1);
        sender.sendMessage(ChatColor.GREEN + args[0] + " has received a King's Sword");
        return true;
    }
}
