package SuperCegg;

import CustomItems.CustomItemsMain;
import Utility.UtilityMain;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class SuperCeggCommands extends CommandExecute implements Listener, CommandExecutor {
    CustomItemsMain customItemsMain = new CustomItemsMain();
    UtilityMain utilityMain = new UtilityMain();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayerExact(args[0]);
        if (!sender.isOp()) {
            return false;
        }
        utilityMain.GivePlayerItem(player, customItemsMain.SuperCegg(), Integer.parseInt(args[1]));
        return true;
    }
}