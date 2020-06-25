package Roam;

import Main.MainClass;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RoamMain extends CommandExecute implements Listener, CommandExecutor {
    Integer MaxRoamDistance = 80;
    MainClass mainClass;
    public RoamMain(MainClass mc) {
        mainClass = mc;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player) || !canRoam(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot roam. Purchase Titan Rank to use it");
            return true;
        }
        Player player = (Player) sender;
        if (!isInRoam(player)) {
            if (canRoam(sender)) {
                Roam((Player) sender);
                sender.sendMessage(ChatColor.GREEN + "You are now roaming");
            } else {
                sender.sendMessage(ChatColor.RED + "You cannot roam");
            }
        } else {
            TeleportPlayerToVillager(player);
            RoamedPlayers.remove(player);
            LastLocation.remove(player);
            PlayersWithVillager.remove(player);
        }
        return true;
    }

    private boolean canRoam(CommandSender sender) {  return sender.hasPermission("cyprus.roam") && !Board.getInstance().getFactionAt(new FLocation(((Player) sender).getLocation())).isWarZone();}

    HashMap<Player, Location> RoamedPlayers = new HashMap<>();
    HashMap<Player, Villager> PlayersWithVillager = new HashMap<>();
    public boolean isInRoam(Player player) {return RoamedPlayers.containsKey(player);}

    private void Roam(Player player) {
        SpawnVillager(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    private void PlayerHitsVillager(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Villager)) {
            return;
        }
        Villager villager = (Villager) event.getEntity();
        if (villager.getCustomName() != null && getPlayerFromVillager(villager) != null) {
            Player RoamedPlayer = getPlayerFromVillager(villager);
            if (FPlayers.getInstance().getByPlayer((Player) event.getDamager()).getFaction().equals(FPlayers.getInstance().getByPlayer(RoamedPlayer).getFaction())) {
                event.setCancelled(true);
                return;
            }
            TeleportPlayerToVillager(RoamedPlayer);
            assert RoamedPlayer != null;
            RoamedPlayer.damage(event.getDamage());
            RoamedPlayers.remove(RoamedPlayer);
            LastLocation.remove(RoamedPlayer);
            PlayersWithVillager.remove(RoamedPlayer);
            if (!RoamedPlayer.isOp()) {
                mainClass.combatTimerMain.damageTracking.put(RoamedPlayer.getUniqueId(), mainClass.combatTimerMain.CombatTimeInSeconds);
            }
        }
    }

    public void RemoveAllVillagers() {
        for (Map.Entry<Player, Villager> element : PlayersWithVillager.entrySet()) {
            element.getValue().remove();
        }
    }

    private Player getPlayerFromVillager(Villager villager) {
        if (PlayersWithVillager.containsValue(villager)) {
            for (Map.Entry<Player, Villager> element : PlayersWithVillager.entrySet()) {
                if (element.getValue().equals(villager)) {
                    return element.getKey();
                }
            }
        }
        return null;
    }

    @EventHandler
    private void PlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(player.getLocation().getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    private void PlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TeleportPlayerToVillager(player);
        RoamedPlayers.remove(player);
        LastLocation.remove(player);
        PlayersWithVillager.remove(player);
    }



    @EventHandler(priority = EventPriority.LOW)
    private void CancelCommand(PlayerCommandPreprocessEvent event) {
        String name = event.getMessage();
        Player player = event.getPlayer();
        if (!name.equalsIgnoreCase("/roam") && isInRoam(player)) {
            player.sendMessage(ChatColor.RED + "You cannot use commands while roaming");
            event.setCancelled(true);
        }
    }

    private void TeleportPlayerToVillager(Player player) {
        if (hasAVillager(player)) {
            Entity villager = getVillager(player);
            player.teleport(villager);
            player.setGameMode(GameMode.SURVIVAL);
            assert villager != null;
            villager.remove();

        } else if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.teleport(player.getLocation().getWorld().getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    private boolean hasAVillager(Player player) {
        return PlayersWithVillager.containsKey(player);
    }

    private Entity getVillager(Player player) {
       return PlayersWithVillager.get(player);
    }

    private void SpawnVillager(Player player) {
        Location location = player.getLocation();
        String name = player.getDisplayName();
        LivingEntity villager = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) villager).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        nmsEntity.c(tag);
        tag.setBoolean("NoAI", true);
        tag.setBoolean("Silent", true);
        nmsEntity.f(tag);
        RoamedPlayers.put(player, villager.getLocation());
        PlayersWithVillager.put(player, (Villager) villager);
    }

    HashMap<Player, Location> LastLocation = new HashMap<>();
    public void RoamDistanceManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Location> element : RoamedPlayers.entrySet()) {
                    Player player = element.getKey();
                    Location OriginalLocation = element.getValue();
                    Location CurrentLociation = player.getLocation();
                    int distance = (int) CurrentLociation.distance(OriginalLocation);
                    if (distance < MaxRoamDistance) {
                        SendDistanceBarMessage(player, getDistanceBar(distance));
                        LastLocation.put(player, CurrentLociation);
                    } else {
                        player.teleport(LastLocation.get(player));
                        SendExceededMaxDistanceMessage(player);
                    }
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 10);
    }

    private String getDistanceBar(Integer distance) {
        List<String> ExpBarList = new ArrayList<String>();
        for (int i = 0; i < MaxRoamDistance / 2; i++) {
            if (i < distance / 2) {
                ExpBarList.add(ChatColor.RED + "|");
            }
            else {
                ExpBarList.add(ChatColor.GRAY + "|");
            }
        }
        return String.join("", ExpBarList);
    }

    private void SendDistanceBarMessage(Player player, String bar) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + bar + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }

    private void SendExceededMaxDistanceMessage(Player player) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.RED + "You have exceeded the max roam distance" + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }
}
