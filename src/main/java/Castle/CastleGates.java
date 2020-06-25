package Castle;

import Main.MainClass;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CastleGates implements Listener {
    MainClass mainClass;
    CastleEvent castleEvent;
    public CastleGates(MainClass mc) {
        mainClass = mc;
        castleEvent = new CastleEvent(mainClass);
    }

    List<Material> BreakableBlocks = new ArrayList<>(Arrays.asList(Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK));
    @EventHandler
    private void blockBreak (BlockBreakEvent event) {
        if (!(castleEvent.isHappening())) {
            return;
        }
        Block block = event.getBlock();
        Material type = block.getType();
        if (BreakableBlocks.contains(type)) {
            event.setCancelled(true);
            GetBlockData(block);
        }
    }

    private void AddGateArmorStand() {
        int GateNumber = mainClass.getConfig().getInt("GateNumber");
        int GateHealth = mainClass.getConfig().getInt("GateHealth");
        int MaxGateHealth = 0;
        Location location = null;
        String Color = ChatColor.GREEN + "";
        String WorldName = "world";
        if (GateNumber == 1) {
            location = new Location(Bukkit.getWorld("world"), 500.5, 97, -90.75);
            MaxGateHealth = 25;
        }
        if (GateHealth < (MaxGateHealth * .6)) {
            if (GateHealth < (MaxGateHealth * .3))  {
                Color = ChatColor.RED + "";
            }
            else {
                Color = ChatColor.YELLOW + "";
            }
        }
        ArmorStand as1 = Bukkit.getWorld(WorldName).spawn(location, ArmorStand.class);
        as1.setGravity(false);
        as1.setCanPickupItems(false);
        as1.setCustomName(Color + ChatColor.BOLD + GateHealth + "/" + MaxGateHealth);
        as1.setCustomNameVisible(true);
        as1.setVisible(false);

    }

    public void RemoveGateArmorStands() {
        for (Entity entity : Bukkit.getWorld("world").getEntities()) {
            if (entity instanceof ArmorStand) {
                if (entity.getLocation().equals(new Location(Bukkit.getWorld("world"), 500.5, 97, -90.75))) {
                    entity.remove();
                }
            }
        }
    }

    Location Gate1BlockLocation = new Location(Bukkit.getWorld("world"), 500, 96, -91);
    private void GetBlockData(Block block) {
        Material type = block.getType();
        int GateNumber = mainClass.getConfig().getInt("GateNumber");
        if (GateNumber == 1 && type.equals(Material.IRON_BLOCK) && block.getLocation().equals(Gate1BlockLocation)) {
            Bukkit.getPlayerExact("Jenovesan").sendMessage("Got here");
            Bukkit.getServer().broadcastMessage("Broke gate block");
            BrokeGateBlock(block);
            block.getLocation().getBlock().setType(type);
        }
    }

    private void BrokeGateBlock(Block block) {
        int GateHealth = mainClass.getConfig().getInt("GateHealth");
        GateHealth = (GateHealth - 1);
        mainClass.getConfig().set("GateHealth", GateHealth);
        mainClass.saveConfig();
        RemoveGateArmorStands();
        AddGateArmorStand();
        if (GateHealth <= 0) {
            BrokeGate();
        }
        block.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOOD, 10, 10);
        for (int i = 0; i < 15; i++) {
            block.getWorld().playEffect(block.getLocation(), Effect.MAGIC_CRIT, 1);
        }
    }

    private void BrokeGate() {
        int GateNumber = mainClass.getConfig().getInt("GateNumber");
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "----------------------------" + "\n" + ChatColor.COLOR_CHAR + "." + ChatColor.RED + "Gate " + ChatColor.DARK_RED + "" + ChatColor.BOLD + GateNumber + ChatColor.RED + "  has been broken!" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "----------------------------");
        if (GateNumber == 1) {
            BreakGateOne();
        }
    }

    private void BreakGateOne() {
        Location location = new Location(Bukkit.getWorld("world"), 498, 95, -91);
        for (int a = 0; a < 5; a++) {
            for (int b = 0; b < 6; b++) {
                Location NewLoc = new Location(location.getWorld(), location.getX() + a, location.getY() + b, 0);
                NewLoc.getBlock().setType(Material.AIR);
            }
        }
    }
}