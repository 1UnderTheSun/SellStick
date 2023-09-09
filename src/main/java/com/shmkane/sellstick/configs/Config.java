package com.shmkane.sellstick.configs;

import com.shmkane.sellstick.utilities.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

public abstract class Config {

    // The filename used for saving/loading
    protected String configFilename;

    // Local instance of configuration
    protected static File conf;

    Config(String configName, File dataFolder) {
        configFilename = configName + ".yml";
        setup(dataFolder);
    }

    // Setup Main Configuration
    public void setup(File dir) {
        if (dir.exists() || dir.mkdirs()) {

            conf = new File(dir + File.separator + configFilename);
            if (!conf.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(conf);

                loadValues(config);

                try {
                    config.save(conf);
                } catch (Exception e) {
                    ChatUtils.log(Level.SEVERE, e.getMessage());
                }
            }
        }
    }

    void loadValues(FileConfiguration config) {

    }

    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(conf);
    }

    String tryGetString(File yamlConfiguration, String sField, String sDefault) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(yamlConfiguration);
        // Missing field
        if (!config.contains(sField)) {
            ChatUtils.log(Level.WARNING, "Adding missing " + sField + " to " + yamlConfiguration.getName());
            config.set(sField, sDefault);
            try {
                config.save(yamlConfiguration);
            } catch (Exception e) {
                ChatUtils.log(Level.SEVERE, e.getMessage());
            }
        }
        // Try load value, otherwise use default
        try {
            return config.getString(sField);
        } catch(NullPointerException e) {
            ChatUtils.log(Level.SEVERE, "Error loading value from" + sField + " in " + yamlConfiguration.getName());
            return sDefault;
        }
    }
}
