package com.shmkane.sellstick.utilities;

import com.shmkane.sellstick.configs.SellstickConfig;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    private static final UUID uuid = UUID.fromString("c5faa888-4b14-11ee-be56-0242ac120002");

    // Make an ItemStack Glow
    public static void glow(ItemStack itemStack) {
        itemStack.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
        itemStack.getItemMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    // Check if an ItemStack is infinite
    public static boolean isInfinite(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        return nbtItemStack.getBoolean("Infinite");
    }

    // Set an Itemstack with a NBT Tag of Infinite with a state
    public static void setInfinite(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        nbtItemStack.setBoolean("Infinite", true);
        nbtItemStack.setInteger("UsesRemaining", Integer.MAX_VALUE);
    }

    // Get uses Remaining from a SellStick
    public static int getUses(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        return nbtItemStack.getInteger("UsesRemaining");
    }

    // Set uses to a SellStick
    public static void setUses(ItemStack itemStack, int uses) {

        // NBT
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        nbtItemStack.setInteger("UsesRemaining", uses);
        nbtItemStack.setBoolean("Infinite", false);
        if(uses == Integer.MAX_VALUE) setInfinite(itemStack);
        itemStack = NBT.itemStackFromNBT(nbtItemStack);

        // Set Lore List
        itemStack.getItemMeta().lore(setLoreList(uses));

    }

    // Subtract a use from a SellStick
    public static void subtractUses(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        int newUses = getUses(itemStack) - 1;
        nbtItemStack.setInteger("UsesRemaining", newUses);
        itemStack = NBT.itemStackFromNBT(nbtItemStack);

        // Update Uses on Lore
        itemStack.lore().set(itemStack.lore().size() - 1, MiniMessage.miniMessage().deserialize(
                SellstickConfig.instance.finiteLore.replace("%remaining%", String.valueOf(newUses))));
    }

    public static void setSellStick(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        nbtItemStack.setUUID("SellStick", uuid);
        NBT.itemStackFromNBT(nbtItemStack);
    }

    @Deprecated
    public static boolean isSellStick(ItemStack itemStack) {
        boolean matchUUID = matchSellStickUUID(itemStack);
        boolean matchMaterial = matchSellstickMaterial(itemStack);

        return (matchUUID && matchMaterial);
    }

    public static boolean matchSellStickUUID(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        return nbtItemStack.getUUID("SellStick").toString().equals("c5faa888-4b14-11ee-be56-0242ac120002");
    }

    public static boolean matchSellstickMaterial(ItemStack itemStack) {
        return itemStack.getType().equals(SellstickConfig.instance.material);
    }

    public static List<Component> setLoreList(int uses){
        List<Component> loreList = new ArrayList<>();
        for(String loreLine : SellstickConfig.instance.lore) {
            loreList.add(MiniMessage.miniMessage().deserialize(loreLine));
        }
        loreList.add(MiniMessage.miniMessage().deserialize(SellstickConfig.instance.finiteLore.replace("%remaining%", String.valueOf(uses))));
        return loreList;
    }

}
