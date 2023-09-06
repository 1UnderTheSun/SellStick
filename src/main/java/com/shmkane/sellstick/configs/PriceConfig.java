package com.shmkane.sellstick.configs;

import java.io.File;
import java.util.logging.Level;

import com.shmkane.sellstick.utilities.ChatUtils;
import com.shmkane.sellstick.utilities.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// Handles Prices.YML
public class PriceConfig {

    // Instance of Price Configuration
    public static final PriceConfig instance = new PriceConfig();

    // Local instance of configuration
    private File conf;

    // Setup prices.yml
    // TODO: Make a Setup function rather than use one for each configuration
    public void setup(File dir) {
        if (dir.exists() || dir.mkdirs()) {

            this.conf = new File(dir + File.separator + "prices.yml");
            if (!this.conf.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(this.conf);

                config.set("prices.SULPHUR", 1.02);
                config.set("prices.RED_ROSE", 0.76);
                config.set("prices.LEATHER", 2.13);
                config.set("prices.COOKED_BEEF", 0.01);
                config.set("prices.BONE", 5.00);


                try {
                    config.save(this.conf);
                } catch (Exception e) {
                    ChatUtils.log(Level.SEVERE, e.getMessage());
                }
            }
        }
    }

    public FileConfiguration getConfig() {
        return ConfigUtils.getConfig(this.conf);
    }
}
