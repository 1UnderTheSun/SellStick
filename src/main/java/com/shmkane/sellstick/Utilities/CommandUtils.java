package com.shmkane.sellstick.Utilities;

import com.shmkane.sellstick.Configs.SellstickConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.logging.Level;

public class CommandUtils {

    public static void giveSellStick(Player target, int uses, boolean isInfinite) {

        // FIXME: Do Items stack if they have a random NBT? If they don't stack, lets get rid of this random string shit
        // This assigns a random string to the item meta so that the item cannot be stacked
        RandomString random = new RandomString(5);

        String UUID = random.nextString();

        ItemStack itemStack;

        try {
            itemStack = new ItemStack(SellstickConfig.instance.material);
        } catch (NullPointerException ex) {
            ChatUtils.log(Level.SEVERE, "[%s] - Invalid item set in config. Please read the links I put in the config to fix this.");
            return;
        }

        // TODO: CreateSellStick function ???
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set display name
        itemMeta.displayName(MiniMessage.miniMessage().deserialize(SellstickConfig.instance.displayName + UUID));

        // Set NBT, uses and lore
        ItemUtils.setSellStick(itemStack);
        ItemUtils.setUses(itemStack, uses);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS); //Maybe add after item should be glowable?

        // Apply meta to item stack
        itemStack.setItemMeta(itemMeta);

        // Add glow if required
        if (SellstickConfig.instance.glow) {
            ItemUtils.glow(itemStack);
        }

        // Add to inventory
        target.getInventory().addItem(itemStack);
    }
}
