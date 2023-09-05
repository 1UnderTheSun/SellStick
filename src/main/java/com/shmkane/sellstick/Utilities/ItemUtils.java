package com.shmkane.sellstick.Utilities;

import com.shmkane.sellstick.Configs.SellstickConfig;
import com.shmkane.sellstick.SellStick;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class ItemUtils {

    private static final UUID uuid = UUID.fromString("c5faa888-4b14-11ee-be56-0242ac120002");

    //TODO: Add Lore to ItemStacks
    //TODO: Add Name to ItemStacks

    // Make an ItemStack Glow
    public static void glow(ItemStack itemStack) {
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
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

        //FIXME: Add colour support for LoreList (also check if this shit works)

        // Lore
        List<Component> loreList = new ArrayList<>();
        for(String loreLine : SellstickConfig.instance.lore) {
            loreList.add(MiniMessage.miniMessage().deserialize(loreLine));
        }
        loreList.add(MiniMessage.miniMessage().deserialize(SellstickConfig.instance.finiteLore.replace("%remaining%", String.valueOf(uses))));
        // Set Lore List
        itemStack.getItemMeta().lore(loreList);

    }

    // Subtract a use from a SellStick
    public static void subtractUses(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        nbtItemStack.setInteger("UsesRemaining", getUses(itemStack) - 1);
        // TODO: Add use updates to lore
        NBT.itemStackFromNBT(nbtItemStack);
    }

    public static void setSellStick(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        nbtItemStack.setUUID("SellStick", uuid);
        NBT.itemStackFromNBT(nbtItemStack);
    }

    public static boolean isSellStick(ItemStack itemStack) {
        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);
        Material material = SellstickConfig.instance.material;
        return (Objects.equals(nbtItemStack.getUUID("SellStick").toString(), "c5faa888-4b14-11ee-be56-0242ac120002") && itemStack.getType() == material);
    }

    static double setMultiplier(Player player) {
        /*
        * Permissions based multiplier check. If user doesn't have
        * sellstick.multiplier.x permission Multiplier defaults to 1 as seen below.
        */
        double multiplier = 1;

        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if (perm.getPermission().startsWith("sellstick.multiplier")) {
                String stringPerm = perm.getPermission();
                String permSection = stringPerm.replaceAll("sellstick.multiplier.", "");
                if (Double.parseDouble(permSection) > multiplier) {
                    multiplier = Double.parseDouble(permSection);
                }
            }
        }
        return multiplier;
    }
}
