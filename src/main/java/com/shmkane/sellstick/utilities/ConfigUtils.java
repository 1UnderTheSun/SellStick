package com.shmkane.sellstick.utilities;

import com.shmkane.sellstick.SellStick;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ConfigUtils {

    public static FileConfiguration getConfig(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    public static String tryGetString(File yamlConfiguration, String sField, String sDefault) {
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
