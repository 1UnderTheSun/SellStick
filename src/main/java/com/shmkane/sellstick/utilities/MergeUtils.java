package com.shmkane.sellstick.utilities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.List;

public class MergeUtils {
    // Merging Sellsticks

    // Search through player inventory to get all sellsticks
    public static ItemStack[] searchInventory(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> sellsticks = new ArrayList<>();

        for (ItemStack item : inventory.getContents()) {
            if (item != null && ItemUtils.isSellStick(item)) {
                sellsticks.add(item);
            }
        }

        return sellsticks.toArray(new ItemStack[0]);
    }

    // Sum the uses of all sellsticks
    public static int sumSellStickUses(ItemStack[] sellsticks) {
        int usesSum = 0;

        for (ItemStack sellstick : sellsticks) {
            usesSum += ItemUtils.getUses(sellstick);
        }

        return usesSum;
    }

    // Remove all sellsticks from player inventory
    public static void removeSellsticks(Player player, ItemStack[] sellsticks) {
        Inventory inventory = player.getInventory();

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
