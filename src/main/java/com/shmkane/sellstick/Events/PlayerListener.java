package com.shmkane.sellstick.Events;

import com.shmkane.sellstick.Configs.SellstickConfig;
import com.shmkane.sellstick.Utilities.ChatUtils;
import com.shmkane.sellstick.Utilities.EventUtils;
import com.shmkane.sellstick.Utilities.ItemUtils;
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
    public void onSellstickUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        //TODO: Reduce Nested Ifs
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Check if clicked block is chest, barrel or shulker
            if (EventUtils.didClickContainerWithSellStick(event)) {

                // Check if another plugin is cancelling the event
                if(event.useInteractedBlock() == Event.Result.DENY){
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
                    //TODO: Change to component for lores
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

    //FIXME: Check if it works as intended
    @EventHandler(priority = EventPriority.MONITOR) // Checks if other plugins are using the event
    public void onSellstickUseNew(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        // Check if clicked block is chest, barrel or shulker
        if (!EventUtils.didClickContainerWithSellStick(event)) return;

        // Check if another plugin is cancelling the event
        if(event.useInteractedBlock() == Event.Result.DENY){
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
