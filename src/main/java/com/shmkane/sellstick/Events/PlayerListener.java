package com.shmkane.sellstick.Events;

import com.shmkane.sellstick.Configs.StickConfig;
import com.shmkane.sellstick.Utilities.ChatUtils;
import com.shmkane.sellstick.Utilities.EventUtils;
import com.shmkane.sellstick.Utilities.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

/**
 * The PlayerListener class will handle all the events from the player.
 * Furthermore, it contains code that will take a stick's lore/display name, and
 * chest interaction events.
 *
 * @author shmkane
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR) // Checks if other plugins are using the event
    public void onSellstickUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        //TODO: Reduce Nested Ifs

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Check if clicked block is chest, barrel or shulker
            if (EventUtils.didClickContainerWithSellStick(p, e)) {

                // Check if another plugin is cancelling the event
                if(e.useInteractedBlock() == Event.Result.DENY){
                    ChatUtils.msg(p, StickConfig.instance.territoryMessage);
                    e.setCancelled(true);
                    return;
                }

                // Checks if Player has the permission to use a sellstick
                if (!p.hasPermission("sellstick.use")) {
                    ChatUtils.msg(p, StickConfig.instance.noPerm);
                    e.setCancelled(true);
                    return;
                }

                ItemStack is = p.getInventory().getItemInMainHand();
                ItemMeta im = is.getItemMeta();

                List<Component> lores = im.lore();

                //TODO: Change to component for lores
                int uses = ItemUtils.handleUses(p, lores);
                //TODO: Make sure state isn't null
                InventoryHolder c = (InventoryHolder) e.getClickedBlock().getState();

                double total = EventUtils.calculateWorth(c, e);

                if (total > 0) {
                    //TODO: Change to component for lores
                    if (ItemUtils.postSale(lores, uses, p, total, im, is) && StickConfig.instance.sound) {
                        p.playSound(e.getClickedBlock().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
                    }
                } else {
                    ChatUtils.msg(p, StickConfig.instance.nothingWorth);
                }
                e.setCancelled(true);
            }
        }

        // Checks if Player has the permission to use a sellstick
        if (!p.hasPermission("sellstick.use")) {
            ChatUtils.msg(p, StickConfig.instance.noPerm);
            e.setCancelled(true);
        }
    }
}
