package com.shmkane.sellstick.Utilities;

import com.shmkane.sellstick.Configs.StickConfig;
import com.shmkane.sellstick.SellStick;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;


public class ChatUtils {
    /**
     * This will send a player a message. If message is empty, it won't send
     * anything.
     *
     * @param sender The target player
     * @param msg    the message
     */
    public static void msg(CommandSender sender, String msg) {
        if (msg.isEmpty()) {
            return;
        }

        sender.sendMessage(StickConfig.instance.prefix + msg);
    }

    //TODO Fix sendCommandNotProperMessage to make it simpler
    /**
     * Sent to 'sender' if their command was invalid.
     * @param sender Sender of the command
     * @param pdf PluginDescriptionFile object
     */
    public static void sendCommandNotProperMessage(CommandSender sender, PluginMeta pdf) {
        // They typed something stupid here...
        ChatUtils.msg(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + pdf.getName()
                + " (MC " + SellStick.getInstance().getServer().getVersion() + ") by " + pdf.getAuthors().get(0));
        if (sender.hasPermission("sellstick.give")) {
            ChatUtils.msg(sender, ChatColor.GREEN
                    + "/SellStick give <player> <amount> (<uses>/infinite)");
        }
    }

    /**
     * Given a string, check if it's numeric.
     *
     * @param str Given a string
     * @return Check if it is a number.
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    /**
     * Server logger
     **/
    public static void log(Level level, String string) {
        SellStick.plugin.getLogger().log(level, string);
    }
}
