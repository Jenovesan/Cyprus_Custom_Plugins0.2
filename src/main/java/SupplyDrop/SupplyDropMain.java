package SupplyDrop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.Random;

public class SupplyDropMain {

    Plugin ThisPlugin;
    public SupplyDropMain(Plugin plugin) {
        ThisPlugin = plugin;
    }

    public void SummonSupplyDrop() {
        int Xcord = RandomCoords();
        int Zcord = RandomCoords();
        Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.RED + "     " + ChatColor.UNDERLINE + "Supply Drop Falling At" + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.RESET + ChatColor.RED + "                  X: " + Xcord + "\n" + "                  Z: " + Zcord + "\n" + ChatColor.COLOR_CHAR + "." + "\n" + ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "---------------------");
        CheckIfSupplyDropIsOnGround(Xcord, Zcord);
    }


    private int RandomCoords() {
        Random random = new Random();
        return (random.nextInt(100) + 340);
    }

    private World getWorld() {
        try {
            return Bukkit.getWorld("world");
        }
        catch(Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage("Could not find world");
            return Bukkit.getWorld("world"); //Do not rename!
        }
    }

    int y = 250;
    private void CheckIfSupplyDropIsOnGround(int Xloc, int Zloc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                y--;
                Location location = new Location(getWorld(), Xloc, y, Zloc);
                getWorld().loadChunk(location.getChunk());
                if (!(location.getBlock().getType().equals(Material.AIR))) {
                    CreateSupplyDropChest(location.add(0,1,0));
                    this.cancel();
                }
                else {
                    location.getBlock().setType(Material.CHEST);
                }
                location.add(0,1,0).getBlock().setType(Material.AIR);
                SpawnFireWorks(location.add(.5,0,.5));
            }
        }.runTaskTimer(ThisPlugin, 0, 10);
    }

    private void CreateSupplyDropChest(Location loc) {
        getWorld().playEffect(loc, Effect.EXPLOSION_HUGE, 5);
        loc.getWorld().playSound(loc, Sound.EXPLODE, 1,1 );
        loc.getBlock().setType(Material.CHEST);
    }

    private void SpawnFireWorks(Location loc) {
        Location NewLoc = loc.subtract(0,5,0);
        Firework firework = getWorld().spawn(NewLoc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        fireworkMeta.addEffect(builder.flicker(false).withColor(Color.RED).build());
        fireworkMeta.addEffect(builder.trail(false).build());
        fireworkMeta.addEffect(builder.withFade(Color.ORANGE).build());
        fireworkMeta.addEffect(builder.with(FireworkEffect.Type.BURST).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
    }
}
