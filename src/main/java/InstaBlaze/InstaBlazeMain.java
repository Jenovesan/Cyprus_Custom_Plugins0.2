package InstaBlaze;

import Main.MainClass;
import Mobcoins.MobcoinsMain;
import XpBoost.XpBoostEvents;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class InstaBlazeMain implements Listener {
    MainClass mainClass;
    XpBoostEvents xpBoostEvents;
    MobcoinsMain mobcoinsMain;
    public InstaBlazeMain(MainClass mc) {
        mainClass = mc;
        xpBoostEvents = new XpBoostEvents(mainClass);
        mobcoinsMain = new MobcoinsMain(mainClass);
    }

    @EventHandler
    private void entityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Blaze && event.getDamager() instanceof Player)) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getDamager();
        Entity blaze  = event.getEntity();
        xpBoostEvents.LoadXpBoostHashMapsInConfig();
        int FinalExpAmount = 10;
        if (!xpBoostEvents.doesNotHaveAnExpBoostCurrently(player)) {
            FinalExpAmount = (int) (FinalExpAmount + ((double) FinalExpAmount / 100 * xpBoostEvents.XpBoostPerc.get(player.getUniqueId())));
        }
        if (isHoldingExpSword(player)) {
            FinalExpAmount = (int) (FinalExpAmount + ((double) FinalExpAmount / 100 * 15));
        }
        player.giveExp(FinalExpAmount);
        if (blaze.getCustomName() != null) {
            String name = blaze.getCustomName();
            String[] SplitSting = name.split("X");
            int Amount = ((Integer.parseInt(ChatColor.stripColor(SplitSting[0]))) - 1);
            if (Amount < 2) {
                blaze.setCustomName(null);
            }
            else {
                blaze.setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + Amount + "X " + ChatColor.GOLD + "Blaze");
            }
        }
        else {
            blaze.remove();
        }
        mobcoinsMain.GiveMobCoin(player, 10);
    }

    private boolean isHoldingExpSword(Player player) {
        ItemStack itemStack = player.getItemInHand();
        return itemStack != null && itemStack.getType().equals(Material.DIAMOND_SWORD) && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equals(ChatColor.RED + "" + ChatColor.BOLD + "EXP Sword" + ChatColor.GRAY + " +15%");
    }
}
