package com.shmkane.sellstick.utilities;

import com.shmkane.sellstick.configs.SellstickConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.logging.Level;

public class CommandUtils {

    public static void giveSellStick(Player target, int uses) {

        // TODO: Do Items stack if they have a random NBT? If they don't stack, lets get rid of this random string shit

        ItemStack itemStack;

        try {
            itemStack = new ItemStack(SellstickConfig.material);
        } catch (Exception ex) {
            ChatUtils.log(Level.SEVERE, SellstickConfig.prefix + " - Invalid item set in config. Please read the links I put in the config to fix this.");
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set display name
        itemMeta.displayName(MiniMessage.miniMessage().deserialize(SellstickConfig.displayName));

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); //Maybe add after item should be glowable?

        // Add glow if required
        if (SellstickConfig.glow) {
            ItemUtils.glow(itemStack);
        }

        // Apply meta to item stack
        itemStack.setItemMeta(itemMeta);


        // Set NBT, uses and lore
        itemStack = ItemUtils.setSellStick(itemStack);
        ItemStack finalItem = ItemUtils.setUses(itemStack, uses);

        // Add to inventory
        target.getInventory().addItem(finalItem);
    }
}
