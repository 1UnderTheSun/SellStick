package com.shmkane.sellstick;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import com.shmkane.sellstick.Configs.StickConfig;
import com.shmkane.sellstick.Utilities.ItemUtils;
import com.shmkane.sellstick.Utilities.ChatUtils;
import com.shmkane.sellstick.Utilities.RandomString;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class SellStickCommand implements CommandExecutor, TabExecutor {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("give");
            if (sender.hasPermission("sellstick.reload")) {
                commands.add("reload");
            }
        } else if (args.length == 2) {
            commands.add(sender.getName());
        } else if (args.length == 3) {
            commands.add("1");
        } else if (args.length == 4) {
            commands.add("i");
            commands.add("1");
            commands.add("2");
            commands.add("3");
            commands.add("5");
            commands.add("10");
        }
        return commands;
    }

    void giveSellStick(Player target, int uses) {

        // This assigns a random string to the item meta so that the item cannot be stacked
        RandomString random = new RandomString(5);
        String UUID = random.nextString();

        ItemStack itemStack;
        try {
            itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial(StickConfig.instance.item)));
        } catch(NullPointerException ex) {
            ChatUtils.log(Level.SEVERE, "[%s] - Invalid item set in config. Please read the links I put in the config to fix this.");
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set display name
        itemMeta.displayName(MiniMessage.miniMessage().deserialize(StickConfig.instance.name + UUID));

        // Set NBT uses and lore
        ItemUtils.setUses(itemStack, uses);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Apply meta to item stack
        itemStack.setItemMeta(itemMeta);

        // Add glow if required
        if (StickConfig.instance.glow) { ItemUtils.glow(itemStack); }

        // Add to inventory
        target.getInventory().addItem(itemStack);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        PluginMeta pluginMeta = SellStick.getInstance().getPluginMeta();

        if (args.length == 0) {
            ChatUtils.sendCommandNotProperMessage(sender, pluginMeta);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        /*
         * Reload command
         */
        if (subCommand.equals("reload") && sender.hasPermission("sellstick.reload")) {
            try {
                SellStick.plugin.reload();
                return true;
            } catch (Exception ex) {
                ChatUtils.msg(sender, "&cSomething went wrong! Check console for error");
                ChatUtils.log(Level.SEVERE, ex.getMessage());
                return false;
            }
        }

        /*
         * Give command
         */
        else if (subCommand.equals("give") && sender.hasPermission("sellstick.give")) {

            if (args.length < 4) {
                ChatUtils.msg(sender, "&cNot enough arguments!");
                return false;
            }

            Player target = SellStick.getInstance().getServer().getPlayer(args[1]);
            if (target == null) {
                ChatUtils.msg(sender, "&cPlayer not found");
                return false;
            }

            int numSticks;
            try {
                numSticks = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ChatUtils.msg(sender, "&cNot a number: " + args[2]);
                return false;
            }

            String argUses = args[3].toLowerCase();
            int uses;
            if (argUses.equals("i") || argUses.equals("infinite")) {
                uses = Integer.MAX_VALUE;
            } else {
                try {
                    uses = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    ChatUtils.msg(sender, "&cMust be a number or 'i': " + args[3]);
                    return false;
                }
            }

            // Give sell sticks
            for (int i = 0; i < numSticks; i++) {
                giveSellStick(target, uses);
            }

            ChatUtils.msg(target, StickConfig.instance.receiveMessage.replace("%amount%",
                    Integer.parseInt(args[2]) + ""));

            ChatUtils.msg(sender, StickConfig.instance.giveMessage.replace("%player%", target.getName())
                    .replace("%amount%", Integer.parseInt(args[2]) + ""));

            return true;
        } else {
            ChatUtils.msg(sender, StickConfig.instance.noPerm);
        }
        return false;
    }
}