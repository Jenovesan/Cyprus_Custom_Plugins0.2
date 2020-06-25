package Staff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffMain {

    List<String> StaffGroups = new ArrayList<>(Arrays.asList("mod", "seniormod"));
    public boolean isStaff(Player player) {
        if (player.hasPermission("cyprus.staff")) {
            return true;
        }
        for (String group : StaffGroups) {
            if (player.hasPermission("group." + group)) {
                return true;
            }
        }
        return false;
    }

    public List<Player> getOnlineStaff() {
        List<Player> OnlineStaff = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isStaff(player)) {
                OnlineStaff.add(player);
            }
        }
        return OnlineStaff;
    }

    public void SendStaffMessage(String message) {
        for (Player player : getOnlineStaff()) {
            player.sendMessage(message);
        }
    }
}
