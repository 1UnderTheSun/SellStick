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
        // This assigns a random string to the item meta so that the item cannot be stacked
        RandomString random = new RandomString(5);

        String UUID = random.nextString();

        ItemStack itemStack;

        try {
            itemStack = new ItemStack(SellstickConfig.material);
        } catch (NullPointerException ex) {
            ChatUtils.log(Level.SEVERE, "[%s] - Invalid item set in config. Please read the links I put in the config to fix this.");
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set display name
        itemMeta.displayName(MiniMessage.miniMessage().deserialize(SellstickConfig.displayName + UUID));

        // Set NBT, uses and lore
        ItemUtils.setSellStick(itemStack);
        ItemUtils.setUses(itemStack, uses);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); //Maybe add after item should be glowable?

        // Add glow if required
        if (SellstickConfig.glow) {
            ItemUtils.glow(itemStack);
        }

        // Apply meta to item stack
        itemStack.setItemMeta(itemMeta);

        // Add to inventory
        target.getInventory().addItem(itemStack);
    }
}
