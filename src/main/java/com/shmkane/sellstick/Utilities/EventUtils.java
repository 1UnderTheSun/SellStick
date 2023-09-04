package com.shmkane.sellstick.Utilities;

import com.earth2me.essentials.IEssentials;
import com.shmkane.sellstick.Configs.PriceConfig;
import com.shmkane.sellstick.Configs.StickConfig;
import com.shmkane.sellstick.SellStick;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.exception.player.PlayerDataNotLoadedException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class EventUtils {

    public static boolean isSellStick(Player p, PlayerInteractEvent e) {
        Material sellItem;
        try {
            sellItem = Material.getMaterial(StickConfig.instance.item.toUpperCase());
        } catch (Exception ex) {
            SellStick.getInstance().log(Level.SEVERE, "Invalid SellStick item set in config.");
            return false;
        }

        return p.getItemInHand().getType() == sellItem && p.getItemInHand().getItemMeta().getDisplayName() != null
                && p.getItemInHand().getItemMeta().getDisplayName().startsWith(StickConfig.instance.name);

    }

    /**
     * Now check the worth of what we're about to sell.
     *
     * @param c Inventory Holder is a chest
     * @param e Triggers on a playerinteract event
     * @return the worth
     */
    @SuppressWarnings("deprecation")
    public static double calculateWorth(InventoryHolder c, PlayerInteractEvent e) {

        ItemStack[] contents = c.getInventory().getContents();

        double total = 0;
        double slotPrice = 0;
        double price = 0;

        StickConfig.SellingInterface si = StickConfig.instance.getSellInterface();

        if (StickConfig.instance.debug) {
            SellStick.getInstance().log(Level.WARNING,"1-Getting prices from " + si);
            SellStick.getInstance().log(Level.WARNING,"2-Clicked Chest(size=" + c.getInventory().getSize() + "):");
        }

        for (int i = 0; i < c.getInventory().getSize(); i++) {

            try {
                if (si == StickConfig.SellingInterface.PRICESYML) { // Not essW, not shop

                    for (String key : PriceConfig.instance.getPrices()) {

                        int data;
                        String name;

                        if (!key.contains(":")) {
                            data = 0;
                            name = key;
                        } else {
                            name = (key.split(":"))[0];
                            data = Integer.parseInt(key.split(":")[1]);
                        }

                        if ((contents[i].getType().toString().equalsIgnoreCase(name)
                                || (ChatUtils.isNumeric(name) && contents[i].getType().getId() == Integer.parseInt(name)))
                                && contents[i].getDurability() == data) {
                            price = Double.parseDouble(PriceConfig.instance.getConfig().getString("prices." + key));

                        }

                        if (StickConfig.instance.debug) {
                            if (price > 0) {
                                SellStick.getInstance().log(Level.WARNING,contents[i].getType() + " x " + contents[i].getAmount());
                                SellStick.getInstance().log(Level.WARNING,"-Price: " + price);
                            }
                        }

                    }
                } else if (si == StickConfig.SellingInterface.ESSWORTH) {
                    try {
                        Object ess = SellStick.getInstance().getServer().getPluginManager().getPlugin("Essentials");

                        price = ((IEssentials) ess).getWorth().getPrice((IEssentials) ess, contents[i]).doubleValue();

                        if (StickConfig.instance.debug) {
                            if (price > 0)
                                SellStick.getInstance().log(Level.WARNING,"-Price: " + price);
                            SellStick.getInstance().log(Level.WARNING,contents[i].getType() + " x " + contents[i].getAmount());
                        }


                    } catch (Exception exception) {
                        SellStick.getInstance().log(Level.WARNING, "Something went wrong enabling Essentials. If you don't use it, you can ignore this message.");
                    }

                } else if (si == StickConfig.SellingInterface.SHOPGUI) {

                    price = ShopGuiPlusApi.getItemStackPriceSell(e.getPlayer(), contents[i]);

                    if (price < 0) {
                        price = 0;
                    }

                    if (StickConfig.instance.debug) {
                        if (price > 0)
                            SellStick.getInstance().log(Level.WARNING,"-Price: " + price);
                        SellStick.getInstance().log(Level.WARNING,contents[i].getType() + " x " + contents[i].getAmount());
                    }

                }

                if (StickConfig.instance.debug) {
                    SellStick.getInstance().log(Level.WARNING,"--Price of (" + contents[i].getType() + "): " + price);
                }

                int amount;
                if (si != StickConfig.SellingInterface.SHOPGUI) {
                    amount = contents[i].getAmount();
                } else {
                    amount = 1;
                }
                slotPrice = price * amount;

                if (slotPrice > 0) {
                    ItemStack sell = contents[i];
                    c.getInventory().remove(sell);
                    e.getClickedBlock().getState().update();
                }

            } catch (Exception ex) {

                if (StickConfig.instance.debug) {
                    if (!(ex instanceof NullPointerException))
                        SellStick.getInstance().log(Level.WARNING, ex.toString());
                }

                if (si == StickConfig.SellingInterface.SHOPGUI && ex instanceof PlayerDataNotLoadedException) {
                    SellStick.getInstance().log(Level.SEVERE,"Player should relog to fix this.");
                    e.getPlayer().sendMessage(ChatColor.DARK_RED + "Please re-log to use SellStick.");
                    return 0;
                }
            }
            if (StickConfig.instance.debug && slotPrice > 0) {
                SellStick.getInstance().log(Level.WARNING,"---slotPrice=" + slotPrice);
                SellStick.getInstance().log(Level.WARNING,"---total=" + total);
            }
            total += slotPrice;
            slotPrice = 0;
            price = 0;
        }
        if (StickConfig.instance.debug)
            System.out.println();

        return total;
    }

    /**
     * Method for checking if a player just clicked a chest with a sellstick
     *
     * @param p Player that clicked the chest
     * @param e On a player interact event
     * @return True if the item in hand was a sellstick && player clicked a chest
     */
    public static boolean didClickChestWithSellStick(Player p, PlayerInteractEvent e) {
        Material sellItem;
        try {
            sellItem = Material.getMaterial(StickConfig.instance.item.toUpperCase());
        } catch (Exception ex) {
            SellStick.getInstance().log(Level.SEVERE, "Invalid SellStick item set in config.");
            return false;
        }

        if (p.getItemInHand().getType() == sellItem) {
            p.getItemInHand().getItemMeta().getDisplayName();
            if (p.getItemInHand().getItemMeta().getDisplayName().startsWith(StickConfig.instance.name)) {
                return e.getClickedBlock().getType() == Material.CHEST
                        || e.getClickedBlock().getType() == Material.TRAPPED_CHEST;
            }
        }

        return false;
    }
}
