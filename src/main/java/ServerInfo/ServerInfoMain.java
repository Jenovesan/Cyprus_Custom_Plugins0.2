package ServerInfo;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.CommandExecute;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.lang.String;

public class ServerInfoMain extends CommandExecute implements Listener, CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("website")) {
            LoadGui(player, "Website", ChatColor.RED + "");
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("Discord")) {
            LoadGui(player, "Discord", ChatColor.RED + "");
            return true;
        }
        return false;
    }

    private void LoadGui(Player player, String GuiName, String Color) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 27, Color + ChatColor.BOLD + GuiName);
        FillGui(inv);
        inv.setItem(13, Paper(Color + ChatColor.BOLD + GuiName));
        player.openInventory(inv);
    }

    ItemStack Paper(String GuiName) {
        ItemStack paper = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = paper.getItemMeta();
        List<String> lore = new ArrayList<>();
        String StrippedName = ChatColor.stripColor(GuiName);
        meta.setDisplayName(GuiName);
        lore.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Click to send the " + StrippedName + " link in chat");
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);
        paper.setItemMeta(meta);
        return paper;
    }

    private void FillGui(Inventory inv) {
        ItemStack Fill = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemStack Red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Fill);
        }
        inv.setItem(0, Red);
        inv.setItem(10, Red);
        inv.setItem(18, Red);
        inv.setItem(8, Red);
        inv.setItem(16, Red);
        inv.setItem(26, Red);
        inv.setItem(4, Red);
        inv.setItem(12, Red);
        inv.setItem(22, Red);
        inv.setItem(14, Red);
    }

    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getSlot() == -999) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        String GuiName = event.getClickedInventory().getName();
        if (GuiName.equals(ChatColor.RED + "" + ChatColor.BOLD + "Website") || GuiName.equals(ChatColor.RED + "" + ChatColor.BOLD + "Discord")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().equals(Material.PAPER)) {
                sendPacket(GuiName, player);
                player.closeInventory();
            }
        }
    }

    private void sendPacket(String GuiName, Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        if (GuiName.equals(ChatColor.RED + "" + ChatColor.BOLD + "Website")) {
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Website ➵ www.CyprusMC.com\",\"bold\":true,\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.cyprusmc.com/\"}}"));
            connection.sendPacket(packet);
        }
        else if (GuiName.equals(ChatColor.RED + "" + ChatColor.BOLD + "Discord")) {
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Discord ➵ discord.gg/rRHYGbB\",\"bold\":true,\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.gg/rRHYGbB\"}}"));
            connection.sendPacket(packet);
        }
    }
}

