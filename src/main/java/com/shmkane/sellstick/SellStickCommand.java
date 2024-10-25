package com.shmkane.sellstick;

import com.shmkane.sellstick.configs.SellstickConfig;
import com.shmkane.sellstick.utilities.ChatUtils;
import com.shmkane.sellstick.utilities.CommandUtils;
import com.shmkane.sellstick.utilities.ConvertUtils;
import com.shmkane.sellstick.utilities.MergeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SellStickCommand implements TabExecutor {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("sellstick.give")) {
                commands.add("give");
            }
            if (sender.hasPermission("sellstick.reload")) {
                commands.add("reload");
            }
            if (sender.hasPermission("sellstick.convert")) {
                commands.add("convert");
            }
            if (sender.hasPermission("sellstick.merge")) {
                commands.add("merge");
            }
        } else if (args.length == 2) {
            for(Player player : SellStick.getInstance().getServer().getOnlinePlayers()){
                commands.add(player.getName());
            }
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (args.length == 0) {
            ChatUtils.sendCommandNotProperMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Reload Command
        if (subCommand.equals("reload") && sender.hasPermission("sellstick.reload")) {
            try {
                SellStick.getInstance().reload();
                return true;
            } catch (Exception ex) {
                ChatUtils.sendMsg(sender, "<red>Something went wrong! Check console for error", true);
                ChatUtils.log(Level.SEVERE, ex.getMessage());
                return false;
            }
        }

        // Convert Command
        if (subCommand.equals("convert") && sender.hasPermission("sellstick.convert")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return false;
            }
            ConvertUtils.convertSellStick((Player) sender);
            return true;
        }

        // Merge Command
        if (subCommand.equals("merge") && sender.hasPermission("sellstick.merge")) {
            Player target = SellStick.getInstance().getServer().getPlayer(args[1]);

            ItemStack[] sellsticks = MergeUtils.searchInventory(target);
            int usesSum = MergeUtils.sumSellStickUses(sellsticks);
            MergeUtils.removeSellsticks(target, sellsticks);
            CommandUtils.giveSellStick(target, usesSum);
        }

        // Give Command
        else if (subCommand.equals("give") && sender.hasPermission("sellstick.give")) {

            if (args.length < 4) {
                ChatUtils.sendMsg(sender, "<red>Not enough arguments!", true);
                return false;
            }

            Player target = SellStick.getInstance().getServer().getPlayer(args[1]);
            if (target == null) {
                ChatUtils.sendMsg(sender, "<red>Player not found", true);
                return false;
            }

            // Check if the argument is an integer
            int numSticks;
            try {
                numSticks = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ChatUtils.sendMsg(sender, "<red>Not a number: " + args[2], true);
                return false;
            }

            // Check if the stick is infinite
            String argUses = args[3].toLowerCase();
            int uses;

            if (argUses.equals("i") || argUses.equals("infinite")) {
                uses = Integer.MAX_VALUE;
            } else { // Parse the argument is a number
                try {
                    uses = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    ChatUtils.sendMsg(sender, "<red>Must be a number or 'i': ", true);
                    return false;
                }
            }

            // Give sell sticks
            for (int i = 0; i < numSticks; i++) {
                // TODO: Check if inventory is full or has enough slots??
                CommandUtils.giveSellStick(target, uses);
            }

            ChatUtils.sendMsg(target, SellstickConfig.receiveMessage.replace("%amount%", numSticks + ""), true);
            ChatUtils.sendMsg(sender, SellstickConfig.giveMessage.replace("%player%", target.getName()).replace("%amount%", numSticks + ""), true);

            return true;
        } else {
            ChatUtils.sendMsg(sender, SellstickConfig.noPerm, true);
        }
        return false;
    }
}