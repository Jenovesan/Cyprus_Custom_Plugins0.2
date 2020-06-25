package FactionUpgrades;

import CyprusTime.CyprusTimeMain;
import Main.MainClass;
import Utility.UtilityMain;
import com.massivecraft.factions.*;
import com.massivecraft.factions.perms.Role;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FactionUpgradeFShield implements Listener {
    MainClass mainClass;
    UtilityMain utilityMain = new UtilityMain();
    public FactionUpgradeFShield(MainClass mc) {
        mainClass = mc;
    }

    public void StartingPointForFShield(Player player, String[] args) {
        if (args.length > 0) {
            if (Factions.getInstance().getByTag(args[0]) != null) {
                Faction faction = Factions.getInstance().getByTag(args[0]);
                SendShieldMessageOfOtherFaction(player, faction);
            }
            else {
                player.sendMessage(ChatColor.RED + args[0] + " is not a faction");
            }
            return;
        }
        LoadGui(player);
    }

    public void StarrtingPointForFBaseChunk(Player player, String command) {
        if (FPlayers.getInstance().getByPlayer(player).getRole().isAtLeast(Role.COLEADER)) {
            if (command.contains("/f setpos1") && FPlayers.getInstance().getByPlayer(player).isInOwnTerritory()) {
                List<Location> positions = PlayersSettingRegion.getOrDefault(player, new ArrayList<>(Arrays.asList(null, null)));
                positions.set(0, player.getLocation());
                PlayersSettingRegion.put(player, positions);
                player.sendMessage(ChatColor.GREEN + "Position 1 set");
            } else if (command.contains("/f setpos2") && FPlayers.getInstance().getByPlayer(player).isInOwnTerritory()) {
                List<Location> positions = PlayersSettingRegion.getOrDefault(player, new ArrayList<>(Arrays.asList(null, null)));
                positions.set(1, player.getLocation());
                PlayersSettingRegion.put(player, positions);
                player.sendMessage(ChatColor.GREEN + "Position 2 set");
            } else if (command.contains("/f setregion")) {
                SetFRegion(player);
            }
        }
        if (command.contains("/f checkregion")) {
            CheckRegion(player);
        }
        else {
            player.sendMessage(ChatColor.RED + "Setting Your Base's Tnt Protection Region" + "\n" + "/f setpos1 - sets the first position" + "\n" + "/f setpos2 - sets the second position" + "\n" + "/f setregion - sets all chunks in between the two regions as tnt protect" + "\n" + "/f checkregion - gets the faction who has that land protected");
        }
    }

    private void CheckRegion(Player player) {
        FLocation fLocation = new FLocation(player.getLocation());
        boolean FoundRegion = false;
        for (Map.Entry<String, Set<FLocation>> element : FactionsBaseRegion.entrySet()) {
            String FactionId = element.getKey();
            Set<FLocation> fLocations = element.getValue();
            for (FLocation fLocation1 : fLocations) {
                if (fLocation1.equals(fLocation)) {
                    player.sendMessage(ChatColor.DARK_GREEN + Factions.getInstance().getFactionById(FactionId).getTag() + ChatColor.GREEN + " has this chunk set as a tnt protected region");
                    FoundRegion = true;
                }
            }
        }
        if (!FoundRegion) {
            player.sendMessage(ChatColor.RED + "No factions have this chunk as a tnt protected region");
        }
    }

    @EventHandler
    private void ReCheckFRegionsWhenUnclaims(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (StringUtils.containsIgnoreCase(command, "/f unclaim") || StringUtils.containsIgnoreCase(command, "/f disband")) {
            ReCheckFRegions();
        }
    }

    private void ReCheckFRegions() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Set<FLocation>> element : FactionsBaseRegion.entrySet()) {
                    String FactionId = element.getKey();
                    Faction faction = Factions.getInstance().getFactionById(FactionId);
                    if (faction == null) {
                        FactionsBaseRegion.remove(FactionId);
                        continue;
                    }
                    Set<FLocation> fLocations = element.getValue();
                    fLocations.removeIf(fLocation1 -> !faction.getAllClaims().contains(fLocation1));
                    FactionsBaseRegion.replace(FactionId, fLocations);
                }
                SaveFactionsBaseRegionInConfig();
            }
        }.runTaskLaterAsynchronously(mainClass.getPlugin(), 1);

    }

    private Boolean isInTheFactionsRegion(Faction faction, FLocation fLocation) {
        Set<FLocation> claims = FactionsBaseRegion.getOrDefault(faction.getId(), new HashSet<>());
        return claims.contains(fLocation);
    }

    HashMap<Player, List<Location>> PlayersSettingRegion = new HashMap<>();
    HashMap<String, Set<FLocation>> FactionsBaseRegion = new HashMap<>();
    private void SetFRegion(Player player) {
        if (ViableToSetFRegion(player)) {
            List<Location> locations = PlayersSettingRegion.get(player);
            Location loc1 = locations.get(0);
            Location loc2= locations.get(1);
            Set<FLocation> FlocationsInBetweenLocations = getFLocationsBetweenPositions(loc1, loc2);
            Set<FLocation> regions = new HashSet<>(FlocationsInBetweenLocations);
            FactionsBaseRegion.put(FPlayers.getInstance().getByPlayer(player).getFactionId(), regions);
            SaveFactionsBaseRegionInConfig();
            player.sendMessage(ChatColor.GREEN + "Your faction's tnt protection region has been set");
        }
    }

    private void SaveFactionsBaseRegionInConfig() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Set<FLocation>> element : FactionsBaseRegion.entrySet()) {
            for (FLocation fLocation : element.getValue()) {
                list.add(element.getKey() + ":" + fLocation);
            }
        }
        mainClass.getConfig().set("FactionsBaseRegion", list);
    }

    public void LoadFactionsBaseRegionFromConfig() {
        List<String> list = mainClass.getConfig().getStringList("FactionsBaseRegion");
        for (String string : list) {
            String FactionId = string.split(":")[0];
            FLocation fLocation = FLocation.fromString(string.split(":")[1]);
            Set<FLocation> CurrentSet = FactionsBaseRegion.getOrDefault(FactionId, new HashSet<>());
            CurrentSet.add(fLocation);
            FactionsBaseRegion.put(FactionId, CurrentSet);
        }
    }


    private Boolean ViableToSetFRegion(Player player) {
        Faction PlayerFaction = FPlayers.getInstance().getByPlayer(player).getFaction();
        List<Location> locations = PlayersSettingRegion.getOrDefault(player, new ArrayList<>(Arrays.asList(null, null)));
        if (locations.get(0) != null && locations.get(1) != null) {
            if (locations.get(0).getWorld().equals(locations.get(1).getWorld())) {
                Location loc1 = locations.get(0);
                Location loc2= locations.get(1);
                Set<FLocation> FlocationsInBetweenLocations = getFLocationsBetweenPositions(loc1, loc2);
                if (FlocationsInBetweenLocations.size() > 900) {
                    return false;
                }
                for (FLocation fLocation : FlocationsInBetweenLocations) {
                    if (!PlayerFaction.getAllClaims().contains(fLocation)) {
                        return false;
                    }
                }
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "An error has occurred and the position was not set");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You must set your positions first");
            player.sendMessage(ChatColor.RED + "Setting Your Base's Tnt Protection Region" + "\n" + "/f setpos1 - sets the first position" + "\n" + "/f setpos2 - sets the second position" + "\n" + "/f setregion - sets all chunks in between the two regions as tnt protect" + "\n" + "/f checkregion - gets the faction who has that land protected");
        }
        return false;
    }

    public static Set<FLocation> getFLocationsBetweenPositions(Location location1, Location location2) {
        Set<FLocation> fLocations = new HashSet<>();
        int minX = Math.min(location1.getBlockX(), location2.getBlockX());
        int maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                fLocations.add(new FLocation(new Location(location1.getWorld(), x, 256, z)));
            }
        }
        return fLocations;
    }

    private void SendShieldMessageOfOtherFaction(CommandSender sender, Faction faction) {
        String state = "Disabled";
        String time = "";
        String color = ChatColor.RED + "";
        String FactionId = faction.getId();
        LoadFactionsShieldStatusFromConfig();
        if (isOnShield(FactionId)) {
            state = "Enabled";
            color = ChatColor.GREEN + "";
        }
        LoadFactionShieldTimesFromConfig();
        for (String string  : FactionShieldTimes) {
            String[] SplitString = string.split(":");
            if (SplitString[0].equals(FactionId)) {
                time = SplitString[1] + " to " + SplitString[2];
                break;
            }
        }
        if (time.equals("")) {
            time = "No time set";
        }
        sender.sendMessage(color + time + " (" + state + ")");
    }

    private void LoadGui(Player player) {
        final Inventory inv;
        inv = Bukkit.createInventory(player, 54, ChatColor.RED + "" + ChatColor.BOLD + "Faction Shield");
        FillGui(inv);
        LoadFactionShieldTimesFromConfig();
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        for (String string : FactionShieldTimes) {
            String[] SplitString = string.split(":");
            if (SplitString[0].contains(FactionId)) {
                String StartTime = ChatColor.RED + SplitString[1];
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    if (item.getType().equals(Material.CLAY)) {
                        if (item.getItemMeta().getDisplayName().equals(StartTime)) {
                            LoadShieldGui(player, inv, i);
                            //ChangeTime(player, inv, i);
                        }
                    }
                }
            }
        }
        player.openInventory(inv);
    }


    private void FillGui(Inventory inv) {
        ItemStack Fill = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, Fill);
        }
        List<Integer> DoNotAddClaySpots = new ArrayList<>(Arrays.asList(18, 27, 17, 26));
        int time = 12;
        String AmOrPm = "am";
        for (int i = 10; i < 35; i++) {
            if (!(DoNotAddClaySpots.contains(i))) {
                inv.setItem(i, utilityMain.createGuiItem(Material.CLAY, ChatColor.RED + Integer.toString(time) + AmOrPm));
                if (time >= 12) {
                    time = 1;
                }
                else {
                    time++;
                }
                if (i > 22) {
                    AmOrPm = "pm";
                }
            }
        }
        inv.setItem(39, utilityMain.createGuiItem(Material.CLAY, ChatColor.RED + "9pm"));
        inv.setItem(40, utilityMain.createGuiItem(Material.CLAY, ChatColor.RED + "10pm"));
        inv.setItem(41, utilityMain.createGuiItem(Material.CLAY, ChatColor.RED + "11pm"));
        inv.setItem(45, utilityMain.createGuiItem(Material.WATCH, ChatColor.YELLOW + "No Time Selected"));
        inv.setItem(53, utilityMain.createGuiItem(Material.BARRIER, ChatColor.RED + "Exit"));
    }

    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getSlot() == -999) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        Material type = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();
        if (!(event.getInventory().getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Faction Shield"))) {
            return;
        }
        event.setCancelled(true);
        if (type.equals(Material.CLAY) || type.equals(Material.STAINED_CLAY)) {
            ChangeTime(player, event.getInventory(), event.getSlot());
            return;
        }
        if (type.equals(Material.BARRIER)) {
            player.closeInventory();
        }
    }

    @EventHandler
    private void explosion(EntityExplodeEvent event) {
        FLocation fLocation = new FLocation(event.getLocation());
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        if (isOnShieldFromLocation(event.getLocation()) && isInTheFactionsRegion(faction, fLocation)) {
            event.setCancelled(true);
        }
    }

    private boolean isOnShieldFromLocation(Location location) {
        FLocation fLocation = new FLocation(location);
        for (Map.Entry element : FactionsShieldStatus.entrySet()) {
            String FactionId = (String) element.getKey();
            Boolean status = (Boolean) element.getValue();
            if (Factions.getInstance().getFactionById(FactionId) != null) {
                Faction faction = Factions.getInstance().getFactionById(FactionId);
                Set<FLocation> claims = faction.getAllClaims();
                if (claims.contains(fLocation)) {
                    if (status.equals(true)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isOnShield(String TargetFactionId) {
        for (Map.Entry element : FactionsShieldStatus.entrySet()) {
            String FactionId = (String) element.getKey();
            Boolean status = (Boolean) element.getValue();
            if (FactionId.equals(TargetFactionId) && status.equals(true)) {
                return true;
            }
        }
        return false;
    }

    private void LoadShieldGui(Player player, Inventory inv, Integer slot) {
        FillGui(inv);
        LoadFactionShieldUpgradeHashmapFromConfig();
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        inv.setItem(slot, ColoredClay("Dark Green", inv.getItem(slot).getItemMeta().getDisplayName()));
        int AmountOfHours = getHoursAmount(FactionId);
        String StartTime  = inv.getItem(slot).getItemMeta().getDisplayName();
        String EndTime = "";
        for (int i = 1, ExtraLoops = 0, SlotNumber = slot + i; i < AmountOfHours + ExtraLoops; i++, SlotNumber++) {
            if (inv.getItem(SlotNumber).getType().equals(Material.CLAY) || inv.getItem(SlotNumber).getType().equals(Material.STAINED_CLAY)) {
                inv.setItem(SlotNumber, ColoredClay("Green", inv.getItem(SlotNumber).getItemMeta().getDisplayName()));
            }
            else {
                ExtraLoops++;
                if (SlotNumber >= 41) {
                    SlotNumber = 9;
                }
            }
            if (i >= AmountOfHours + ExtraLoops - 1) {
                EndTime = inv.getItem(SlotNumber).getItemMeta().getDisplayName();
            }
        }
        inv.setItem(45, utilityMain.createGuiItem(Material.WATCH, ChatColor.YELLOW + ChatColor.stripColor(StartTime) + " to " + ChatColor.stripColor(EndTime)));
        LoadFactionShieldTimesFromConfig();
        LoadFactionsShieldStatusFromConfig();
        for (String string : FactionShieldTimes) {
            String[] SplitString = string.split(":");
            if (SplitString[0].equals(FactionId)) {
                FactionShieldTimes.remove(string);
                break;
            }
        }
    }

    private void ChangeTime(Player player, Inventory inv, Integer slot) {
        LoadFactionShieldUpgradeHashmapFromConfig();
        String FactionId = FPlayers.getInstance().getByPlayer(player).getFactionId();
        if (!(FactionShieldUpgrade.containsKey(FactionId))) {
            player.sendMessage(ChatColor.RED + "Your faction must purchase the faction shield upgrade");
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        if (!FPlayers.getInstance().getByPlayer(player).getRole().isAtLeast(Role.COLEADER)) {
            player.sendMessage(ChatColor.RED + "Only Co-Leaders+ can change the shield time");
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        if (mainClass.cyprusTimeMain.time == CyprusTimeMain.Time.TNT) {
            player.sendMessage(ChatColor.RED + "You cannot change your shield time when tnt is enabled");
            player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1, 1);
            return;
        }
        FillGui(inv);
        inv.setItem(slot, ColoredClay("Dark Green", inv.getItem(slot).getItemMeta().getDisplayName()));
        int AmountOfHours = getHoursAmount(FactionId);
        String StartTime  = inv.getItem(slot).getItemMeta().getDisplayName();
        String EndTime = "";
        for (int i = 1, ExtraLoops = 0, SlotNumber = slot + i; i < AmountOfHours + ExtraLoops; i++, SlotNumber++) {
            if (inv.getItem(SlotNumber).getType().equals(Material.CLAY) || inv.getItem(SlotNumber).getType().equals(Material.STAINED_CLAY)) {
                inv.setItem(SlotNumber, ColoredClay("Green", inv.getItem(SlotNumber).getItemMeta().getDisplayName()));
            }
            else {
                ExtraLoops++;
                if (SlotNumber >= 41) {
                    SlotNumber = 9;
                }
            }
            if (i >= AmountOfHours + ExtraLoops - 1) {
                EndTime = inv.getItem(SlotNumber).getItemMeta().getDisplayName();
            }
        }
        inv.setItem(45, utilityMain.createGuiItem(Material.WATCH, ChatColor.YELLOW + ChatColor.stripColor(StartTime) + " to " + ChatColor.stripColor(EndTime)));
        LoadFactionShieldTimesFromConfig();
        LoadFactionsShieldStatusFromConfig();
        for (String string : FactionShieldTimes) {
            String[] SplitString = string.split(":");
            if (SplitString[0].equals(FactionId)) {
                FactionShieldTimes.remove(string);
                break;
            }
        }
        for (Map.Entry element : FactionsShieldStatus.entrySet()) {
            String Id = (String) element.getKey();
            if (Id.equals(FactionId)) {
                FactionsShieldStatus.remove(FactionId);
                break;
            }
        }
        FactionShieldTimes.add(FactionId + ":" + ChatColor.stripColor(StartTime) + ":" + ChatColor.stripColor(EndTime));
        FactionsShieldStatus.put(FactionId, false);
        SaveFactionShieldHashmapInConfig();
        SaveFactionsShieldStatusInConfig();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
    }

    private void LoadFactionShieldTimesFromConfig() {
        FactionShieldTimes = mainClass.getConfig().getStringList("FactionShieldTimes");
    }

    List<String> FactionShieldTimes = new ArrayList<>();
    private void SaveFactionShieldHashmapInConfig() {
        mainClass.getConfig().set("FactionShieldTimes", FactionShieldTimes);
        mainClass.saveConfig();
    }

    private ItemStack ColoredClay(String color, String name) {
        ItemStack clay = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
        if (color.equals("Dark Green")) {
            clay = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
        }
        ItemMeta meta = clay.getItemMeta();
        meta.setDisplayName(name);
        clay.setItemMeta(meta);
        return clay;
    }

    private Integer getHoursAmount(String FactionId) {
        LoadFactionShieldUpgradeHashmapFromConfig();
        if (!(FactionShieldUpgrade.containsKey(FactionId))) {
            return 0;
        }
        Integer level = FactionShieldUpgrade.get(FactionId);
        switch (level) {
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 6;
            case 4:
                return 9;
            default:
                return 12;
        }
    }

    HashMap<String, Integer> FactionShieldUpgrade = new HashMap<>();
    private void LoadFactionShieldUpgradeHashmapFromConfig() {
        List<String> FactionShieldUpgradeStringFromConfig = mainClass.getConfig().getStringList("FactionShieldUpgrade");
        for (String string : FactionShieldUpgradeStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Integer Level = Integer.parseInt(SplitString[1]);
            FactionShieldUpgrade.put(FactionId, Level);
        }
    }

    public void FactionShieldTimeManager() {
        LoadFactionsShieldStatusFromConfig();
        new BukkitRunnable() {
            @Override
            public void run() {
                String time = getCurrentEstTime();
                LoadFactionShieldTimesFromConfig();
                for (String string : FactionShieldTimes) {
                    String[] SplitString = string.split(":");
                    String FactionId = SplitString[0];
                    Faction faction = Factions.getInstance().getFactionById(FactionId);
                    String EndTime = getEndTime(SplitString[2]);
                    if (SplitString[1].equals(time) && !isOnShield(FactionId)) {
                        FactionsShieldStatus.replace(FactionId, true);
                        SaveFactionsShieldStatusInConfig();
                        SendFactionShieldMessage("Enabled", faction);
                    }
                    else if (SplitString[2].equals(EndTime) && isOnShield(FactionId)) {
                        FactionsShieldStatus.replace(FactionId, false);
                        SaveFactionsShieldStatusInConfig();
                        SendFactionShieldMessage("Disabled", faction);
                    }
                }
            }
        }.runTaskTimerAsynchronously(mainClass.getPlugin(), 0, 500);
    }

    private String getEndTime(String string) {
        String AmOrPm = string.replaceAll("[0-9]", "");
        int EndTime = ((Integer.parseInt(string.replaceAll("[^0-9]", ""))) + 1);
        if (AmOrPm.equals("am")) {
            if (EndTime > 12) {
                return EndTime - 12 + "pm";
            } else {
                return EndTime + "am";
            }
        }
        else {//pm
            if (EndTime > 12) {
                return EndTime - 12 + "am";
            } else {
                return EndTime + "pm";
            }
        }
    }

    HashMap<String, Boolean> FactionsShieldStatus = new HashMap<>();
    private void LoadFactionsShieldStatusFromConfig() {
        List<String> FactionsShieldStatusStringFromConfig = mainClass.getConfig().getStringList("FactionsShieldStatus");
        for (String string : FactionsShieldStatusStringFromConfig) {
            String[] SplitString = string.split(":");
            String FactionId = SplitString[0];
            Boolean status = Boolean.parseBoolean(SplitString[1]);
            FactionsShieldStatus.put(FactionId, status);
        }
    }

    private void SaveFactionsShieldStatusInConfig() {
        List<String> FactionsShieldStatusString = new ArrayList<>();
        for (Map.Entry element : FactionsShieldStatus.entrySet()) {
            String FactionId = (String) element.getKey();
            String status = Boolean.toString((Boolean) element.getValue());
            FactionsShieldStatusString.add(FactionId + ":" + status);
        }
        mainClass.getConfig().set("FactionsShieldStatus", FactionsShieldStatusString);
        mainClass.saveConfig();
    }

    private void SendFactionShieldMessage(String DisabledOrEnabled, Faction faction) {
        Set<FPlayer> FactionPlayers = faction.getFPlayers();
        for (FPlayer player : FactionPlayers) {
            if (player.isOnline()) {
                if (DisabledOrEnabled.equals("Enabled")) {
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Your faction's shield has been enabled!");
                }
                else {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Your faction's shield has been disabled");
                }
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 1, 1);
            }
        }
    }

    private String getCurrentEstTime() {
        Calendar calNewYork = Calendar.getInstance();
        calNewYork.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        int time = calNewYork.get(Calendar.HOUR_OF_DAY);
        return ConvertToAmOrPm(time);
    }

    private String ConvertToAmOrPm(Integer time) {
        if (time > 12) {
            return time - 12 + "pm";
        }
        return time + "am";
    }
}
