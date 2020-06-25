package Main;

import AddAboveMaxEnchantsToArmor.AddAboveMaxEnchantsToArmorMain;
import AfkCheck.AfkCheckMain;
import AntiCheat.AntiCheatCPSCap;
import AntiCheat.AntiCheatLongTermAutoClicker;
import AntiCheat.AntiCheatNukerAndKarma;
import ArmorDurability.DurabilityMain;
import AutoSellGem.AutoSellGemMain;
import BlackMarket.BlackMarketPlayerEvents;
import ChunkBusters.ChunkBustersMain;
import Classes.ClassesArcher;
import Classes.ClassesBard;
import CombatTimer.CombatTimerMain;
import Conquest.ConquestMain;
import CustomFactionCommands.CustomFactionCommandsFChest;
import CustomFactionCommands.CustomFactionCommandsFLock;
import CustomFactionCommands.CustomFactionCommandsFRoster;
import CustomFactionCommands.CustomFactionsCommandsFAlts;
import CustomFactions.CustomFactionsSendToSpawnIfLogoutInEnemyTerritory;
import CustomMobDrops.CustomMobDropsMain;
import CyprusTime.CyprusTimeMain;
import DamageBoost.DamageBoostCommands;
import DamageBoost.DamageBoostEvents;
import Energizer.EnergizerMain;
import EntityClear.EntityClearMain;
import FactionUpgrades.*;
import FactionsPickaxe.FactionsPickaxeCommands;
import FactionsPickaxe.FactionsPickaxeEvents;
import GoldenApplesCooldown.GoldenApplesCooldownMain;
import HarvesterHoes.HarvesterHoeCommands;
import HarvesterHoes.HarvesterHoeEvents;

import InstaBlaze.InstaBlazeMain;
import JackPot.JackPotCommands;
import JackPot.JackPotEvents;
import Key.KeyMain;
import KingsSword.KingsSwordMain;
import Kits.KitsGui;
import Kits.KitsMain;
import LightningRod.LightningRodMain;
import Mobcoins.MobcoinsMain;
import NiceCommands.NiceCommandsCane;
import NiceCommands.NiceCommandsPot;
import PearlCooldown.PearlCooldownMain;
import Prot7Book.Prot7BookMain;
import QueensAxe.QueensAxeMain;
import Restrictions.RestrictionsMain;
import Roam.RoamMain;
import SandBots.SandBotsMain;
import SellBoost.SellBoostCommands;
import SellBoost.SellBoostEvents;
import SellWands.SellWandsMain;
import ServerInfo.ServerInfoMain;
import SotwJoin.SotwJoinMain;
import Staff.StaffReportCommand;
import SuperCegg.SuperCeggCommands;
import SuperCegg.SuperCeggEvents;
import CustomEnchants.CustomEnchantsCommands;
import CustomEnchants.CustomEnchantsEvents;
import Teleport.TeleportMain;
import Tntfill.TntfillMain;
import TopDown.TopDownMain;
import WarpGui.WarpGuiMain;
import WbAndNv.WbAndNvMain;
import Wild.WildMain;
import XpBoost.XpBoostCommands;
import XpBoost.XpBoostEvents;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainClass extends JavaPlugin {
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Cyprus Plugins Have Been Successfully Been Enabled");
        RegisterCommands();
        RegisterEvents();
        StartOnEnableRunnablesAndFunctions();
        SetUpVaultAPI();
    }

    public void onDisable() {
        SavingFunctions();
        this.saveConfig();
    }

    private void RegisterCommands() {
        this.getCommand("ce").setExecutor(new CustomEnchantsCommands());
        this.getCommand("website").setExecutor(new ServerInfoMain());
        this.getCommand("discord").setExecutor(new ServerInfoMain());
        this.getCommand("hh").setExecutor(new HarvesterHoeCommands());
        this.getCommand("autosellgem").setExecutor(new AutoSellGemMain());
        //this.getCommand("supplydrop").setExecutor(new SupplyDropCommands(this));
        //this.getCommand("sd").setExecutor(new SupplyDropCommands(this));
        this.getCommand("xpboost").setExecutor(new XpBoostCommands());
        this.getCommand("damageboost").setExecutor(new DamageBoostCommands());
        this.getCommand("sellboost").setExecutor(new SellBoostCommands());
        //this.getCommand("blackmarket").setExecutor(blackMarketCommandsAndGui);
        this.getCommand("mobcoins").setExecutor(new MobcoinsMain(this));
        this.getCommand("mobshop").setExecutor(new MobcoinsMain(this));
        this.getCommand("supercegg").setExecutor(new SuperCeggCommands());
        this.getCommand("tntfill").setExecutor(new TntfillMain(this));
        this.getCommand("givetrenchpick").setExecutor(new FactionsPickaxeCommands());
        this.getCommand("sandbot").setExecutor(sandBotsMain);
        //this.getCommand("track").setExecutor(staffTrack);
        //this.getCommand("canebot").setExecutor(caneBotsGui);
        this.getCommand("sellwand").setExecutor(sellWandsMain);
        this.getCommand("report").setExecutor(new StaffReportCommand());
        this.getCommand("scane").setExecutor(new NiceCommandsCane(this));
        this.getCommand("pot").setExecutor(new NiceCommandsPot());
        //this.getCommand("whitelist").setExecutor(whiteListMain);
        this.getCommand("chunkbuster").setExecutor(chunkBustersMain);
        this.getCommand("roam").setExecutor(roamMain);
        this.getCommand("kit").setExecutor(new KitsGui(this));
        this.getCommand("lightningrod").setExecutor(lightningRodMain);
        this.getCommand("afkcheck").setExecutor(afkCheckMain);
        this.getCommand("top").setExecutor(topDownMain);
        this.getCommand("down").setExecutor(topDownMain);
        this.getCommand("kingswordgive").setExecutor(new KingsSwordMain());
        this.getCommand("queenaxegive").setExecutor(new QueensAxeMain());
        this.getCommand("entityclear").setExecutor(entityClearMain);
        this.getCommand("prot7").setExecutor(new Prot7BookMain());
        this.getCommand("cyprustime").setExecutor(cyprusTimeMain);
        this.getCommand("wild").setExecutor(wildMain);
        this.getCommand("nv").setExecutor(wbAndNvMain);
        this.getCommand("wb").setExecutor(wbAndNvMain);
        this.getCommand("reclaim").setExecutor(keyMain);
        this.getCommand("sotwjoin").setExecutor(sotwJoinMain);
    }

    private void RegisterEvents() {
        getServer().getPluginManager().registerEvents(new CustomEnchantsCommands(), this);
        getServer().getPluginManager().registerEvents(customEnchantsEvents, this);
        getServer().getPluginManager().registerEvents(new ServerInfoMain(), this);
        getServer().getPluginManager().registerEvents(new HarvesterHoeEvents(this), this);
        getServer().getPluginManager().registerEvents(new AutoSellGemMain(), this);
        //getServer().getPluginManager().registerEvents(new SupplyDropCommands(this), this);
        getServer().getPluginManager().registerEvents(goldenApplesCooldownMain, this);
        getServer().getPluginManager().registerEvents(xpBoostEvents, this);
        getServer().getPluginManager().registerEvents(damageBoostEvents, this);
        getServer().getPluginManager().registerEvents(jackPotEvents, this);
        //getServer().getPluginManager().registerEvents(blackMarketCommandsAndGui, this);
        //getServer().getPluginManager().registerEvents(new BlackMarketPlayerEvents(this), this);
        getServer().getPluginManager().registerEvents(new InstaBlazeMain(this), this);
        getServer().getPluginManager().registerEvents(restrictionsMain, this);
        getServer().getPluginManager().registerEvents(new MobcoinsMain(this), this);
        getServer().getPluginManager().registerEvents(new AddAboveMaxEnchantsToArmorMain(), this);
        //getServer().getPluginManager().registerEvents(new CastleWorldGuard(), this);
        //getServer().getPluginManager().registerEvents(new CastleGates(this), this);
        getServer().getPluginManager().registerEvents(new FactionUpgradesGui(this), this);
        getServer().getPluginManager().registerEvents(new JackPotCommands(this), this);
        getServer().getPluginManager().registerEvents(new JackPotEvents(this), this);
        getServer().getPluginManager().registerEvents(new SuperCeggEvents(), this);
        getServer().getPluginManager().registerEvents(new FactionUpgradesXpUpgrade(this), this);
        getServer().getPluginManager().registerEvents(new FactionUpgradesDropRateUpgrade(this), this);
        getServer().getPluginManager().registerEvents(new FactionUpgradesCropGrowthUpgrade(this), this);
        getServer().getPluginManager().registerEvents(new FactionUpgradesFTtntUpgrade(this), this);
        getServer().getPluginManager().registerEvents(new TntfillMain(this), this);
        getServer().getPluginManager().registerEvents(factionUpgradeFShield, this);
        getServer().getPluginManager().registerEvents(new FactionsPickaxeEvents(), this);
        getServer().getPluginManager().registerEvents(new FactionUpgradesDamageReduction(this), this);
        getServer().getPluginManager().registerEvents(new FactionUpgradesFWarpUpgrade(this), this);
        getServer().getPluginManager().registerEvents(new CustomFactionsCommandsMain(this), this);
        getServer().getPluginManager().registerEvents(combatTimerMain, this);
        //getServer().getPluginManager().registerEvents(staffTrack, this);
        getServer().getPluginManager().registerEvents(sandBotsMain, this);
        getServer().getPluginManager().registerEvents(classesBard, this);
        getServer().getPluginManager().registerEvents(new CustomMobDropsMain(), this);
        getServer().getPluginManager().registerEvents(classesArcher, this);
        //getServer().getPluginManager().registerEvents(caneBotsGui, this);
        //getServer().getPluginManager().registerEvents(new TntProtBlocksMain(), this);
        getServer().getPluginManager().registerEvents(sellWandsMain, this);
        getServer().getPluginManager().registerEvents(sellBoostEvents, this);
        //getServer().getPluginManager().registerEvents(customFactionCommandsFLock, this);
        getServer().getPluginManager().registerEvents(customFactionCommandsFChest, this);
        getServer().getPluginManager().registerEvents(antiCheatNukerAndKarma, this);
        getServer().getPluginManager().registerEvents(customFactionCommandsFRoster, this);
        getServer().getPluginManager().registerEvents(pearlCooldownMain, this);
        //getServer().getPluginManager().registerEvents(whiteListMain, this);
        getServer().getPluginManager().registerEvents(chunkBustersMain, this);
        getServer().getPluginManager().registerEvents(roamMain, this);
        getServer().getPluginManager().registerEvents(kitsMain, this);
        getServer().getPluginManager().registerEvents(lightningRodMain, this);
        getServer().getPluginManager().registerEvents(new EnergizerMain(this), this);
        getServer().getPluginManager().registerEvents(new DurabilityMain(), this);
        getServer().getPluginManager().registerEvents(afkCheckMain, this);
        getServer().getPluginManager().registerEvents(entityClearMain, this);
        getServer().getPluginManager().registerEvents(teleportMain, this);
        getServer().getPluginManager().registerEvents(new WarpGuiMain(), this);
        getServer().getPluginManager().registerEvents(new CustomFactionsSendToSpawnIfLogoutInEnemyTerritory(this), this);
        getServer().getPluginManager().registerEvents(customFactionsCommandsFAlts, this);
        getServer().getPluginManager().registerEvents(antiCheatCPSCap, this);
        //getServer().getPluginManager().registerEvents(antiCheatLongTermAutoClicker, this);
        getServer().getPluginManager().registerEvents(keyMain, this);
        //getServer().getPluginManager().registerEvents(sotwJoinMain, this);
        getServer().getPluginManager().registerEvents(conquestMain, this);
    }

    //SupplyDropTimer supplyDropTimer = new SupplyDropTimer(this);
    CustomEnchantsEvents customEnchantsEvents = new CustomEnchantsEvents(this);
    GoldenApplesCooldownMain goldenApplesCooldownMain = new GoldenApplesCooldownMain(this);
    XpBoostEvents xpBoostEvents = new XpBoostEvents(this);
    DamageBoostEvents damageBoostEvents = new DamageBoostEvents(this);
    public SellBoostEvents sellBoostEvents = new SellBoostEvents(this);
    JackPotCommands jackPotCommands = new JackPotCommands(this);
    JackPotEvents jackPotEvents = new JackPotEvents(this);
    //BlackMarketCommandsAndGui blackMarketCommandsAndGui = new BlackMarketCommandsAndGui(this);
    //BlackMarketManager blackMarketManager = new BlackMarketManager(this);
    public FactionUpgradeFShield factionUpgradeFShield = new FactionUpgradeFShield(this);
    //CastleEvent castleEvent = new CastleEvent(this);
    FactionUpgradesFRally factionUpgradesFRally = new FactionUpgradesFRally(this);
    //StaffTrack staffTrack = new StaffTrack(this);
    SandBotsMain sandBotsMain = new SandBotsMain(this);
    public ClassesBard classesBard = new ClassesBard(this);
    ClassesArcher classesArcher = new ClassesArcher(this);
    //CaneBotsGui caneBotsGui = new CaneBotsGui(this);
    SellWandsMain sellWandsMain = new SellWandsMain(this);
    public CombatTimerMain combatTimerMain = new CombatTimerMain(this);
    RestrictionsMain restrictionsMain = new RestrictionsMain(this);
    public CustomFactionCommandsFLock customFactionCommandsFLock = new CustomFactionCommandsFLock(this);
    public CustomFactionCommandsFChest customFactionCommandsFChest = new CustomFactionCommandsFChest(this);
    AntiCheatNukerAndKarma antiCheatNukerAndKarma = new AntiCheatNukerAndKarma(this);
    public CustomFactionCommandsFRoster customFactionCommandsFRoster = new CustomFactionCommandsFRoster(this);
    PearlCooldownMain pearlCooldownMain = new PearlCooldownMain(this);
    //WhiteListMain whiteListMain = new WhiteListMain(this);
    ChunkBustersMain chunkBustersMain = new ChunkBustersMain();
    public RoamMain roamMain = new RoamMain(this);
    public KitsMain kitsMain = new KitsMain(this);
    LightningRodMain lightningRodMain = new LightningRodMain(this);
    AfkCheckMain afkCheckMain = new AfkCheckMain(this);
    TopDownMain topDownMain = new TopDownMain();
    EntityClearMain entityClearMain = new EntityClearMain(this);
    public TeleportMain teleportMain = new TeleportMain(this);
    public CustomFactionsCommandsFAlts customFactionsCommandsFAlts = new CustomFactionsCommandsFAlts(this);
    public CyprusTimeMain cyprusTimeMain = new CyprusTimeMain(this);
    WildMain wildMain = new WildMain(this);
    public WbAndNvMain wbAndNvMain = new WbAndNvMain(this);
    AntiCheatCPSCap antiCheatCPSCap = new AntiCheatCPSCap(this);
    //AntiCheatLongTermAutoClicker antiCheatLongTermAutoClicker = new AntiCheatLongTermAutoClicker(this);
    KeyMain keyMain = new KeyMain(this);
    SotwJoinMain sotwJoinMain = new SotwJoinMain(this);
    public ConquestMain conquestMain = new ConquestMain(this);

    private void StartOnEnableRunnablesAndFunctions() {
        //supplyDropTimer.TimmerRunnable();
        goldenApplesCooldownMain.RemoveCooldowns();
        customEnchantsEvents.BountyTimeManager();
        xpBoostEvents.LoadXpBoostHashMapsInConfig();
        xpBoostEvents.XpBoostTimeManager();
        damageBoostEvents.LoadDamageBoostHashMapsInConfig();
        damageBoostEvents.DamageBoostTimeManager();
        sellBoostEvents.LoadSellBoostHashMapsInConfig();
        sellBoostEvents.SellBoostTimeManager();
        //blackMarketManager.BlackMarketTimeManager();
        //castleEvent.PickNextCastleTimeIfNull();
        //castleEvent.TimeTilCastleTimeManager();
        //castleEvent.RunCastleTimerOnStartupIfEnabled();
        factionUpgradeFShield.FactionShieldTimeManager();
        factionUpgradesFRally.RallyCooldownTimeManager();
        factionUpgradesFRally.RallyUseTimeManager();
        sandBotsMain.LoadSandBotsFromConfig();
        sandBotsMain.SpawnSand();
        classesBard.BardEnergyManager();
        classesArcher.TaggedPlayersManager();
        //caneBotsGui.LoadCaneBotsFromConfig();
        jackPotEvents.LoadJackPotTotalFromConfig();
        combatTimerMain.CombatTimeManager();
        restrictionsMain.DisableFlyWhenEnemiesNearby();
        customFactionCommandsFLock.LoadFLockFromConfig();
        antiCheatNukerAndKarma.AntiCheatNukeAndKarmaRunnable();
        customFactionCommandsFRoster.LoadFRostersFromConfig();
        customFactionCommandsFRoster.LoadFRosterEnabledFromConfig();
        pearlCooldownMain.PearlCooldownTimeManager();
        //whiteListMain.LoadWhiteListInfoFromConfig();
        roamMain.RoamDistanceManager();
        kitsMain.KitTimeManager();
        kitsMain.LoadKitsFromConfig();
        afkCheckMain.CheckIfMoved();
        entityClearMain.ClearEntitiesTime();
        teleportMain.CancelTeleportIfMoves();
        factionUpgradeFShield.LoadFactionsBaseRegionFromConfig();
        customFactionCommandsFChest.LoadFChests();
        customFactionsCommandsFAlts.LoadFAltsFromConfig();
        cyprusTimeMain.LoadTimeFromConfig();
        teleportMain.TeleportingPlayersTimeManager();
        wildMain.WildCooldownTimeManager();
        wbAndNvMain.LoadPlayersWithEffectsFromConfig();
        antiCheatCPSCap.CPSCapTimeManager();
        //antiCheatLongTermAutoClicker.LongTermCPSCapTimeManager();
        keyMain.LoadUsedKeyFromConfig();
        sotwJoinMain.SotwJoinTimeManager();
        conquestMain.conquestTimeManager();
    }

    private void SavingFunctions() {
        xpBoostEvents.SaveXpBoostHashMapsInConfig();
        damageBoostEvents.SaveDamageBoostHashMapsInConfig();
        roamMain.RemoveAllVillagers();
    }

    private void SetUpVaultAPI() {
        if (!setupEconomy() ) {
            System.out.println(String.format("Disabled due to no Vault dependency found!"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    private static Economy econ = null;
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
    public JavaPlugin getPlugin() {
        return this;
    }
}

