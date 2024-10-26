package com.shmkane.sellstick.utilities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.List;

public class MergeUtils {
    // Search through player inventory to get all sellsticks
    public static ItemStack[] searchInventory(Player player) {
        // Get inventory
        Inventory inventory = player.getInventory();
        // List to store all sellsticks
        List<ItemStack> sellsticks = new ArrayList<>();

        // Search through inventory to find all sellsticks and store in sellsticks list
        for (ItemStack item : inventory.getContents()) {
            if (item != null && ItemUtils.matchSellStickUUID(item)) {
                sellsticks.add(item);
            }
        }

        // Return sellsticks list as an array
        return sellsticks.toArray(new ItemStack[0]);
    }

    // Sum the uses of all sellsticks
    public static int sumSellStickUses(ItemStack[] sellsticks) {
        int usesSum = 0;

        // Sum the uses of all sellsticks in sellsticks array
        for (ItemStack sellstick : sellsticks) {
            usesSum += ItemUtils.getUses(sellstick);
        }

        return usesSum;
    }

    // Remove all sellsticks from player inventory
    public static void removeSellsticks(Player player, ItemStack[] sellsticks) {
        // Get inventory
        Inventory inventory = player.getInventory();

        // Remove all sellsticks from sellsticks array
        for (ItemStack sellstick : sellsticks) {
            inventory.remove(sellstick);
        }
    }

    // Create a new sellstick with a number of uses equalling usesSum
    // public static ItemStack giveNewSellstick(int usesSum, Player player) {
    //     Player target = SellStick.getInstance().getServer().getPlayer(args[1]);
    //     CommandUtils.giveSellStick(target, usesSum);
    // }
}
