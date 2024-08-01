package com.shmkane.sellstick.utilities;

import com.shmkane.sellstick.configs.SellstickConfig;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    private static final UUID uuid = UUID.fromString("c5faa888-4b14-11ee-be56-0242ac120002");

    // Make an ItemStack Glow
    public static ItemStack glow(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
        return itemStack;
    }

    // Check if an ItemStack is infinite
    public static boolean isInfinite(ItemStack itemStack) {
        NBTItem nbtItemStack = new NBTItem(itemStack);
        return nbtItemStack.getBoolean("Infinite");
    }

    // Set an Itemstack with a NBT Tag of Infinite with a state
    public static NBTItem setInfinite(ItemStack itemStack) {
        NBTItem nbtItemStack = new NBTItem(itemStack);
        nbtItemStack.setBoolean("Infinite", true);
        nbtItemStack.setInteger("UsesRemaining", Integer.MAX_VALUE);
        return nbtItemStack;
    }

    // Get uses Remaining from a SellStick
    public static int getUses(ItemStack itemStack) {
        NBTItem nbtItemStack = new NBTItem(itemStack);
        return nbtItemStack.getInteger("UsesRemaining");
    }

    // Set uses to a SellStick
    public static ItemStack setUses(ItemStack itemStack, int uses) {

        // Update ItemMeta
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(setLoreList(uses));
        itemStack.setItemMeta(itemMeta);

        // NBT
        NBTItem nbtItemStack = new NBTItem(itemStack);
        nbtItemStack.setInteger("UsesRemaining", uses);
        nbtItemStack.setBoolean("Infinite", false);
        if (uses == Integer.MAX_VALUE) nbtItemStack = setInfinite(itemStack);

        return nbtItemStack.getItem();
    }

    // Subtract a use from a SellStick
    public static ItemStack subtractUses(ItemStack itemStack) {
        NBTItem nbtItemStack = new NBTItem(itemStack);
        int newUses = getUses(itemStack) - 1;
        nbtItemStack.setInteger("UsesRemaining", newUses);
        itemStack = nbtItemStack.getItem();

        // Update Uses on Lore

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(setLoreList(newUses));
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }

    public static ItemStack setSellStick(ItemStack itemStack) {
        NBTItem nbtItemStack = new NBTItem(itemStack);
        nbtItemStack.setString("SellStickUUID", uuid.toString());
        nbtItemStack.setString("RandomSSUUID", UUID.randomUUID().toString()); // Make it non stackable
        return nbtItemStack.getItem();
    }

    @Deprecated
    public static boolean isSellStick(ItemStack itemStack) {
        boolean matchUUID = matchSellStickUUID(itemStack);
        boolean matchMaterial = matchSellStickMaterial(itemStack);

        return (matchUUID && matchMaterial);
    }

    public static boolean matchSellStickUUID(ItemStack itemStack) {
        NBTItem nbtItemStack = new NBTItem(itemStack);
        return nbtItemStack.getString("SellStickUUID").equals(uuid.toString());
    }

    public static boolean matchSellStickMaterial(ItemStack itemStack) {
        return itemStack.getType().equals(SellstickConfig.material);
    }

    public static List<Component> setLoreList(int uses){
        List<Component> loreList = new ArrayList<>();
        for(String loreLine : SellstickConfig.lore) {
            loreList.add(MiniMessage.miniMessage().deserialize(loreLine));
        }
        if(uses == Integer.MAX_VALUE){
            loreList.add(MiniMessage.miniMessage().deserialize(SellstickConfig.infiniteLore));
        } else {
            loreList.add(MiniMessage.miniMessage().deserialize(SellstickConfig.finiteLore.replace("%remaining%", String.valueOf(uses))));
        }
        return loreList;
    }

}
