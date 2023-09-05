package com.shmkane.sellstick.Configs;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// Handles Prices.YML
public class PriceConfig {

    // Instance of Price Configuration
    public static PriceConfig instance = new PriceConfig();

    // Local instance of configuration
    private File conf;

    // Setup prices.yml
    // TODO: Make a Setup function rather than use one for each configuration
    public void setup(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.conf = new File(dir + File.separator + "prices.yml");
        if (!this.conf.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(this.conf);

            config.set("prices.SULPHUR", 1.02);
            config.set("prices.RED_ROSE", 0.76);
            config.set("prices.LEATHER", 2.13);
            config.set("prices.COOKED_BEEF", 0.01);
            config.set("prices.BONE", 5.00);
            config.set("prices.stOnE", 0.234);
            config.set("prices.STONE:2", 0.22);
            config.set("prices.STONE:3", 0.02);

            try {
                config.save(this.conf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Return Prices.Yml
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(this.conf);
    }

    // Write Config File
    // TODO: Use function and make it have no errors
    public void write(File dir, String loc, Object obj) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.conf = new File(dir + File.separator + "prices.yml");

        getConfig().set(loc, obj);
        try {
            getConfig().save(this.conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
