package de.wiese.christoph.mcbattle;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BattleManager {
    private static FileConfiguration config;
    public static HashMap<String, BattleArena> bas = new HashMap<>();
    private static final String[] roles = {"spec", "red", "blue"};

    public static HashMap<String, BattleTournament> bts = new HashMap<>();

    public static void initArenas() {
        config = BattleMain.plugin.getConfig();
        if(BattleMain.plugin.getConfig().contains("arenas")){
            for (String aN : BattleMain.plugin.getConfig().getConfigurationSection("arenas").getKeys(false)) {
                // load spawns
                Location specL = new Location(Bukkit.getWorld(config.getString("arenas." + aN + ".specSpawn.world")), config.getDouble("arenas." + aN + ".specSpawn.x"), config.getDouble("arenas." + aN + ".specSpawn.y"), config.getDouble("arenas." + aN + ".specSpawn.z"), (float) config.getDouble("arenas." + aN + ".specSpawn.yaw"), (float) config.getDouble("arenas." + aN + ".specSpawn.pitch"));
                Location redL = new Location(Bukkit.getWorld(config.getString("arenas." + aN + ".redSpawn.world")), config.getDouble("arenas." + aN + ".redSpawn.x"), config.getDouble("arenas." + aN + ".redSpawn.y"), config.getDouble("arenas." + aN + ".redSpawn.z"), (float) config.getDouble("arenas." + aN + ".redSpawn.yaw"), (float) config.getDouble("arenas." + aN + ".redSpawn.pitch"));
                Location blueL = new Location(Bukkit.getWorld(config.getString("arenas." + aN + ".blueSpawn.world")), config.getDouble("arenas." + aN + ".blueSpawn.x"), config.getDouble("arenas." + aN + ".blueSpawn.y"), config.getDouble("arenas." + aN + ".blueSpawn.z"), (float) config.getDouble("arenas." + aN + ".blueSpawn.yaw"), (float) config.getDouble("arenas." + aN + ".blueSpawn.pitch"));

                // load items
                ItemStack[] armor = null;
                if(BattleMain.plugin.getConfig().contains("arenas." + aN + ".armor")) {
                    armor = new ItemStack[4];
                    for (int i = 0; i < 4; i++)
                        armor[i] = new ItemStack(Material.getMaterial(BattleMain.plugin.getConfig().getString("arenas." + aN + ".armor." + i)));
                }
                ItemStack[] hotbar = null;
                if(BattleMain.plugin.getConfig().contains("arenas." + aN + ".hotbar")) {
                    hotbar = new ItemStack[9];
                    for (int i = 0; i < 9; i++)
                        hotbar[i] = new ItemStack(Material.getMaterial(BattleMain.plugin.getConfig().getString("arenas." + aN + ".hotbar." + i)));
                }

                BattleArena ba = new BattleArena(aN, specL, redL, blueL, armor, hotbar);
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

                // save armor
                if(arena.armor != arena.standardArmor) {
                    for(int i = 0; i < 4; i++) {
                        if (arena.armor[i] == null) {
                            config.set("arenas." + arena.name + ".armor." + i, Material.AIR.name());
                            continue;
                        }
                        config.set("arenas." + arena.name + ".armor." + i, arena.armor[i].getType().name());
                    }
                }
                // save hotbar
                if(arena.hotbar != arena.standardHotbar) {
                    for(int i = 0; i < 9; i++) {
                        if(arena.hotbar[i] == null) {
                            config.set("arenas." + arena.name + ".hotbar." + i, Material.AIR.name());
                            continue;
                        }
                        config.set("arenas." + arena.name + ".hotbar." + i, arena.hotbar[i].getType().name());
                    }
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
        return ChatColor.RED + "Unknown Arena!";
    }

    public static String setEquipment(String arenaName, ItemStack[] armor, ItemStack[] hotbar) {
        if(bas.containsKey(arenaName)) {
            BattleArena arena = bas.get(arenaName);
            arena.armor = armor;
            arena.hotbar = hotbar;
            return ChatColor.GREEN + "Successfully set equipment for " + arena.name;
        }
        return ChatColor.RED + "Unknown Arena!";
    }

    public static void closeTournament(String name) {
        if(bts.containsKey(name.toLowerCase()))
            bts.remove(name.toLowerCase());
    }

    public static BattleArena getArenaWithPlayer(Player player) {
        for(BattleArena arena : bas.values()) if(arena.containsPlayer(player)) return arena;
        return null;
    }

    public static BattleTournament getTournamentWithPlayer(Player player) {
        for(BattleTournament tournament : bts.values()) if(tournament.containsPlayer(player)) return tournament;
        return null;
    }
}
