package com.shmkane.sellstick.Utilities;

import com.earth2me.essentials.IEssentials;
import com.shmkane.sellstick.Configs.PriceConfig;
import com.shmkane.sellstick.Configs.SellstickConfig;
import com.shmkane.sellstick.SellStick;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import java.util.Objects;

public class EventUtils {

    //TODO: Reduce Code
    public static double calculateContainerWorth(PlayerInteractEvent event) {

        //TODO: Check if getState is actually required?
        InventoryHolder container = (InventoryHolder) Objects.requireNonNull(event.getClickedBlock()).getState();

        ItemStack[] containerContents = container.getInventory().getContents();

        double total = 0;

        SellstickConfig.SellingInterface sellInterface = SellstickConfig.instance.getSellInterface();

        for (ItemStack itemstack : containerContents) {
            // Reset each variable on each itemstack
            double price = 0;
            double slotPrice;

            switch (sellInterface) {
                case PRICESYML:
                    ConfigurationSection pricesSection = PriceConfig.instance.getConfig().getConfigurationSection("prices");

                    // Initialize a map to store prices
                    assert pricesSection != null;
                    Map<String, Object> prices = pricesSection.getValues(false);

                    // Check Price of ItemStack
                    for (Map.Entry<String, Object> entry : prices.entrySet()) {
                        if(itemstack.getType().toString().equalsIgnoreCase(entry.getKey())) {
                            // TODO: Add a check if someone is stupid enough to make the block value a string...
                            price = (Double) entry.getValue();
                        }
                    }

                    break;
                case SHOPGUI:
                    price = ShopGuiPlusApi.getItemStackPriceSell(itemstack);

                    if (price < 0) {
                        price = 0;
                    }

                    break;
                case ESSWORTH:
                    IEssentials ess = (IEssentials) SellStick.getInstance().getServer().getPluginManager().getPlugin("Essentials");

                    assert ess != null;
                    price = ess.getWorth().getPrice(ess, itemstack).doubleValue();

                    break;
            }

            int amount = itemstack.getAmount();

            // ShopGUI already implements amount within the API
            if(sellInterface == SellstickConfig.SellingInterface.SHOPGUI) {
                amount = 1;
            }

            slotPrice = price * amount;

            if(slotPrice > 0) {
                container.getInventory().remove(itemstack);
            }
            total += slotPrice;
        }
        return total;
    }

    // Checks if clicked block is on a chest, barrel or Shulker Box with a SellStick
    public static boolean didClickContainerWithSellStick(PlayerInteractEvent event) {

        ItemStack playerHand = event.getPlayer().getInventory().getItemInMainHand();

        if (!ItemUtils.isSellStick(playerHand)) return false;

        Block block = event.getClickedBlock();
        return (block instanceof Chest || block instanceof Barrel || block instanceof ShulkerBox);
    }
}
