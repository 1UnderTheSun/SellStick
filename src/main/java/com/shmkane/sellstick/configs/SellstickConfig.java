package com.shmkane.sellstick.configs;

import com.shmkane.sellstick.SellStick;
import com.shmkane.sellstick.utilities.ChatUtils;
import com.shmkane.sellstick.utilities.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.List;
import java.util.logging.Level;

// Handles config.YML
public class SellstickConfig {

    // Makes new instance of SellStick configuration
    public static final SellstickConfig instance = new SellstickConfig();
    // Local instance of the configuration
    private File conf;
    public List<String> lore;
    public String displayName, PriceInterface, receiveMessage, giveMessage, nonSellingRelated, brokenStick,
            nothingWorth, territoryMessage, noPerm, sellMessage, prefix, infiniteLore, finiteLore;
    public boolean sound, glow;
    public Material material;
    SellingInterface sellInterface;

    // Load configuration values
    public void loadValues() {
        // Load Configuration
        FileConfiguration config = ConfigUtils.getConfig(this.conf);
        // Price Interface Configuration
        this.PriceInterface = ConfigUtils.tryGetString(this.conf,"PriceInterface", "PricesYML");

        // Item Configuration
        this.displayName = ConfigUtils.tryGetString(this.conf,"Item.DisplayName", "&cSellStick");
        this.material = Material.getMaterial(ConfigUtils.tryGetString(this.conf, "Item.Material".toUpperCase(), "STICK"));
        this.lore = config.getStringList("Item.StickLore");
        this.finiteLore = ConfigUtils.tryGetString(this.conf,"Item.FiniteLore", "&c%remaining% &fremaining uses");
        this.infiniteLore = ConfigUtils.tryGetString(this.conf,"Item.InfiniteLore", "&4Infinite &cuses!");
        this.glow = config.getBoolean("Item.Glow", true);
        this.sound = config.getBoolean("Item.UseSound", true);
        // Messages
        this.prefix = ConfigUtils.tryGetString(this.conf,"Messages.PluginPrefix", "&6[&eSellStick&6] ");
        this.sellMessage = ConfigUtils.tryGetString(this.conf,"Messages.SellMessage", "&cYou sold items for &f%price% &cand now have &f%balance%");
        this.noPerm = ConfigUtils.tryGetString(this.conf,"Messages.NoPermissionMessage", "&cSorry, you don''t have permission for this!");
        this.territoryMessage = ConfigUtils.tryGetString(this.conf,"Messages.InvalidTerritoryMessage", "&cYou can''t use sell stick here!");
        this.nothingWorth = ConfigUtils.tryGetString(this.conf,"Messages.NotWorthMessage", "&cNothing worth selling inside");
        this.brokenStick = ConfigUtils.tryGetString(this.conf,"Messages.BrokenStick","&cYour sellstick broke!(Ran out of uses)");
        this.nonSellingRelated = ConfigUtils.tryGetString(this.conf,"Messages.NonSellingRelated", "&cOak''s words echoed...&7There''s a time and place for everything but not now! (Right click a chest!)");
        this.receiveMessage = ConfigUtils.tryGetString(this.conf,"Messages.ReceiveMessage", "&aYou gave &e%player% &e&l%amount% &aSellSticks!" );
        this.giveMessage = ConfigUtils.tryGetString(this.conf,"Messages.GiveMessage", "&aYou''ve received &e&l%amount% &asell sticks!");
    }

    // Setup Main Configuration
    public void setup(File dir) {
        if (dir.exists() || dir.mkdirs()) {

            this.conf = new File(dir + File.separator + "config.yml");

            if (!this.conf.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(this.conf);
                try {
                    config.save(this.conf);
                } catch (Exception e) {
                    ChatUtils.log(Level.SEVERE, e.getMessage());
                }
            }

            loadValues();
            handleInterface();
        }
    }

    private void handleInterface() {
        sellInterface = setSellInterface(PriceInterface);
    }

    // Get Sell Interface
    public SellingInterface getSellInterface() {
        return sellInterface;
    }

    // Set Sell Interface
    public SellingInterface setSellInterface(String priceInterface) {
        if(priceInterface.equalsIgnoreCase("ShopGUI") && SellStick.getInstance().ShopGUIEnabled) return SellingInterface.SHOPGUI;
        if(priceInterface.equalsIgnoreCase("Essentials") && SellStick.getInstance().EssentialsEnabled) return SellingInterface.ESSWORTH;
        ChatUtils.log(Level.WARNING, "Prices.yml loaded as PriceInterface did not match the required options given.");
        return SellingInterface.PRICESYML;
    }

    // Sell Interfaces
    public enum SellingInterface {
        PRICESYML,
        ESSWORTH,
        SHOPGUI
    }

}
