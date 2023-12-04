package com.shmkane.sellstick.events;

import com.shmkane.sellstick.configs.SellstickConfig;
import com.shmkane.sellstick.utilities.ChatUtils;
import com.shmkane.sellstick.utilities.CommandUtils;
import com.shmkane.sellstick.utilities.EventUtils;
import com.shmkane.sellstick.utilities.ItemUtils;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    /*
    @Deprecated
    @EventHandler(priority = EventPriority.MONITOR) // Checks if other plugins are using the event
    public void onSellstickUseOld(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Check if clicked block is chest, barrel or shulker
            if (EventUtils.didClickContainerWithSellStick(event)) {

                // Check if another plugin is cancelling the event
                if (event.useInteractedBlock() == Event.Result.DENY){
                    ChatUtils.sendMsg(player, SellstickConfig.territoryMessage, true);
                    event.setCancelled(true);
                    return;
                }

                // Checks if Player has the permission to use a SellStick
                if (!player.hasPermission("sellstick.use")) {
                    ChatUtils.sendMsg(player, SellstickConfig.noPerm, true);
                    event.setCancelled(true);
                    return;
                }

                ItemStack sellStick = player.getInventory().getItemInMainHand();

                int uses = ItemUtils.getUses(sellStick);

                double total = EventUtils.calculateContainerWorth(event);

                if (total > 0) {
                    if (EventUtils.saleEvent(player, sellStick, uses, total) && SellstickConfig.sound) {

                        assert event.getInteractionPoint() != null;
                        player.playSound(event.getInteractionPoint(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
                    }
                } else {
                    ChatUtils.sendMsg(player, SellstickConfig.nothingWorth, true);
                }
                event.setCancelled(true);
            }
        }
    }
    */

    //FIXME: Check if it works as intended - not sure if a Event.setCancelled is needed for each
    @EventHandler(priority = EventPriority.MONITOR) // Checks if other plugins are using the event
    public void onSellstickUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack sellStick = player.getInventory().getItemInMainHand();
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        if (sellStick.getType().isAir() || sellStick.getAmount() == 0) return;
        Block block = event.getClickedBlock();
        // Check if Item Matches UUID NBT of SellStick
        if (!ItemUtils.matchSellStickUUID(sellStick)) return;
        // Check if clicked block is chest, barrel or shulker
        if (!EventUtils.didClickSellStickBlock(player, block, event)) return;
        // Check if Item Matches Material of SellStick
        if(!ItemUtils.matchSellStickMaterial(sellStick)) {
            // Replace the item if it is an outdated item
            player.getInventory().removeItem(sellStick);
            CommandUtils.giveSellStick(player, ItemUtils.getUses(sellStick));
            return;
        }

        // Check if another plugin is cancelling the event
        if (event.useInteractedBlock() == Event.Result.DENY){
            System.out.println(6);
            ChatUtils.sendMsg(player, SellstickConfig.territoryMessage, true);
            event.setCancelled(true);
            return;
        }
        // Checks if Player has the permission to use a SellStick
        if (!player.hasPermission("sellstick.use")) {
            System.out.println(7);
            ChatUtils.sendMsg(player, SellstickConfig.noPerm, true);
            event.setCancelled(true);
            return;
        }

        int uses = ItemUtils.getUses(sellStick);
        double total = EventUtils.calculateContainerWorth(event);

        if (total > 0) {
            if (EventUtils.saleEvent(player, sellStick, uses, total) && SellstickConfig.sound) {

                assert event.getInteractionPoint() != null;
                player.playSound(event.getInteractionPoint(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
            }
        } else {
            ChatUtils.sendMsg(player, SellstickConfig.nothingWorth, true);
        }
        event.setCancelled(true);
    }
}
