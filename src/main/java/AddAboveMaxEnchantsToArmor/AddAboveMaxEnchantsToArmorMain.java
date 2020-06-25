package AddAboveMaxEnchantsToArmor;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.ItemArmor;
import net.minecraft.server.v1_8_R3.ItemBow;
import net.minecraft.server.v1_8_R3.ItemSword;
import net.minecraft.server.v1_8_R3.ItemTool;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

public class AddAboveMaxEnchantsToArmorMain implements Listener {

    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        if (event.getSlot() == -999) {
            return;
        }
        if (event.getCurrentItem() == null || event.getCursor() == null || event.getCurrentItem().getType().equals(Material.AIR) || event.getCurrentItem().getType().equals(Material.AIR) || event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
            return;
        }
        ItemStack CursorItem = event.getCursor();
        ItemStack CurrentItem = event.getCurrentItem();
        if (!(CursorItem.getType().equals(Material.ENCHANTED_BOOK))) {
            return;
        }
        if (!(CursorItem.hasItemMeta())) {
            return;
        }
        if (!(CursorItem.getItemMeta().hasLore())) {
            return;
        }
        if (!(CraftItemStack.asNMSCopy(CurrentItem).getItem() instanceof ItemTool ||  CraftItemStack.asNMSCopy(CurrentItem).getItem() instanceof ItemArmor || CraftItemStack.asNMSCopy(CurrentItem).getItem() instanceof ItemSword || CraftItemStack.asNMSCopy(CurrentItem).getItem() instanceof ItemBow)) {
            event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot apply that enchant to the gear");
            return;
        }
        for (String lore : CursorItem.getItemMeta().getLore()) {
            if (lore.equals(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Drag and drop on a piece of gear")) {
                ApplyEnchant(CurrentItem, CursorItem, (Player) event.getWhoClicked(), event.getSlot());
                event.setCancelled(true);
                event.setCursor(null);
                return;
            }
        }
    }

    private void ApplyEnchant(ItemStack armor, ItemStack book, Player player, int slot) {
        ItemStack gear = new ItemStack(armor);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        Map<Enchantment, Integer> enchants = meta.getStoredEnchants();
        gear.addUnsafeEnchantments(enchants);
        gear.setItemMeta(meta);
        player.getInventory().setItem(slot, gear);
        player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
    }
}
