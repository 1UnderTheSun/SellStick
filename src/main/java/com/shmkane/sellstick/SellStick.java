package com.shmkane.sellstick;

import com.shmkane.sellstick.Configs.PriceConfig;
import com.shmkane.sellstick.Configs.StickConfig;
import com.shmkane.sellstick.Events.PlayerListener;
import com.shmkane.sellstick.Utilities.ChatUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

/**
 * SellStick is a Minecraft plugin that allows customizable selling of
 * chest contents.
 *
 * @author shmkane
 */

public class SellStick extends JavaPlugin {

    /**
     * Instance of Vault Economy
     **/
    private static Economy econ = null;
    /**
     * Instance of Plugin
     */
    public static SellStick plugin;

    public boolean ShopGUIEnabled = false;

    public boolean EssentialsEnabled = false;

    /**
     * Initial plugin setup. Creation and loading of YML files.
     * <p>
     * Creates / Loads Config.yml
     * Creates / Loads Prices.yml
     * Saves Current Config.
     * Create instance of Essentials
     * Hook SellStickCommand executor
     */
    @Override
    public void onEnable() {

        // Don't load plugin if Vault is not present
        if (!setupEconomy()) {
            ChatUtils.log(Level.SEVERE,"[%s] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        loadDependencies();
        loadClasses();
    }

    public void reload() {
        // Update config file
        reloadConfig();
        // Update dependencies
        loadDependencies();
        // Update config vars
        StickConfig.instance.setup(getDataFolder());
        PriceConfig.instance.setup(getDataFolder());
    }

    public void loadDependencies() {
        // Check Soft Dependencies
        ShopGUIEnabled = Bukkit.getPluginManager().isPluginEnabled("ShopGuiPlus");
        EssentialsEnabled = Bukkit.getPluginManager().isPluginEnabled("Essentials");
    }

    public void loadClasses() {
        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        // Register Commands
        getCommand("sellstick").setExecutor(new SellStickCommand());

    }

    /**
     * Attempt to disable plugin. Reset the values of some instance variables.
     */
    @Override
    public void onDisable() {
    }

    /**
     * Attempts to hook into Vault.
     *
     * @return If vault is available or not.
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    /**
     * Returns an instance of vault.
     *
     * @return Economy Instance
     */
    public Economy getEcon() {
        return SellStick.econ;
    }

    public static SellStick getInstance() {
        return SellStick.plugin;
    }

}
