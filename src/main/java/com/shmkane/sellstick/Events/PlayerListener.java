package com.shmkane.sellstick.Events;

import com.shmkane.sellstick.Configs.StickConfig;
import com.shmkane.sellstick.SellStick;
import com.shmkane.sellstick.Utilities.ChatUtils;
import com.shmkane.sellstick.Utilities.EventUtils;
import com.shmkane.sellstick.Utilities.ItemUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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

    //TODO Add Barrels and Shulkers
    /**
     * Handles the actual clicking event of the player. Deprecated since getItemHand
     * in 1.9+ should specify which hand, but will keep since it allows for
     * backwards compatibility
     * <p>
     * {@link EventPriority} Should let all other plugins handle whether
     * sellstick can be used.
     *
     * @param e The event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        // When they left-click with that item, and that item has the same name
        // as a sellstick
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !p.isSneaking()) {
            if (EventUtils.didClickChestWithSellStick(p, e)) {

                // Other plugin overriden.
                if (e.isCancelled()) {
                    ChatUtils.msg(p, StickConfig.instance.territoryMessage);
                    e.setCancelled(true);
                    return;
                }

                // Didn't have permission :(
                if (!p.hasPermission("sellstick.use")) {
                    ChatUtils.msg(p, StickConfig.instance.noPerm);
                    e.setCancelled(true);
                    return;
                }

                ItemStack is = p.getItemInHand();
                ItemMeta im = is.getItemMeta();

                List<String> lores = im.getLore();

                int uses = ItemUtils.handleUses(p, lores);

                InventoryHolder c = (InventoryHolder) e.getClickedBlock().getState();

                double total = EventUtils.calculateWorth(c, e);

                if (total > 0) {
                    if (ItemUtils.postSale(lores, uses, p, total, im, is) && StickConfig.instance.sound) {
                        p.playSound(e.getClickedBlock().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
                    }
                } else {
                    ChatUtils.msg(p, StickConfig.instance.nothingWorth);
                }
                e.setCancelled(true);
            }
        }else{
            if(EventUtils.isSellStick(p, e)) {
                ChatUtils.msg(p, StickConfig.instance.nonSellingRelated);
                e.setCancelled(true);
            }
        }
    }
}
