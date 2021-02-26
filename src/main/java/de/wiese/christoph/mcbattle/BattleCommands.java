package de.wiese.christoph.mcbattle;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Locale;

public class BattleCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            //////////////////////////////////////////////
            // args[0] - subcommand                     //
            // From args[1] args for subcommand         //
            //////////////////////////////////////////////
            if(args.length < 1) {
                player.sendMessage(ChatColor.RED + "Unknown command!");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "create":
                    if(player.hasPermission("mcbattle.admin")) {
                        if(args.length >= 2) {
                            BattleManager.bas.put(args[1].toLowerCase(), new BattleArena(args[1]));
                            player.sendMessage(ChatColor.GREEN + "Successfully created Arena: '" + args[1] + "'!");
                        } else player.sendMessage(ChatColor.RED + "No arena name provided!");
                    }
                    break;

                case "save":
                    if(player.hasPermission("mcbattle.admin"))
                        player.sendMessage(BattleManager.save());
                    break;

                case "list":
                    if(player.hasPermission("mcbattle.admin")) {
                        String list = "";
                        for(BattleArena arena : BattleManager.bas.values()) list += arena.name + "  ";
                        player.sendMessage(ChatColor.GREEN + "Current arenas: " + ChatColor.GOLD + list + ChatColor.GREEN + " available!");
                    }
                    break;

                case "setspawn":
                    if(player.hasPermission("mcbattle.admin")) {
                        if(args.length >= 3) {
                            player.sendMessage(BattleManager.setSpawn(args[1].toLowerCase(), args[2].toLowerCase(), player.getLocation()));
                        } else player.sendMessage(ChatColor.RED + "Must provide arena name and spawn role!");
                    }
                    break;

                case "setequip":
                    if(player.hasPermission("mcbattle.admin")) {
                        if(args.length >= 2) {
                            ItemStack[] hotbar = new ItemStack[9];
                            for(int i = 0; i < 9; i++)
                                hotbar[i] = player.getInventory().getItem(i);
                            player.sendMessage(BattleManager.setEquipment(args[1], player.getInventory().getArmorContents(), hotbar));
                        } else player.sendMessage(ChatColor.RED + "Must provide arena name!");
                    }
                    break;

                case "join":
                    if(args.length >= 2) {
                        if(BattleManager.bas.containsKey(args[1].toLowerCase())) {
                            // check if the player is in an arena
                            BattleArena currentArena = BattleManager.getArenaWithPlayer(player);
                            if (currentArena != null) currentArena.leave(player);
                            BattleManager.bas.get(args[1].toLowerCase()).join(player);
                        }
                        else player.sendMessage(ChatColor.RED + "Invalid arena name!");
                    } else player.sendMessage(ChatColor.RED + "Must provide arena name!");
                    break;

                case "view":
                    if(args.length >= 2) {
                        if(BattleManager.bas.containsKey(args[1].toLowerCase())) {
                            // check if the player is in an arena
                            BattleArena currentArena = BattleManager.getArenaWithPlayer(player);
                            if (currentArena != null) currentArena.leave(player);

                            BattleManager.bas.get(args[1].toLowerCase()).view(player);
                        }
                        else player.sendMessage(ChatColor.RED + "Invalid arena name!");
                    } else player.sendMessage(ChatColor.RED + "Must provide arena name!");
                    break;

                case "leave":
                    BattleArena arena = BattleManager.getArenaWithPlayer(player);
                    if(arena != null) arena.leave(player);
                    else player.sendMessage(ChatColor.GREEN + "You are not in an arena!");
                    break;

                default:
                    player.sendMessage(ChatColor.RED + "Unknown command '" + args[0] + "'!");
                    break;
            }
        }
        return true;
    }
}
