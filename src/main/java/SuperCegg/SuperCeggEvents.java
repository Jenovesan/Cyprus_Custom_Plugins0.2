package SuperCegg;

import CustomItems.CustomItemsMain;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreeper;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SuperCeggEvents implements Listener {
    CustomItemsMain customItemsMain = new CustomItemsMain();
    @EventHandler
    private void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack subtract = player.getItemInHand();
        ItemStack checkStack = new ItemStack(player.getItemInHand());
        checkStack.setAmount(1);
        if(checkStack.equals(customItemsMain.SuperCegg())) {
            if(!event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                event.setCancelled(true);
                return;
            }
            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setCustomName("cegg");
            if(subtract.getAmount() == 1) {
                player.setItemInHand(null);
            } else {
                subtract.setAmount(subtract.getAmount() - 1);
            }
        }
    }

    @EventHandler
    private void snowballHit(ProjectileHitEvent event) {
        Projectile snowBall = event.getEntity();
        if (!(snowBall instanceof Snowball)) {
            return;
        }
        if (snowBall.getCustomName() == null) {
            return;
        }
        if (!(snowBall.getCustomName().equals("cegg"))) {
            return;
        }
        CraftCreeper creeper = event.getEntity().getWorld().spawn(event.getEntity().getLocation(), CraftCreeper.class);
        creeper.setPowered(true);
        Entity nms = ((CraftEntity) creeper).getHandle();
        NBTTagCompound nbttag = new NBTTagCompound();
        nms.c(nbttag);
        nbttag.setInt("Fuse", 0);
        EntityLiving livingCreeper = (EntityLiving) nms;
        livingCreeper.a(nbttag);
    }
}
