package de.wiese.christoph.mcbattle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BattleTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("battle")) {
            // subcommands //
            if(args.length == 1) {
                List<String> list = new ArrayList<>();
                if(sender.hasPermission("mcbattle.admin")) {
                    list.add("create");
                    list.add("save");
                    list.add("list");
                    list.add("setSpawn");
                }
                list.add("join");
                list.add("view");
                list.add("leave");

                Collections.sort(list);
                return list;
            }

            // autocomplete for set Spawn //
            if(args.length == 2 && args[0].equalsIgnoreCase("setSpawn")) {
                return new ArrayList<>(BattleManager.bas.keySet());
            }
            if(args.length == 3 && args[0].equalsIgnoreCase(("setSpawn"))) {
                List<String> roles = new ArrayList<>();
                roles.add("spec");
                roles.add("red");
                roles.add("blue");
                return roles;
            }

            // autocomplete for join and view //
            if(args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("view")))
                return new ArrayList<>(BattleManager.bas.keySet());

        }
        return null;
    }
}
