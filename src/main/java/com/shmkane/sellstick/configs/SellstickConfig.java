package com.shmkane.sellstick.configs;

import com.shmkane.sellstick.SellStick;
import com.shmkane.sellstick.utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

// Handles config.YML
public class SellstickConfig extends Config {

    public static List<String> lore;
    public static String displayName, PriceInterface, receiveMessage, giveMessage, nonSellingRelated, brokenStick,
            nothingWorth, territoryMessage, noPerm, sellMessage, prefix, infiniteLore, finiteLore;
    public static boolean sound, glow;
    public static Material material;
    static PriceSource priceSource;

    public SellstickConfig(String configName, File dataFolder) {
        super(configName, dataFolder);
    }

    // Setup Main Configuration
    public void setup(File dir) {
        super.setup(dir);
        setPriceSource(PriceInterface);
    }

    // Load configuration values
    void loadValues(FileConfiguration config) {
        // Price Interface Configuration
        PriceInterface = tryGetString(conf,"PriceInterface", "PricesYML");

        // Item Configuration
        displayName = tryGetString(conf,"Item.DisplayName", "&cSellStick");
        material = Material.getMaterial(tryGetString(conf, "Item.Material".toUpperCase(), "STICK"));
        lore = config.getStringList("Item.StickLore");
        finiteLore = tryGetString(conf,"Item.FiniteLore", "&c%remaining% &fremaining uses");
        infiniteLore = tryGetString(conf,"Item.InfiniteLore", "&4Infinite &cuses!");
        glow = config.getBoolean("Item.Glow", true);
        sound = config.getBoolean("Item.UseSound", true);
        // Messages
        prefix = tryGetString(conf,"Messages.PluginPrefix", "&6[&eSellStick&6] ");
        sellMessage = tryGetString(conf,"Messages.SellMessage", "&cYou sold items for &f%price% &cand now have &f%balance%");
        noPerm = tryGetString(conf,"Messages.NoPermissionMessage", "&cSorry, you don''t have permission for this!");
        territoryMessage = tryGetString(conf,"Messages.InvalidTerritoryMessage", "&cYou can''t use sell stick here!");
        nothingWorth = tryGetString(conf,"Messages.NotWorthMessage", "&cNothing worth selling inside");
        brokenStick = tryGetString(conf,"Messages.BrokenStick","&cYour sellstick broke!(Ran out of uses)");
        nonSellingRelated = tryGetString(conf,"Messages.NonSellingRelated", "&cOak''s words echoed...&7There''s a time and place for everything but not now! (Right click a chest!)");
        receiveMessage = tryGetString(conf,"Messages.ReceiveMessage", "&aYou gave &e%player% &e&l%amount% &aSellSticks!" );
        giveMessage = tryGetString(conf,"Messages.GiveMessage", "&aYou''ve received &e&l%amount% &asell sticks!");
    }


    public static PriceSource getPriceSource() {
        return priceSource;
    }

    public void setPriceSource(String priceString) {
        if(priceString.equalsIgnoreCase("ShopGUI") && SellStick.getInstance().ShopGUIEnabled)
            priceSource = PriceSource.SHOPGUI;
        if(priceString.equalsIgnoreCase("Essentials") && SellStick.getInstance().EssentialsEnabled)
            priceSource = PriceSource.ESSWORTH;
        ChatUtils.log(Level.WARNING, "PriceInterface did not match any option. Defaulting to prices.yml.");
        priceSource = PriceSource.PRICESYML;
    }

    public enum PriceSource {
        PRICESYML,
        ESSWORTH,
        SHOPGUI
    }

}
