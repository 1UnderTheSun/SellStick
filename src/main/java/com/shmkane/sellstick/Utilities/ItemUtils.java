package com.shmkane.sellstick.Utilities;

import com.shmkane.sellstick.Configs.StickConfig;
import com.shmkane.sellstick.SellStick;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ItemUtils {
    /**
     * @param itemStack Accepts an itemstack
     * @return returns an enchanted item with durability 1(unbreaking)
     */
    public static ItemStack glow(ItemStack itemStack) {
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return itemStack;
    }

    /**
     * Determines if the stick is infinite. An infinte stick means that it can be
     * used regardless of it's durabaility. For that matter, it's infinite if the
     * item lore matches the infinite lore stated in the config.
     * <p>
     * IMPORTANT: If you change the infiniteLore in the config, you may break the
     * sticks with the old lore.
     *
     * @param lores Given these lores from the stick
     * @return True if infinite stick
     */
    public static boolean isInfinite(List<String> lores) {
        return lores.get(StickConfig.instance.durabilityLine - 1).equalsIgnoreCase(StickConfig.instance.infiniteLore);
    }

    /**
     * This method was created incase someone wants to put "%remaining uses out of
     * 50% Where the last int is NOT the remaining uses.
     * <p>
     * There's probably a more efficient way to do this but I haven't gotten around
     * to recoding it and it hasn't given an issue yet.
     *
     * @param lores Takes a string list
     * @return finds the uses in the lores and returns as int.
     */
    public static int getUsesFromLore(List<String> lores) {

        /*
         * Get all the lores. lore[1] contains the uses/infinite info. We need to get
         * the uses. The reason for all of this is incase someone puts %remaining% uses
         * out of 50, in the config. We need to be able to get the first number(or the
         * lower number in most cases) because an annoying person will put out of 50 you
         * have %remaining% uses left. Again, the only purpose of this is to Get the #
         * of uses if theres multiple numbers in the lore We loop through the
         * String(lore at index 1) and check all the indexes
         */

        String found = parseDurabilityLine(lores);

        // We now take that found string, and split it at every "-"
        String[] split = found.split("-");

        List<Integer> hold = new ArrayList<Integer>();

        // We take the split array, and loop thru it
        for (int i = 0; i < split.length; i++) {
            // If we find a number in it,
            if (ChatUtils.isNumeric(split[i])) {
                // We hold onto that number
                hold.add(Integer.parseInt(split[i]));
            }
        }
        // Now we just do a quick loop through the hold array and find the
        // lowest number.

        int min = -2;
        try {
            min = hold.get(0);
        } catch (Exception ex) {
            SellStick.getInstance().log(Level.SEVERE, StickConfig.instance.durabilityLine + "");
            SellStick.getInstance().log(Level.SEVERE, "The problem seems to be that your sellstick useline number has changed.");
            SellStick.getInstance().log(Level.SEVERE, ex.toString());
        }

        for (int i = 0; i < hold.size(); i++) {
            if (hold.get(i) < min) {
                min = hold.get(i);
            }
        }
        return min;
    }

    /**
     * Loops through the lores and determines if lines are valid and if they're
     * color codes.
     * <p>
     * This method will go through and find a digit (i). If a digit is found, get
     * the char before it (i-1). if (i-1) is the color_char(�).
     * <p>
     * It will make a string that looks something like "----a--b-4--" where dashes
     * represent something, and everything that remaining represents color codes and integer values.
     * In short, everything that's not a color code is turned into a dash.
     * Then we go figure out which one was a color code and which one was an actual int (for the dura)
     * <p>
     * It's not worth understanding how this method works if I'm being honest. Just
     * don't break it.
     * <p>
     * Also parses the durability from all it.
     * <p>
     * Tried to make this as idiot-proof as possible, but made code sloppy
     *
     * @param lores Lores of the sellstick
     * @return a specially "encrypted" string that can be read to make sense by
     * further methods.
     */
    public static String parseDurabilityLine(List<String> lores) {
        StringBuilder found = new StringBuilder();
        int duraLine = StickConfig.instance.durabilityLine;

        for (int i = 0; i < lores.get(duraLine - 1).length(); i++) {
            if (Character.isDigit(lores.get(duraLine - 1).charAt(i))) {

                if (i != 0) {
                    // Check to see if the index before is the & sign (If its a color code)
                    if (lores.get(duraLine - 1).charAt(i - 1) != ChatColor.COLOR_CHAR) {
                        // And if it isnt, keep track of it
                        found.append(lores.get(duraLine - 1).charAt(i));
                    } else {
                        // If it IS a color code, simply ignore it
                        found.append("-");
                    }
                    // But if it's index == 0
                } else {
                    // There can't be a & before it, so keep track of it
                    found.append(lores.get(duraLine - 1).charAt(i));
                }
            } else {
                // Otherwise we insert a "-"
                found.append("-");
            }
        }

        return found.toString();
    }

    /**
     * Gets uses from the lores
     *
     * @param p     Send message to this player
     * @param lores SellStick lores List
     * @return returns the number of uses the stick has
     */
    public static int handleUses(Player p, List<String> lores) {
        int uses = -1;
        if (!ItemUtils.isInfinite(lores)) {
            uses = ItemUtils.getUsesFromLore(lores);
        }
        if (uses == -2) {
            // This should honestly never happen unless someone changes sellstick lores
            ChatUtils.msg(p, NamedTextColor.RED + "There was an error!");
            ChatUtils.msg(p, NamedTextColor.RED + "Please let an admin know to check console, or, send them these messages:");

            ChatUtils.msg(p,
                    NamedTextColor.RED + "Player has a sellstick that has had its 'DurabilityLine' changed in the config");
            ChatUtils.msg(p, NamedTextColor.RED
                    + "For this reason, the plugin could not find the line number on which the finite/infinite lore exists");
            ChatUtils.msg(p, NamedTextColor.RED + "This can be resolved by either:");
            ChatUtils.msg(p, NamedTextColor.RED + "1: Giving the player a new sellstick");
            ChatUtils.msg(p, NamedTextColor.RED + "(Includes anyone on the server that has this issue)");
            ChatUtils.msg(p, NamedTextColor.RED + "or");
            ChatUtils.msg(p, NamedTextColor.RED + "2: Changing the DurabilityLine to match the one that is on this sellstick");

            ChatUtils.msg(p, NamedTextColor.RED + "For help, contact shmkane on spigot or github");
            ChatUtils.msg(p, NamedTextColor.RED + "But shmkane will just tell you to do one of the above options.");

        }
        return uses;
    }

    /**
     * Handles the sellstick after the 'sale' has been made.
     *
     * @param lores Lores of the sellstick
     * @param uses  number of uses
     * @param p     The player who used it
     * @param total how much was sold
     * @param im    Item meta of the stick
     * @param is    Item stack object of the stick
     * @author MrGhetto
     */
    @SuppressWarnings("deprecation")
    public static boolean postSale(List<String> lores, int uses, Player p, double total, ItemMeta im, ItemStack is) {

        if (!ItemUtils.isInfinite(lores)) {

            lores.set(StickConfig.instance.durabilityLine - 1,
                    lores.get(StickConfig.instance.durabilityLine - 1).replaceAll(uses + "", (uses - 1) + ""));
            im.setLore(lores);
            is.setItemMeta(im);
        }

        /*
         * Permissions based multiplier check. If user doesn't have
         * sellstick.multiplier.x permission Multiplier defaults to 1 as seen below.
         */
        double multiplier = Double.NEGATIVE_INFINITY;

        for (PermissionAttachmentInfo perm : p.getEffectivePermissions()) {
            if (perm.getPermission().startsWith("sellstick.multiplier")) {
                String stringPerm = perm.getPermission();
                String permSection = stringPerm.replaceAll("sellstick.multiplier.", "");
                if (Double.parseDouble(permSection) > multiplier) {
                    multiplier = Double.parseDouble(permSection);
                }
            }
        }

        /*
         * Multiplier set to Double.NEGATIVE_INFINITY by default to signal "unchanged"
         * Problem with defaulting to 0 is total*0 = 0, Problem with defaulting to 1 is
         * multipliers < 1.
         */
        EconomyResponse r;

        if (multiplier == Double.NEGATIVE_INFINITY) {
            r = SellStick.getInstance().getEcon().depositPlayer(p, total);
        } else {
            r = SellStick.getInstance().getEcon().depositPlayer(p, total * multiplier);
        }

        boolean success = false;

        if (r.transactionSuccess()) {
            success = true;
            if (StickConfig.instance.sellMessage.contains("\\n")) {
                String[] send = StickConfig.instance.sellMessage.split("\\\\n");
                for (String msg : send) {
                    ChatUtils.msg(p, msg.replace("%balance%", SellStick.getInstance().getEcon().format(r.balance)).replace("%price%",
                            SellStick.getInstance().getEcon().format(r.amount)));
                }
            } else {
                ChatUtils.msg(p, StickConfig.instance.sellMessage.replace("%balance%", SellStick.getInstance().getEcon().format(r.balance))
                        .replace("%price%", SellStick.getInstance().getEcon().format(r.amount)));
            }

            SellStick.getInstance().log(Level.INFO,p.getName() + " sold items via sellstick for " + r.amount + " and now has " + r.balance);
        } else {
            ChatUtils.msg(p, String.format("An error occured: %s", r.errorMessage));
        }

        if (uses - 1 == 0) {
            p.getInventory().remove(p.getItemInHand());
            p.updateInventory();
            ChatUtils.msg(p, StickConfig.instance.brokenStick);
        }

        return success;

    }
}
