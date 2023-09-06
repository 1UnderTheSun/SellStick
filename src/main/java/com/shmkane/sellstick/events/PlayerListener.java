package com.shmkane.sellstick.events;

import com.shmkane.sellstick.configs.SellstickConfig;
import com.shmkane.sellstick.utilities.ChatUtils;
import com.shmkane.sellstick.utilities.CommandUtils;
import com.shmkane.sellstick.utilities.EventUtils;
import com.shmkane.sellstick.utilities.ItemUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    @Deprecated
    @EventHandler(priority = EventPriority.MONITOR) // Checks if other plugins are using the event
    public void onSellstickUseOld(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Check if clicked block is chest, barrel or shulker
            if (EventUtils.didClickContainerWithSellStick(event)) {

                // Check if another plugin is cancelling the event
                if (event.useInteractedBlock() == Event.Result.DENY){
                    ChatUtils.sendMsg(player, SellstickConfig.instance.territoryMessage, true);
                    event.setCancelled(true);
                    return;
                }

                // Checks if Player has the permission to use a SellStick
                if (!player.hasPermission("sellstick.use")) {
                    ChatUtils.sendMsg(player, SellstickConfig.instance.noPerm, true);
                    event.setCancelled(true);
                    return;
                }

                ItemStack sellStick = player.getInventory().getItemInMainHand();

                int uses = ItemUtils.getUses(sellStick);

                double total = EventUtils.calculateContainerWorth(event);

                if (total > 0) {
                    if (EventUtils.saleEvent(player, sellStick, uses, total) && SellstickConfig.instance.sound) {

                        assert event.getInteractionPoint() != null;
                        player.playSound(event.getInteractionPoint(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
                    }
                } else {
                    ChatUtils.sendMsg(player, SellstickConfig.instance.nothingWorth, true);
                }
                event.setCancelled(true);
            }
        }
    }

    //FIXME: Check if it works as intended - not sure if a Event.setCancelled is needed for each
    @EventHandler(priority = EventPriority.MONITOR) // Checks if other plugins are using the event
    public void onSellstickUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        ItemStack sellStick = player.getInventory().getItemInMainHand();

        // Check if clicked block is chest, barrel or shulker
        if (!EventUtils.didClickSellStickBlock(event.getClickedBlock())) return;

        // Check if Item Matches UUID NBT of SellStick
        if (!ItemUtils.matchSellStickUUID(sellStick)) return;

        // Check if Item Matches Material of SellStick
        if(!ItemUtils.matchSellstickMaterial(sellStick)) {
            // Replace the item if it is an outdated item
            player.getInventory().removeItem(sellStick);
            CommandUtils.giveSellStick(player, ItemUtils.getUses(sellStick));
            return;
        }

        // Check if another plugin is cancelling the event
        if (event.useInteractedBlock() == Event.Result.DENY){
            ChatUtils.sendMsg(player, SellstickConfig.instance.territoryMessage, true);
            event.setCancelled(true);
            return;
        }
        // Checks if Player has the permission to use a SellStick
        if (!player.hasPermission("sellstick.use")) {
            ChatUtils.sendMsg(player, SellstickConfig.instance.noPerm, true);
            event.setCancelled(true);
            return;
        }

        int uses = ItemUtils.getUses(sellStick);
        double total = EventUtils.calculateContainerWorth(event);

        if (total > 0) {
            if (EventUtils.saleEvent(player, sellStick, uses, total) && SellstickConfig.instance.sound) {

                assert event.getInteractionPoint() != null;
                player.playSound(event.getInteractionPoint(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
            }
        } else {
            ChatUtils.sendMsg(player, SellstickConfig.instance.nothingWorth, true);
        }
        event.setCancelled(true);
    }
}
