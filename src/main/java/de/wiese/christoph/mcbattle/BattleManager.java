package de.wiese.christoph.mcbattle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BattleManager {
    private static FileConfiguration config;
    public static HashMap<String, BattleArena> bas = new HashMap<String, BattleArena>();
    private static String[] roles = {"spec", "red", "blue"};

    public static void initArenas() {
        config = BattleMain.plugin.getConfig();
        if(BattleMain.plugin.getConfig().contains("arenas")){
            for (String aN : BattleMain.plugin.getConfig().getConfigurationSection("arenas").getKeys(false)) {
                Location specL = new Location(Bukkit.getWorld(config.getString("arenas." + aN + ".specSpawn.world")), config.getDouble("arenas." + aN + ".specSpawn.x"), config.getDouble("arenas." + aN + ".specSpawn.y"), config.getDouble("arenas." + aN + ".specSpawn.z"), (float) config.getDouble("arenas." + aN + ".specSpawn.yaw"), (float) config.getDouble("arenas." + aN + ".specSpawn.pitch"));
                Location redL = new Location(Bukkit.getWorld(config.getString("arenas." + aN + ".redSpawn.world")), config.getDouble("arenas." + aN + ".redSpawn.x"), config.getDouble("arenas." + aN + ".redSpawn.y"), config.getDouble("arenas." + aN + ".redSpawn.z"), (float) config.getDouble("arenas." + aN + ".redSpawn.yaw"), (float) config.getDouble("arenas." + aN + ".redSpawn.pitch"));
                Location blueL = new Location(Bukkit.getWorld(config.getString("arenas." + aN + ".blueSpawn.world")), config.getDouble("arenas." + aN + ".blueSpawn.x"), config.getDouble("arenas." + aN + ".blueSpawn.y"), config.getDouble("arenas." + aN + ".blueSpawn.z"), (float) config.getDouble("arenas." + aN + ".blueSpawn.yaw"), (float) config.getDouble("arenas." + aN + ".blueSpawn.pitch"));

                BattleArena ba = new BattleArena(aN, specL, redL, blueL);
                bas.put(aN.toLowerCase(), ba);
            }
        }
    }

    public static String save() {
        // check if there are arenas in the map
        if(!bas.isEmpty()) {
            // save all arenas in map
            for(BattleArena arena : bas.values()) {
                // save location for each role
                for(String role : roles) {
                    if (arena.getSpawnByName(role) != null) {
                        config.set("arenas." + arena.name + "." + role +"Spawn.world", arena.getSpawnByName(role).getWorld().getName());
                        config.set("arenas." + arena.name + "." + role +"Spawn.x", arena.getSpawnByName(role).getX());
                        config.set("arenas." + arena.name + "." + role +"Spawn.y", arena.getSpawnByName(role).getY());
                        config.set("arenas." + arena.name + "." + role +"Spawn.z", arena.getSpawnByName(role).getZ());
                        config.set("arenas." + arena.name + "." + role +"Spawn.yaw", arena.getSpawnByName(role).getYaw());
                        config.set("arenas." + arena.name + "." + role +"Spawn.pitch", arena.getSpawnByName(role).getPitch());
                    } else return ChatColor.RED + role.toUpperCase() + " spawn for " + arena.name + " not set!";
                }
            }
            BattleMain.plugin.saveConfig();
            return ChatColor.GREEN + "Successfully saved!";
        }
        return ChatColor.RED + "Nothing to save.";
    }

    public static String setSpawn(String arena, String role, Location loc) {
        if(bas.containsKey(arena)) {
            switch (role) {
                case "spec":
                    bas.get(arena).specSpawn = loc;
                    return ChatColor.GREEN + "Successfully set Spectator spawn!";
                case "red":
                    bas.get(arena).redSpawn = loc;
                    return ChatColor.GREEN + "Successfully set Red spawn!";
                case "blue":
                    bas.get(arena).blueSpawn = loc;
                    return ChatColor.GREEN + "Successfully set Blue spawn!";
                default:
                    return ChatColor.RED + "Unknown role!";
            }
        }
        return ChatColor.RED + "Unknow Arena!";
    }

    public static BattleArena getArenaWithPlayer(Player player) {
        for(BattleArena arena : bas.values()) if(arena.containsPlayer(player)) return arena;
        return null;
    }
}
