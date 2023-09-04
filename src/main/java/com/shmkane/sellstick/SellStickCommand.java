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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        PluginMeta pdf = SellStick.getInstance().getPluginMeta();

        if (args.length == 0) {
            ChatUtils.sendCommandNotProperMessage(sender, pdf);
            return true;
        }
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("sellstick.reload")) {
                try {
                    SellStick.plugin.reload();
                    return true;
                } catch (Exception ex) {
                    ChatUtils.msg(sender, "&cSomething went wrong! Check console for error");
                    ChatUtils.log(Level.SEVERE, ex.getMessage());
                }
            }
        }
        //TODO: Reduce reduce reduce!!!
        else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("sellstick.give")) {

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

                for (int i = 0; i < numSticks; i++) {
                    /*
                      This assigns a random string to the item meta so that the item cannot be
                      stacked
                     */
                    RandomString random = new RandomString(5);
                    String UUID = random.nextString();
                    ItemStack itemStack;
                    try {
                        itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial(StickConfig.instance.item)));
                    } catch(NullPointerException ex) {
                        ChatUtils.log(Level.SEVERE, "[%s] - Invalid item set in config. Please read the links I put in the config to fix this.");
                        return false;
                    }
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    List<String> lores = new ArrayList<>();
                    itemMeta.displayName(MiniMessage.miniMessage().deserialize(StickConfig.instance.name + UUID));

                    // Load values from config onto the stick lores array
                    for (int z = 0; z < StickConfig.instance.lore.size(); z++) {
                        //TODO: Replace below with new colour formatter
                        lores.add(StickConfig.instance.lore.get(z).replace("&", ChatColor.COLOR_CHAR + ""));
                    }

                    try {
                        lores.add(StickConfig.instance.durabilityLine - 1, "%usesLore%");
                    } catch (IndexOutOfBoundsException e) {
                        ChatUtils.msg(sender, NamedTextColor.RED + "CONFIG ERROR:");
                        ChatUtils.msg(sender,
                                NamedTextColor.RED + "You tried to set a DurabilityLine of "
                                        + (StickConfig.instance.durabilityLine - 1) + " but the lore is "
                                        + lores.size() + " long");
                        ChatUtils.msg(sender,
                                NamedTextColor.RED + "Try changing the DurabilityLine value in the config");
                        ChatUtils.msg(sender, NamedTextColor.RED + "Then, run /sellstick reload");

                        return false;

                    } catch (Exception ex) {
                        ChatUtils.msg(sender, NamedTextColor.RED
                                + "Something went wrong. Please check the console for an error message.");
                        ChatUtils.log(Level.SEVERE, ex.getMessage());
                        return false;
                    }

                    if (args[3].equalsIgnoreCase("infinite") || args[3].equalsIgnoreCase("i")) {
                        lores.set(StickConfig.instance.durabilityLine - 1,
                                lores.get(StickConfig.instance.durabilityLine - 1).replace("%usesLore%",
                                        StickConfig.instance.infiniteLore));
                    } else {
                        try {
                            int uses = Integer.parseInt(args[3]);
                            // otherwise replace it with the remaining uses
                            lores.set(StickConfig.instance.durabilityLine - 1,
                                    lores.get(StickConfig.instance.durabilityLine - 1).replace("%usesLore%",
                                            StickConfig.instance.finiteLore.replace("%remaining%", uses + "")));
                        } catch (Exception ex) {
                            // They typed something stupid here...
                            ChatUtils.sendCommandNotProperMessage(sender, pdf);
                            return false;
                        }
                    }
                    //TODO: Change to new "setLore"
                    itemMeta.setLore(lores);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    itemStack.setItemMeta(itemMeta);
                    //FIXME: Not sure what the fuck this error is
                    if (StickConfig.instance.glow) {
                        itemStack = ItemUtils.glow(itemStack);
                    }

                    target.getInventory().addItem(itemStack);
                }
                ChatUtils.msg(target, StickConfig.instance.receiveMessage.replace("%amount%",
                        Integer.parseInt(args[2]) + ""));

                ChatUtils.msg(sender, StickConfig.instance.giveMessage.replace("%player%", target.getName())
                        .replace("%amount%", Integer.parseInt(args[2]) + ""));

                return true;
            } else {
                ChatUtils.msg(sender, StickConfig.instance.noPerm);
            }
        } else {
            //TODO: Look at error
            ChatUtils.msg(sender, "" + NamedTextColor.RED + "Invalid command. Type /Sellstick for help");
        }
        return false;
    }
}