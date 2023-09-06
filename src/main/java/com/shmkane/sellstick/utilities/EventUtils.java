package com.shmkane.sellstick.utilities;

import com.earth2me.essentials.IEssentials;
import com.shmkane.sellstick.configs.PriceConfig;
import com.shmkane.sellstick.configs.SellstickConfig;
import com.shmkane.sellstick.SellStick;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class EventUtils {

    public static double calculateContainerWorth(PlayerInteractEvent event) {

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
                            price = Double.parseDouble((String) entry.getValue());
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
    @Deprecated
    public static boolean didClickContainerWithSellStick(PlayerInteractEvent event) {
        ItemStack playerHand = event.getPlayer().getInventory().getItemInMainHand();

        if (ItemUtils.isSellStick(playerHand)) return false;

        Block block = event.getClickedBlock();
        return (block instanceof Chest || block instanceof Barrel || block instanceof ShulkerBox);
    }

    // Checks if clicked block is on a chest, barrel or shulker box
    public static boolean didClickSellStickBlock(Block block) {
        return (block instanceof Chest || block instanceof Barrel || block instanceof ShulkerBox);
    }

    // Handles the SellStick in SaleEvent and PostSaleEvent - (Originally Made by MrGhetto)
    public static boolean saleEvent(Player player, ItemStack sellStick, int uses, double total) {

        if (!ItemUtils.isInfinite(sellStick)) {
            ItemUtils.subtractUses(sellStick);
        }

        double multiplier = setMultiplier(player);

        EconomyResponse r;

        if (multiplier != 1) {
            r = SellStick.getInstance().getEcon().depositPlayer(player, total * multiplier);
        }

        r = SellStick.getInstance().getEcon().depositPlayer(player, total);

        boolean success = false;

        if (r.transactionSuccess()) {
            success = true;
            //FIXME: What the fuck in god's name
            if (SellstickConfig.instance.sellMessage.contains("\\n")) {
                String[] send = SellstickConfig.instance.sellMessage.split("\\\\n");
                for (String msg : send) {
                    ChatUtils.sendMsg(player, msg.replace("%balance%", SellStick.getInstance().getEcon().format(r.balance)).replace("%price%",
                            SellStick.getInstance().getEcon().format(r.amount)),true);
                }
            }
            else {
                ChatUtils.sendMsg(player, SellstickConfig.instance.sellMessage.replace("%balance%", SellStick.getInstance().getEcon().format(r.balance))
                        .replace("%price%", SellStick.getInstance().getEcon().format(r.amount)), true);
            }

            ChatUtils.log(Level.INFO,player.getName() + " sold items via SellStick for " + r.amount + " and now has " + r.balance);
        }
        else {
            ChatUtils.sendMsg(player, String.format("An error occurred: %s", r.errorMessage), true);
        }

        if (uses - 1 == 0) {
            player.getInventory().remove(player.getInventory().getItemInMainHand());
            ChatUtils.sendMsg(player, SellstickConfig.instance.brokenStick, true);
        }

        return success;
    }

    static double setMultiplier(Player player) {
        /*
         * Permissions based multiplier check. If user doesn't have access to
         * sellstick.multiplier.x permission Multiplier defaults to 1 as seen below.
         */
        double multiplier = 1;

        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if (perm.getPermission().startsWith("sellstick.multiplier")) {
                String stringPerm = perm.getPermission();
                String permSection = stringPerm.replaceAll("sellstick.multiplier.", "");
                if (Double.parseDouble(permSection) > multiplier) {
                    multiplier = Double.parseDouble(permSection);
                }
            }
        }
        return multiplier;
    }
}
