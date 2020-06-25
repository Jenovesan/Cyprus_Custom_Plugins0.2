package CustomEnchants;

import Main.MainClass;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.massivecore.particleeffect.ParticleEffect;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.ItemArmor;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftThrownPotion;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import Utility.UtilityMain;

import java.util.*;

public class CustomEnchantsEvents implements Listener {
    MainClass mainClass;
    public CustomEnchantsEvents(MainClass mc) {
        mainClass = mc;
    }
    UtilityMain utilityMain = new UtilityMain();

    List<Material> GuiBlocks = new ArrayList<>(Arrays.asList(Material.CHEST, Material.ENDER_CHEST, Material.STORAGE_MINECART, Material.TRAPPED_CHEST, Material.WORKBENCH, Material.FURNACE, Material.BURNING_FURNACE, Material.DISPENSER, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BREWING_STAND, Material.BEACON, Material.ANVIL, Material.HOPPER, Material.HOPPER_MINECART));
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { //Adding Ce's
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Block block = event.getClickedBlock();
                if (GuiBlocks.contains(block.getType())) {
                    return;
                }
            }
            ItemStack ItemInHand = player.getItemInHand();
            if (ItemInHand.hasItemMeta()) {
                if (ItemInHand.getItemMeta().hasLore()) {
                    AddPermanentPotionEffectsIncludingCurrentItem(player, ItemInHand, null);
                }
            }
        }
        if (event.getClickedBlock() != null) {
            if (GetMainHandLores(player).contains("Obsidian Breaker")) {
                ObsidianBreaker(player, event.getClickedBlock());
            }
        }
        if (GetMainHandLores(player).contains("Haste")) {
            Haste(player);
        }
        else {
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        }
    }

    @EventHandler
    private void playerItemBreak(PlayerItemBreakEvent event) { //Remove Ce's after armor breaks
        Player player = event.getPlayer();
        RemovePotionEffects(player, true);
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (!armor.equals(event.getBrokenItem())) {
                if (armor.hasItemMeta()) {
                    if (armor.getItemMeta().hasLore()) {
                        for (String lore : armor.getItemMeta().getLore()) {
                            AddPermamentPotionEffects(ChatColor.stripColor(lore), player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void closeInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (GetMainHandLores(player).contains("Splitter")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1));
        }
        if (GetMainHandLores(player).contains("Haste")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        }
    }

    @EventHandler
    private void inInventoryClick(InventoryClickEvent event) { //Adding Ce's
        Player player = (Player) event.getWhoClicked();
        if (!event.getClick().equals(ClickType.LEFT) && !event.getClick().equals(ClickType.SHIFT_LEFT)) {
            return;
        }
        if (!event.getInventory().getType().equals(InventoryType.CRAFTING)) {
            return;
        }
        if (event.getSlot() == -999) {
            return;
        }
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            RemovePotionEffects(player, true);
            if (event.getCursor() == null) {
                return;
            }
            if (event.getCursor().hasItemMeta() && EquipedRightArmor(event.getCursor(), event.getSlot())) {
                AddPermanentPotionEffectsIncludingCurrentItem(player, event.getCursor(), event.getCurrentItem());
            }
            else {
                AddPermanentPotionEffectsExcludingCursorItem(player, event.getCurrentItem());
            }
        }
        else if (event.isShiftClick()) {
            if (event.getCurrentItem() != null) {
                ItemStack CurrentItem = event.getCurrentItem();
                if (CraftItemStack.asNMSCopy(CurrentItem).getItem() instanceof ItemArmor) {
                    AddPermanentPotionEffectsIncludingCurrentItem(player, event.getCurrentItem(), null);
                }
            }
        }
        else {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.WEAKNESS) || effect.getType().equals(PotionEffectType.FAST_DIGGING)) {
                    player.removePotionEffect(PotionEffectType.WEAKNESS);
                    player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                    AddPermanentPotionEffectsExcludingCursorItem(player, null);
                }
            }
        }
        if (mainClass.wbAndNvMain.hasPermaNv(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, (short) 0));
        }
        if (mainClass.wbAndNvMain.hasPermaWb(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, (short) 0));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void playerConsumption(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        if (player.getItemInHand().getType().equals(Material.GOLDEN_APPLE)) { //Gapple can be found in GoldenApplesCooldownMain
            int Duration = 100;
            player.removePotionEffect(PotionEffectType.REGENERATION);
            if (player.getItemInHand().getDurability() == 0) { //For golden apples
                if (GetArmorLores(player).contains("Crapple")) {
                    Duration = 160;
                    PotionEffect EffectToAdd = new PotionEffect(PotionEffectType.REGENERATION, Duration, (short) 1);
                    player.addPotionEffect(EffectToAdd);
                }
            }
            else { //For god apples
                Duration = 600;
            }
            if (GetArmorLores(player).contains("Regeneration")) {
                ResetEffectsAfterDelay(Duration, player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void entityHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof  Player) {
            Player enemy = (Player) event.getEntity();
            if (event.getDamager() instanceof  Arrow) { //If shot by arrow
                Arrow arrow = (Arrow) event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Player shooter = (Player) arrow.getShooter();
                    if (shooter.getItemInHand().getType().equals(Material.BOW)) {
                        if (GetMainHandLores(shooter).contains("Piercing")) {
                            event.setDamage((event.getDamage() * 3));
                        }
                        if (GetMainHandLores(shooter).contains("Explosive Shot")) {
                            ExplosiveShot(arrow);
                        }
                        if (GetMainHandLores(shooter).contains("Toxin")) {
                            Toxin(enemy, shooter);
                        }
                        if (GetMainHandLores(shooter).contains("Longshot")) {
                            int Distance = (int) enemy.getLocation().distance(shooter.getLocation());
                            event.setDamage(event.getDamage() + ((event.getDamage() / 50) * Distance));
                            shooter.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Longshot>> " + ChatColor.RED + "You shot " + enemy.getName() + " from " + Distance + " blocks away");
                        }
                    }
                }
            }
            else if (event.getDamager() instanceof  Player){
                Player damager = (Player) event.getDamager();
                if (GetMainHandLores(damager).contains("Frostbite")) {
                    Frostbite(enemy, damager);
                }
                if (GetMainHandLores(damager).contains("Starvation")) {
                    Starvation(enemy, damager);
                }
                if (GetArmorLores(enemy).contains("Second Life")) { //Not really the enemy
                    SecondLife(enemy);
                }
                if (GetMainHandLores(damager).contains("Cripple")) {
                    Cripple(enemy, damager);
                }
                if (GetMainHandLores(damager).contains("Backstab")) {
                    if (Math.abs(enemy.getLocation().getDirection().angle(damager.getLocation().getDirection())) < .46) {
                        event.setDamage(event.getDamage() + (event.getDamage() / 4));
                        for (int i = 0; i < 10; i++) {
                            enemy.getWorld().playEffect(enemy.getEyeLocation(), Effect.MAGIC_CRIT, 1);
                            enemy.getWorld().playEffect(enemy.getLocation(), Effect.MAGIC_CRIT, 1);
                        }
                        enemy.getWorld().playSound(enemy.getLocation(), Sound.SKELETON_HURT, 10, 1);
                    }
                }
                if (GetMainHandLores(damager).contains("Reaper")) {
                    if (((int) enemy.getHealth()) <= 8) {
                        event.setDamage(event.getDamage() + (event.getDamage() / 5));
                        for (int i = 0; i < 5; i++) {
                            enemy.getWorld().playEffect(enemy.getEyeLocation(), Effect.WITCH_MAGIC, 5);
                            enemy.getWorld().playEffect(enemy.getLocation(), Effect.WITCH_MAGIC, 5);
                        }
                    }
                }
                if (GetMainHandLores(damager).contains("Lifesteal")) {
                    damager.setHealth(damager.getHealth() + (event.getFinalDamage() / 10)); //was 50
                }
                if (GetArmorLores(enemy).contains("Reinforced")) {
                    Reinforced(enemy);
                }
                if (GetArmorLores(enemy).contains("Last Stand")) {
                    int FinalDamage = (int) event.getFinalDamage();
                    LastStand(enemy, FinalDamage, 0);
                }
                if (GetMainHandLores(damager).contains("Splitter")) {
                    Splitter(enemy, damager);
                }
                if (GetArmorLores(enemy).contains("Riposte")) {
                    Riposte(damager, enemy, (int) event.getFinalDamage());
                }
                if (GetMainHandLores(damager).contains("Bounty Hunter")) {
                    BountyHunter(damager, enemy);
                }
                if (BountiedPlayers.containsKey(enemy)) {
                    event.setDamage((event.getDamage() + (event.getDamage() / 5)));
                }
                if (GetArmorLores(damager).contains("Tank")) {
                    event.setDamage(event.getDamage() * .7);
                }
                if (GetArmorLores(enemy).contains("Demise")) {
                    Demise(enemy);
                }
                if (GetMainHandLores(damager).contains("Swap")) {
                    Swap(enemy);
                }
                if (GetMainHandLores(damager).contains("Combo")) {
                    if (ComboPlayer.containsKey(damager)) {
                        for (Map.Entry mapElement : ComboPlayer.entrySet()) {
                            Player Combod = (Player) mapElement.getValue();
                            if (Combod.equals(enemy)) {
                                ComboInt.replace(damager, (ComboInt.get(damager) + 1));
                                event.setDamage(event.getDamage() + ((event.getDamage() / 33) * (ComboInt.get(damager))));
                            }
                            else {
                                mapElement.setValue(enemy);
                                ComboInt.replace(damager, 1);
                            }
                        }
                    }
                    else {
                        ComboPlayer.put(damager, enemy);
                        ComboInt.put(damager, 1);
                    }
                    if (ComboInt.get(damager) > 2) {
                        damager.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Combo>> " + ChatColor.YELLOW + "" + ComboInt.get(damager) + " hit combo");
                    }
                }
                else {
                    if (ComboPlayer.containsKey(damager)) {
                        ComboPlayer.remove(damager);
                    }
                    if (ComboInt.containsKey(damager)) {
                        ComboInt.remove(damager);
                    }
                }
                if (ComboPlayer.containsKey(enemy) && ComboPlayer.get(enemy).equals(damager)) {
                    ComboPlayer.remove(enemy);
                    if (ComboInt.containsKey(enemy)) {
                        ComboInt.remove(enemy);
                    }
                }
            }
        }

    }

    static HashMap<Player, Player> ComboPlayer = new HashMap<Player, Player>();
    static HashMap<Player, Integer> ComboInt = new HashMap<Player, Integer>();

    @EventHandler
    private void playerDeath(PlayerDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }
        Player killer = event.getEntity().getKiller();
        Player killed = event.getEntity();
        if (GetMainHandLores(killer).contains("Looter")) { //Looter
            Looter(killer, killed);
        }
        if (GetArmorLores(killer).contains("Reforged")) {
            Reforged(killer);
        }
    }

    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (GetArmorLores(player).contains("Patching")) {
            Patching(player);
        }
    }

    @EventHandler
    private void potionSplash(PotionSplashEvent event) {
        CraftThrownPotion potion = (CraftThrownPotion) event.getPotion();
        if (potion.getShooter() instanceof Player) {
            if (GetArmorLores((Player) potion.getShooter()).contains("Purification")) {
                Purification((Player) potion.getShooter());
            }
        }
    }

    @EventHandler
    private void playerTeleport(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            Player player = event.getPlayer();
            if (GetArmorLores(player).contains("Ethereal")) {
                Ethereal(player, event.getTo());
            }
        }
    }

    @EventHandler
    private void playerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItem(event.getNewSlot()) != null) {
            ItemStack NewItem = player.getInventory().getItem(event.getNewSlot());
            if (NewItem.hasItemMeta()) {
                if (NewItem.getItemMeta().hasLore()) {
                    if (NewItem.getItemMeta().getLore().contains(ChatColor.GRAY + "Haste")) {
                        Haste(player);
                        return;
                    }
                    if (NewItem.getItemMeta().getLore().contains(ChatColor.GREEN + "Splitter")) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1));
                    }
                }
            }
        }
        if (GetMainHandLores(player).contains("Splitter")) {
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            AddPermanentPotionEffectsExcludingCursorItem(player, null);
        }
        player.removePotionEffect(PotionEffectType.FAST_DIGGING);
    }

    @EventHandler
    private void onHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (GetArmorLores(player).contains("Last Stand")) {
                LastStand(player, 0, (int) event.getAmount());
            }
        }
    }

    @EventHandler
    private void dropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        RemovePotionEffects(player, true);
        AddPermanentPotionEffectsExcludingCursorItem(player, item);
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasLore()) {
                for (String lore : item.getItemMeta().getLore()) {
                    String StripLore = ChatColor.stripColor(lore);
                    if (StripLore.equals("Haste")) {
                        player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                        AddPermanentPotionEffectsExcludingCursorItem(player, null);
                    }
                }
            }
        }
    }

    @EventHandler
    private void pickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasLore()) {
                if (item.getItemMeta().getLore().contains(ChatColor.GRAY + "Haste")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
                }
            }
        }
    }

    @EventHandler
    private void RemoveBountyLpWhenConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "lp user " + player.getName() + " parent remove bounty");
    }

    private void Demise(Player player) {
        if (!RandomChance(2, true, player)) {
            return;
        }
        DemiseParticles(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                Faction PlayerFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
                for (Entity entity : player.getNearbyEntities(5,5,5)) {
                    if (entity instanceof Player) {
                        Player enemy = (Player) entity;
                        Faction EnemyFaction = FPlayers.getInstance().getByPlayer(enemy).getFaction();
                        if (!PlayerFaction.equals(EnemyFaction)) {
                            enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 70, 2));
                        }
                    }
                }
            }
        }.runTaskLater(mainClass, 5);
    }

    private void DemiseParticles(final Player player) {
        new BukkitRunnable() {
            double t = 0;
            Location loc = player.getLocation().subtract(0,1,0);
            @Override
            public void run() {
                t = t + 0.1*Math.PI;
                for (double theta = 0; theta <= 2*Math.PI; theta = theta + Math.PI/32) {
                    double x = t*Math.cos(theta);
                    double y = 0.25 * Math.exp(-0.1*t) * Math.sin(t) + 1.5;
                    double z = t*Math.sin(theta);
                    loc.add(x,y,z);
                    ParticleEffect.SPELL_WITCH.display(loc,0,0,0,0,1);
                    loc.subtract(x,y,z);
                } if (t > 5) {
                    this.cancel();
                }
            }
        }.runTaskTimer(mainClass.getPlugin(), 0, 1);
    }

    private void Swap(Player enemy) {
        if (utilityMain.RandomNumberBoolean(100, 98)) { //10,000
            int SwitchSlot = utilityMain.getRandom().nextInt(9);
            ItemStack mainHand = new ItemStack(enemy.getItemInHand());
            ItemStack switchSlot;
            if (enemy.getInventory().getItem(SwitchSlot) != null) {
                switchSlot = new ItemStack(enemy.getInventory().getItem(SwitchSlot));
            } else {
                switchSlot = new ItemStack(Material.AIR);
            }
            enemy.setItemInHand(switchSlot);
            enemy.getInventory().setItem(SwitchSlot, mainHand);
            enemy.sendMessage("Swapped");
        }
    }

    private boolean EquipedRightArmor(ItemStack item, Integer slot) {
        if (!(CraftItemStack.asNMSCopy(item).getItem() instanceof ItemArmor)) {
            return false;
        }
        if (item.getType().equals(Material.DIAMOND_HELMET) && slot.equals(39)) {
            return true;
        }
        if (item.getType().equals(Material.DIAMOND_CHESTPLATE) && slot.equals(38)) {
            return true;
        }
        if (item.getType().equals(Material.DIAMOND_LEGGINGS) && slot.equals(37)) {
            return true;
        }
        if (item.getType().equals(Material.DIAMOND_BOOTS) && slot.equals(36)) {
            return true;
        }
        return false;
    }

    private void Riposte(Player damager, Player enemy, int damage) {
        if (RandomChance(3, false, null)) {
            damager.setHealth(damager.getHealth() - damage);
            final Vector v = damager.getLocation().toVector().subtract(enemy.getLocation().toVector()).normalize().multiply(1).setY(.1);
            damager.setVelocity(v);
            damager.getWorld().playEffect(damager.getEyeLocation(), Effect.EXPLOSION_LARGE, 5);
            damager.getWorld().playSound(damager.getEyeLocation(), Sound.ZOMBIE_METAL, 1,1 );
        }
    }

    static HashMap<Player, Integer> SplitterTracking = new HashMap<Player, Integer>();
    private void Splitter(Player enemy, Player damager) {
        if (!SplitterTracking.containsKey(damager)) {
            SplitterTracking.put(damager, 1);
        }
        for (Map.Entry mapElement : SplitterTracking.entrySet()) {
            Player player = (Player) mapElement.getKey();
            if (player.equals(damager)) {
                int value = (int) mapElement.getValue();
                if (value >= 10) {
                    for (ItemStack armor : enemy.getInventory().getArmorContents()) {
                        if (armor != null) {
                            armor.setDurability((short) (armor.getDurability() + 1));
                        }
                    }
                    SplitterTracking.remove(damager);
                }
                else {
                    SplitterTracking.replace(damager, value + 1);
                }
            }
        }
    }


    private void LastStand(Player player, int FinalDamage, int RegenHealth) {
        if ((player.getHealth() - FinalDamage + RegenHealth) <= 8) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                    if (effect.getAmplifier() == 1) {
                        return;
                    }
                }
            }
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
            for (int i = 0; i < 5; i++) {
                player.getWorld().playEffect(player.getEyeLocation(), Effect.FLAME, 5);
                player.getWorld().playEffect(player.getLocation(), Effect.FLAME, 5);
            }
            player.getWorld().playSound(player.getLocation(), Sound.WOLF_GROWL, 1, 1);
        }
        else {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                    if (effect.getAmplifier() == 1) {
                        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        for (ItemStack armor : player.getInventory().getArmorContents()) {
                            if (armor != null) {
                                if (armor.hasItemMeta()) {
                                    if (armor.getItemMeta().hasLore()) {
                                        for (String lore : armor.getItemMeta().getLore()) {
                                            if (ChatColor.stripColor(lore).equals("Strength")) {
                                                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    HashMap<Player, Integer> BountiedPlayers = new HashMap<Player, Integer>();
    HashMap<Player, Integer> BountyCooldown = new HashMap<>();

    private void BountyHunter(Player damager, Player enemy) {
        if (!(BountyCooldown.containsKey(damager))) {
            enemy.setPlayerListName(ChatColor.YELLOW + enemy.getName());
            BountyCooldown.put(damager, 30);
            BountiedPlayers.put(enemy, 10);
            mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "lp user " + enemy.getName() + " parent add bounty");
            ActionBarMessage(enemy, "§eYou have been bountied");
        }
    }

    public void BountyTimeManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry element : BountiedPlayers.entrySet()) {
                    Player player = (Player) element.getKey();
                    int time = (int) element.getValue();
                    if (time <= 0) {
                        mainClass.getPlugin().getServer().dispatchCommand(mainClass.getPlugin().getServer().getConsoleSender(), "lp user " + player.getName() + " parent remove bounty");
                        BountiedPlayers.remove(player);
                    }
                    else {
                        element.setValue(time - 1);
                    }
                }
                for (Map.Entry element : BountyCooldown.entrySet()) {
                    Player player = (Player) element.getKey();
                    int time = (int) element.getValue();
                    if (time <= 0) {
                        BountyCooldown.remove(player);
                        ActionBarMessage(player, "§eBounty Hunter Cooldown Reset");
                    }
                    else {
                        element.setValue(time - 1);
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 20);
    }

    private void ActionBarMessage(Player player, String message) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }

    private void Ethereal(Player player, Location to) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 180, (short) 0));
        PotionEffect strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, (short) 1);
        ResetEffectsAfterDelay(strength.getDuration(), player);
        player.addPotionEffect(strength);
        for (int i = 0; i < 25; i++) {
            player.getWorld().playEffect(to, Effect.PORTAL, 5);
            player.getWorld().playEffect(to, Effect.PORTAL, 5);
        }
        player.getWorld().playSound(to, Sound.ENDERMAN_TELEPORT, 10, 1);
    }

    private void Reinforced(Player player) {
        for (ItemStack ArmorPiece : player.getInventory().getArmorContents()) {
            if (ArmorPiece.hasItemMeta()) {
                if (ArmorPiece.getItemMeta().hasLore()) {
                    if (ArmorPiece.getItemMeta().getLore().contains("Reinforced")) {
                        if (RandomChance(10, false, null)) {
                            if (ArmorPiece.getDurability() != 0) {
                                ArmorPiece.setDurability((short) (ArmorPiece.getDurability() - 1));
                            }
                        }
                    }
                }
            }
        }
    }

    private void Cripple(Player enemy, Player damager) {
        int DebuffAmplifier = 0;
        if (GetArmorLores(damager).contains("Contaminator")) {
            DebuffAmplifier = 1;
        }
        if (RandomChance(3, true, enemy)) {
            PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, 80, (short) 3 + DebuffAmplifier);
            ResetEffectsAfterDelay(weakness.getDuration(), enemy);
            enemy.addPotionEffect(weakness);
            for (int i = 0; i < 15; i++) {
                enemy.getWorld().playEffect(enemy.getEyeLocation(), Effect.CRIT, 5);
                enemy.getWorld().playEffect(enemy.getLocation(), Effect.CRIT, 5);
            }
            enemy.getWorld().playSound(enemy.getLocation(), Sound.ZOMBIE_METAL, 10, 1);
        }
    }

    private void SecondLife(Player player) {
        if ((int) player.getHealth() <= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, (short) 0));
        }
    }

    private void Reforged(Player killer) {
        for (ItemStack ArmorPiece : killer.getInventory().getArmorContents()) {
            if (ArmorPiece != null) {
                ArmorPiece.setDurability((short) 0);
                for (int i = 0; i < 5; i++) {
                    killer.getWorld().playEffect(killer.getEyeLocation(), Effect.LAVA_POP, 5);
                    killer.getWorld().playEffect(killer.getLocation(), Effect.LAVA_POP, 5);
                }
                killer.getWorld().playSound(killer.getLocation(), Sound.ANVIL_USE, 10, 1);
            }
        }
    }

    private void ObsidianBreaker(Player player, Block block) {
        if (block.getType().equals(Material.OBSIDIAN)) {
            block.setType(Material.AIR);
            player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.OBSIDIAN));
        }
    }

    private void Toxin(Player enemy, Player shooter) {
        int DebuffAmplifier = 0;
        if (GetArmorLores(shooter).contains("Contaminator")) {
            DebuffAmplifier = 1;
        }
        int Duration = 100;
        enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Duration, (short) 0 + DebuffAmplifier));
        PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, Duration, (short) 1 + DebuffAmplifier);
        ResetEffectsAfterDelay(weakness.getDuration(), enemy);
        enemy.addPotionEffect(weakness);
        ResetEffectsAfterDelay(Duration, enemy);
        enemy.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Duration, (short) 0 + DebuffAmplifier));
        for (int i = 0; i < 10; i++) {
            enemy.getWorld().playEffect(enemy.getEyeLocation(), Effect.SLIME, 5);
            enemy.getWorld().playEffect(enemy.getLocation(), Effect.SLIME, 5);
        }
        enemy.getWorld().playSound(enemy.getLocation(), Sound.ZOMBIE_INFECT, 10, 1);
    }

    private void Haste(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, (short) 1));
    }

    private void Purification(Player player) {
        if (RandomChance(25, false, null)) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.HUNGER) || effect.getType().equals(PotionEffectType.SLOW) || effect.getType().equals(PotionEffectType.BLINDNESS) || effect.getType().equals(PotionEffectType.CONFUSION) || effect.getType().equals(PotionEffectType.POISON) || effect.getType().equals(PotionEffectType.WEAKNESS) || effect.getType().equals(PotionEffectType.WITHER)) {
                    if (effect.getDuration() < 20000) {
                        player.removePotionEffect(effect.getType());
                        for (int i = 0; i < 3; i++) {
                            player.getWorld().playEffect(player.getEyeLocation(), Effect.HEART, 5);
                            player.getWorld().playEffect(player.getLocation(), Effect.HEART, 5);
                        }
                        player.getWorld().playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                    }
                }
            }
        }
    }

    private void ExplosiveShot(Arrow arrow) {
        arrow.getWorld().playEffect(arrow.getLocation(), Effect.EXPLOSION_LARGE, 5);
        arrow.getWorld().playSound(arrow.getLocation(), Sound.EXPLODE, 10, 1);
        List<Entity> NearbyPlayers = arrow.getNearbyEntities(1.5,1.5,1.5);
        for (Entity entity : NearbyPlayers) {
            if (entity instanceof Player) {
                Player players = (Player) entity;
                final Vector v = players.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(1).setY(.1);
                players.setVelocity(v);
                players.damage(2);
            }
            else {
                return;
            }
        }
    }

    private boolean RandomChance(int Chance, boolean Debuff, Player player) {
        Random rand = new Random();
        int random = rand.nextInt(100);
        if (Debuff && GetArmorLores(player).contains("Odds")) {
            if (random <= (Chance + 2)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            if (random <= (Chance - 1)) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    private void Looter(Player killer, Player killed) {
        for (ItemStack Armor : killed.getInventory().getArmorContents()) {
            if (Armor != null) {
                killer.getInventory().addItem(Armor);
            }
        }
        if (killed.getItemInHand() != null) {
            killer.getInventory().addItem(killed.getItemInHand());
        }
    }

    List<String> GetArmorLores(Player player) {
        List<String> Lores = new ArrayList<>();
        for (ItemStack Armor : player.getInventory().getArmorContents()) {
            if (Armor.hasItemMeta()) {
                if (Armor.getItemMeta().hasLore()) {
                    for (String Lore : Armor.getItemMeta().getLore()) {
                        Lores.add(ChatColor.stripColor(Lore));
                    }
                }
            }
        }
        return Lores;
    }

    private List<String> GetMainHandLores(Player player) {
        List<String> Lores = new ArrayList<>();
        if (player.getItemInHand() != null) {
            if (player.getItemInHand().hasItemMeta()) {
                if (player.getItemInHand().getItemMeta().hasLore()) {
                    for (String Lore : player.getItemInHand().getItemMeta().getLore()) {
                        Lores.add(ChatColor.stripColor(Lore));
                    }
                }
            }
        }
        return Lores;
    }

    void ResetEffectsAfterDelay(int Duration, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                RemovePotionEffects(player, false);
                AddPermanentPotionEffectsIncludingCurrentItem(player, player.getItemInHand(), null);
            }
        }.runTaskLater(mainClass.getPlugin(), Duration);
    }

    private void AddPermanentPotionEffectsExcludingCursorItem(Player player, ItemStack CursorItem) {
        RemovePotionEffects(player, false);
        for (ItemStack Armor : player.getInventory().getArmorContents()) {
            if (Armor != null && Armor.hasItemMeta()) {
                if (Armor.getItemMeta().hasLore()) {
                    for (String ArmorLore : Armor.getItemMeta().getLore()) {
                        if (CursorItem != null) {
                            if (!Armor.equals(CursorItem)) {
                                AddPermamentPotionEffects(ChatColor.stripColor(ArmorLore), player);
                            }
                        }
                        else {
                            AddPermamentPotionEffects(ChatColor.stripColor(ArmorLore), player);
                        }
                    }
                }
            }
        }
    }

    private void AddPermanentPotionEffectsIncludingCurrentItem(Player player, ItemStack CurrentItem, ItemStack ArmorToNotInclude) {
        RemovePotionEffects(player, false);
        for (ItemStack Armor : player.getInventory().getArmorContents()) {
            if (Armor != null) {
                if (Armor.hasItemMeta()) {
                    if (Armor.getItemMeta().hasLore()) {
                        if (ArmorToNotInclude != null) {
                            if (!Armor.equals(ArmorToNotInclude)) {
                                for (String ArmorLore : Armor.getItemMeta().getLore()) {
                                    AddPermamentPotionEffects(ChatColor.stripColor(ArmorLore), player);
                                }
                            }
                        }
                        else {
                            for (String ArmorLore : Armor.getItemMeta().getLore()) {
                                AddPermamentPotionEffects(ChatColor.stripColor(ArmorLore), player);
                            }
                        }
                    }
                }
            }
        }
        if (CurrentItem != null) {
            if (CurrentItem.hasItemMeta()) {
                if (CurrentItem.getItemMeta().hasLore()) {
                    for (String CurrentItemLore : CurrentItem.getItemMeta().getLore()) {
                        AddPermamentPotionEffects(ChatColor.stripColor(CurrentItemLore), player);
                    }
                }
            }
        }
    }

    public void AddPermamentPotionEffects(String ArmorLore, Player player) {
        if (ArmorLore.equals("Strength")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, (short) 0));
        }
        else if (ArmorLore.equals("Speed")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, (short) 1));
        }
        else if (ArmorLore.equals("Fire Resistance")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, (short) 0));
        }
        else if (ArmorLore.equals("Water Breathing")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, (short) 0));
        }
        else if (ArmorLore.equals("Regeneration")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, (short) 0));
        }
        else if (ArmorLore.equals("Resistance")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, (short) 0));
        }
        else if (ArmorLore.equals("Health Boost")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, (short) 0));
        }
        else if (ArmorLore.equals("Night Vision")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, (short) 0));
        }
        else if (ArmorLore.equals("Saturation")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, (short) 0));
        }
        else if (ArmorLore.equals("Tank")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, (short) 4));
        }
        else if (ArmorLore.equals("Splitter")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, (short) 1));
        }
    }

    private void Patching(Player player) {
        if (player.getItemInHand().getType().equals(Material.OBSIDIAN)) {
            if (RandomChance(50, false, null)) {
                player.getInventory().setItemInHand(player.getItemInHand());
            }
        }
    }

    private void Starvation(Player enemy, Player damager) {
        int DebuffAmplifier = 0;
        if (GetArmorLores(damager).contains("Contaminator")) {
            DebuffAmplifier = 1;
        }
        if (RandomChance(5, true, damager)) {
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120, (short) 2 + DebuffAmplifier));
            enemy.getWorld().playSound(enemy.getLocation(), Sound.EAT, 10, 1);
            for (int a = 0; a < 5; a++) {
                enemy.getWorld().playEffect(enemy.getEyeLocation(), Effect.SMOKE, 5);
                enemy.getWorld().playEffect(enemy.getLocation(), Effect.SMOKE, 5);
            }
        }
    }

    private void Frostbite(Player enemy, Player damager) {
        int DebuffAmplifier = 0;
        if (GetArmorLores(damager).contains("Contaminator")) {
            DebuffAmplifier = 1;
        }
        if (RandomChance(5, true, damager)) {
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, (short) 0 + DebuffAmplifier));
            for (int a = 0; a < 50; a++) {
                enemy.getWorld().playEffect(enemy.getEyeLocation(), Effect.SNOW_SHOVEL, 1);
            }
        }
    }

    public void RemovePotionEffects(Player player, boolean RemoveHealthBoost) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getDuration() > 20000) {
                if (!RemoveHealthBoost) {
                    if (!(effect.getType().equals(PotionEffectType.HEALTH_BOOST))) {
                        player.removePotionEffect(effect.getType());
                    }
                }
                else {
                    player.removePotionEffect(effect.getType());
                }
            }
        }
    }
}
