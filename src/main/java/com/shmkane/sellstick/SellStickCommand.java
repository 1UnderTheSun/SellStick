package com.shmkane.sellstick;

import com.shmkane.sellstick.Configs.SellstickConfig;
import com.shmkane.sellstick.Utilities.ChatUtils;
import com.shmkane.sellstick.Utilities.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
                SellStick.plugin.reload();
                return true;
            } catch (Exception ex) {
                ChatUtils.sendMsg(sender, "&cSomething went wrong! Check console for error", true);
                ChatUtils.log(Level.SEVERE, ex.getMessage());
                return false;
            }
        }

        // Give Command
        else if (subCommand.equals("give") && sender.hasPermission("sellstick.give")) {

            if (args.length < 4) {
                ChatUtils.sendMsg(sender, "&cNot enough arguments!", true);
                return false;
            }

            Player target = SellStick.getInstance().getServer().getPlayer(args[1]);
            if (target == null) {
                ChatUtils.sendMsg(sender, "&cPlayer not found", true);
                return false;
            }

            // Check if the argument is an integer
            int numSticks;
            try {
                numSticks = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ChatUtils.sendMsg(sender, "&cNot a number: " + args[2], true);
                return false;
            }

            // Check if the stick is infinite
            String argUses = args[3].toLowerCase();
            int uses;
            boolean isInfinite = false;

            if (argUses.equals("i") || argUses.equals("infinite")) {
                uses = Integer.MAX_VALUE;
                isInfinite = true;
            } else { // Parse the argument is a number
                try {
                    uses = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    ChatUtils.sendMsg(sender, "&cMust be a number or 'i': " + args[3], true);
                    return false;
                }
            }

            // Give sell sticks
            for (int i = 0; i < numSticks; i++) {
                // TODO: Check if inventory is full or has enough slots??
                CommandUtils.giveSellStick(target, uses, isInfinite);
            }

            ChatUtils.sendMsg(target, SellstickConfig.instance.receiveMessage.replace("%amount%",
                    Integer.parseInt(args[2]) + ""), true);

            ChatUtils.sendMsg(sender, SellstickConfig.instance.giveMessage.replace("%player%", target.getName())
                    .replace("%amount%", Integer.parseInt(args[2]) + ""), true);

            return true;
        } else {
            ChatUtils.sendMsg(sender, SellstickConfig.instance.noPerm, true);
        }
        return false;
    }
}