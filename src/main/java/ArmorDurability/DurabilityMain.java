package ArmorDurability;

import Utility.UtilityMain;
import net.minecraft.server.v1_8_R3.ItemAxe;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class DurabilityMain implements Listener {
    UtilityMain utilityMain = new UtilityMain();
    @EventHandler
    private void RemoveDurability(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof  Player)) {
            return;
        }
        Player damagee = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!damager.getItemInHand().getType().equals(Material.AIR) && CraftItemStack.asNMSCopy(damager.getItemInHand()).getItem() instanceof ItemAxe) {
            RemoveArmorDurability(damagee, true);
            return;
        }
        RemoveArmorDurability(damagee, false);
    }

    private void RemoveArmorDurability(Player player, Boolean UsingAxe) {
        int SubtractBecauseOfAxe = 0;
        if (UsingAxe) {
            SubtractBecauseOfAxe = 1;
        }
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (!armor.getType().equals(Material.AIR)) {
                continue;
            }
            int UnbreakingLevel = armor.getEnchantmentLevel(Enchantment.DURABILITY);
            for (int i = 0; i < 2; i++) {
                if (utilityMain.RandomNumberBoolean(10, (6 + SubtractBecauseOfAxe) - UnbreakingLevel)) {
                    armor.setDurability((short) (armor.getDurability() - 3));
                }
            }
        }
    }
}
