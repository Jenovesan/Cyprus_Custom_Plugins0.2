package WbAndNv;

import Main.MainClass;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class WbAndNvMain extends CommandExecute implements Listener, CommandExecutor {
    MainClass mainClass;
    public WbAndNvMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        UUID uuid = ((Player) sender).getUniqueId();
        if (cmd.getName().equalsIgnoreCase("nv") && sender.hasPermission("nightvision.use")) {
            if (PlayersWithNightVisionEnabled.contains(uuid)) {
                PlayersWithNightVisionEnabled.remove(uuid);
                UsedEffectCommand(PotionEffectType.NIGHT_VISION, (Player) sender, false);
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "(" + ChatColor.RED + "" + ChatColor.BOLD + "!" + ChatColor.GRAY + "" + ChatColor.BOLD + ")" + ChatColor.GRAY + "You have disabled permament " + ChatColor.RED + "Night Vision");
            } else {
                PlayersWithNightVisionEnabled.add(uuid);
                UsedEffectCommand(PotionEffectType.NIGHT_VISION, (Player) sender, true);
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "(" + ChatColor.RED + "" + ChatColor.BOLD + "!" + ChatColor.GRAY + "" + ChatColor.BOLD + ")" + ChatColor.GRAY + "You have enabled permament " + ChatColor.RED + "Night Vision");
            }
            SavePlayersWithEffectsToConfig();
        } else if (cmd.getName().equalsIgnoreCase("wb") && sender.hasPermission("wb.use")) {
            if (PlayersWithWaterBreathingEnabled.contains(uuid)) {
                PlayersWithWaterBreathingEnabled.remove(uuid);
                UsedEffectCommand(PotionEffectType.WATER_BREATHING, (Player) sender, false);
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "(" + ChatColor.RED + "" + ChatColor.BOLD + "!" + ChatColor.GRAY + "" + ChatColor.BOLD + ")" + ChatColor.GRAY + "You have disabled permament " + ChatColor.RED + "Water Breathing");
            } else {
                PlayersWithWaterBreathingEnabled.add(uuid);
                UsedEffectCommand(PotionEffectType.WATER_BREATHING, (Player) sender, true);
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "(" + ChatColor.RED + "" + ChatColor.BOLD + "!" + ChatColor.GRAY + "" + ChatColor.BOLD + ")" + ChatColor.GRAY + "You have enabled permament " + ChatColor.RED + "Water Breathing");
            }
            SavePlayersWithEffectsToConfig();
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
        }
        return true;
    }

    Set<UUID> PlayersWithNightVisionEnabled = new HashSet<>();
    Set<UUID> PlayersWithWaterBreathingEnabled = new HashSet<>();

    public boolean hasPermaWb(Player player) {
        return PlayersWithWaterBreathingEnabled.contains(player.getUniqueId());
    }

    public boolean hasPermaNv(Player player) {
        return PlayersWithNightVisionEnabled.contains(player.getUniqueId());
    }

    public boolean HasPermaNv(Player player) {
        return PlayersWithNightVisionEnabled.contains(player.getUniqueId());
    }

    private void UsedEffectCommand(PotionEffectType effectType, Player player, boolean enable) {
        if (enable) {
            player.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, 0));
        } else {
            player.removePotionEffect(effectType);
        }
    }

    private void SavePlayersWithEffectsToConfig() {
        List<String> nvuuids = new ArrayList<>();
        for (UUID uuid : PlayersWithNightVisionEnabled) {
            nvuuids.add(uuid.toString());
        }
        mainClass.getConfig().set("nv", nvuuids);
        List<String> wbuuids = new ArrayList<>();
        for (UUID uuid : PlayersWithWaterBreathingEnabled) {
            wbuuids.add(uuid.toString());
        }
        mainClass.getConfig().set("wb", wbuuids);
        mainClass.saveConfig();
    }

    public void LoadPlayersWithEffectsFromConfig() {
        List<String> nvstrings = mainClass.getConfig().getStringList("nv");
        for (String string : nvstrings) {
            PlayersWithNightVisionEnabled.add(UUID.fromString(string));
        }
        List<String> wbstrings = mainClass.getConfig().getStringList("wb");
        for (String string : wbstrings) {
            PlayersWithWaterBreathingEnabled.add(UUID.fromString(string));
        }
    }
}
