package com.shmkane.sellstick.Configs;

import com.shmkane.sellstick.SellStick;
import com.shmkane.sellstick.Utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

// Handles config.YML
public class SellstickConfig {

    // Makes new instance of SellStick configuration
    public static SellstickConfig instance = new SellstickConfig();
    // Local instance of the configuration
    private File conf;
    public List<String> lore;
    public String displayName, PriceInterface, receiveMessage, giveMessage, nonSellingRelated, brokenStick,
            nothingWorth, territoryMessage, noPerm, sellMessage, prefix, infiniteLore, finiteLore;;
    public boolean sound, debug, glow;
    public Material material;
    SellingInterface sellInterface;

    //TODO: Fix all configuration to MiniMessage
    // Load configuration values
    public void loadValues() {
        // Load Configuration
        FileConfiguration config = YamlConfiguration.loadConfiguration(this.conf);
        // Name, Material and Interface
        this.displayName = config.getString("DisplayName");
        this.material = Material.getMaterial(Objects.requireNonNull(config.getString("Material")).toUpperCase());
        this.PriceInterface = config.getString("PriceInterface");
        // Messages
        this.receiveMessage = config.getString("ReceiveMessage");
        this.giveMessage = config.getString("GiveMessage");
        this.brokenStick = config.getString("BrokenStick");
        this.territoryMessage = config.getString("InvalidTerritoryMessage");
        this.prefix = config.getString("MessagePrefix");
        this.sellMessage = config.getString("SellMessage");
        this.noPerm = config.getString("NoPermissionMessage");
        this.nothingWorth = config.getString("NotWorthMessage");
        this.nonSellingRelated = config.getString("NonSellingRelated");
        // Lore
        this.lore = config.getStringList("StickLore");
        this.finiteLore = config.getString("FiniteLore");
        this.infiniteLore = config.getString("InfiniteLore");
        // Booleans
        this.glow = config.getBoolean("Glow");
        this.sound = config.getBoolean("UseSound");
        this.debug = config.getBoolean("debug");
    }

    // Setup Main Configuration
    public void setup(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.conf = new File(dir + File.separator + "config.yml");

        if (!this.conf.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(this.conf);

            config.set("DisplayName", "&cSellStick");
            config.set("ItemType", "STICK");
            config.get("Glow", true);

            List<String> lore = Arrays.asList("&c&lLeft&c click on a chest to sell items inside!",
                    "&cSellStick by &oshmkane");
            config.set("StickLore", lore);
            config.set("FiniteLore", "&c%remaining% &fremaining uses");
            config.set("InfiniteLore", "&4Infinite &cuses!");
            config.set("DurabilityLine", 2);
            config.set("MessagePrefix", "&6[&eSellStick&6] &e");
            config.set("SellMessage", "&cYou sold items for &f%price% &cand now have &f%balance%");
            config.set("NoPermissionMessage", "&cSorry, you don't have permission for this!");
            config.set("InvalidTerritoryMessage", "&cYou can't use sell stick here!");
            config.set("NotWorthMessage", "&cNothing worth selling inside");
            config.set("BrokenStick", "&cYour sellstick broke!(Ran out of uses)");
            config.set("NonSellingRelated", "&cOak's words echoed...&7There's a time and place for everything but not now!");
            config.set("GiveMessage", "&aYou gave &e%player%& &e&l%amount% &asell sticks!");
            config.set("ReceiveMessage", "&aYou've received &e&l%amount% &asell sticks!");
            config.set("UseEssentialsWorth", false);
            config.set("UseShopGUI", false);
            config.set("UseSound", true);
            config.set("debug", false);

            try {
                config.save(this.conf);
            } catch (Exception e) {
                ChatUtils.log(Level.SEVERE, e.getMessage());
            }
        }
        loadValues();
        handleInterface();
    }

    private void handleInterface() {
        sellInterface = setSellInterface(PriceInterface);
    }

    // Return instance of the config
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(this.conf);
    }

    // Write instance of the configuration
    public void write(File dir, String loc, Object obj) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.conf = new File(dir + File.separator + "config.yml");

        getConfig().set(loc, obj);
        try {
            getConfig().save(this.conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadValues();
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
