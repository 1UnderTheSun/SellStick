package com.shmkane.sellstick;

import com.earth2me.essentials.Essentials;
import com.shmkane.sellstick.Configs.PriceConfig;
import com.shmkane.sellstick.Configs.StickConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SellStick is a MC plugin that allows customizable selling of
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

        if (!setupEconomy()) {
            this.log("SEVERE",(String.format("[%s] - Disabled due to no Vault dependency found!")));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setupEssentials();

        /**
         * Check Soft Dependencies
         */
        if (Bukkit.getPluginManager().isPluginEnabled("ShopGuiPlus")) {
            if (!StickConfig.instance.useShopGUI) {
                this.log("WARNING", String.format("[%s] ShopGUI+ was found but not enabled in the config!"));
            }
        }

        this.saveDefaultConfig();

        StickConfig.instance.setup(getDataFolder());
        PriceConfig.instance.setup(getDataFolder());

        this.getCommand("sellstick").setExecutor(new SellStickCommand(this));
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    /**
     * Attempt to disable plugin. Reset the values of some instance variables.
     */
    @Override
    public void onDisable() {
        this.log("WARNING",(String.format("[%s] - Attempting to disabling...")));
        try {
            econ = null;
        } catch (Exception ex) {
            this.log("SEVERE",(String.format("[%s] - Was not disabled correctly!")));
        } finally {
            this.log("WARNING",(String.format("[%s] - Attempt complete!")));
        }
    }

    /**
     * Checks if Essentials is available to be hooked into.
     */
    public void setupEssentials() {

        try {
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                this.log("Level.INFO",(String.format("[%s] Essentials was found")));
                Essentials ess = Essentials.getPlugin(Essentials.class);
                if (StickConfig.instance.useEssentialsWorth) {
                    if (ess == null) {
                        this.log("WARNING",(String.format("[%s] Trying to use essentials worth but essentials not found!")));
                    } else {
                        this.log("INFO",String.format("[%s] Using essentials worth!"));
                    }
                }
            }else{
                this.log("WARNING",(String.format("[%s] Essentials not found")));
            }
        } catch (Exception ex) {
            this.log("WARNING", String.format("Something went wrong enabling Essentials. If you don't use it, you can ignore this message:"));
            this.log("WARNING",(ex.getMessage()));
        }
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
        return econ != null;
    }

    /**
     * Returns an instance of vault.
     *
     * @return
     */
    public Economy getEcon() {
        return SellStick.econ;
    }
    /**
     * This will send a player a message. If message is empty, it wont send
     * anything.
     *
     * @param sender The target player
     * @param msg    the message
     */
    public void msg(CommandSender sender, String msg) {
        if (msg.length() == 0) {
            return;
        }

        sender.sendMessage(StickConfig.instance.prefix + msg);
    }

    /**
     * Server logger
     **/
    public void log(String string) {
        getLogger().log(Level.INFO, string);
    }

    public void log(String level, String string) {
        Level l = Level.INFO;
        switch(level) {
            case "WARNING": l = Level.WARNING; break;
            case "SEVERE": l = Level.SEVERE; break;
            case "ALL": l = Level.ALL; break;
        }
        getLogger().log(l, string);
    }

}
