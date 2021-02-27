package de.wiese.christoph.mcbattle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BattleArena {
    public String name;

    public Location specSpawn;
    public Location redSpawn;
    public Location blueSpawn;

    private List<Player> specPlayers = new ArrayList<>();
    private Player redPlayer = null;
    private Player bluePlayer = null;

    private int redScore = 0;
    private int blueScore = 0;

    public boolean active = false;
    private int rounds = 0;

    public ItemStack[] armor;
    public ItemStack[] hotbar;
    ///////////// Standard Items /////////////////
    public ItemStack[] standardArmor = new ItemStack[]{new ItemStack(Material.CHAINMAIL_BOOTS), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_HELMET)};
    public ItemStack[] standardHotbar = new ItemStack[]{new ItemStack(Material.IRON_AXE)};
    //////////////////////////////////////////////

    public BattleArena(String name, Location specSpawn, Location redSpawn, Location blueSpawn, ItemStack[] armor, ItemStack[] hotbar) {
        this.name = name;
        this.specSpawn = specSpawn;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.armor = armor == null ? standardArmor : armor;
        this.hotbar = hotbar == null ? standardHotbar : hotbar;
    }

    public BattleArena(String name) {
        this.name = name;
        this.specSpawn = null;
        this.redSpawn = null;
        this.blueSpawn = null;
        armor = standardArmor;
        hotbar = standardHotbar;
    }

    private void beginRound() {
        active = true;

        // teleport players
        redPlayer.teleport(redSpawn);
        bluePlayer.teleport(blueSpawn);
        // set inventory
        for(Player p : new Player[]{redPlayer, bluePlayer}) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(armor);
            for(int i = 0; i < hotbar.length; i++)
                p.getInventory().setItem(i, hotbar[i]);
        }
    }

    public void endRound(Player deadPlayer) {
        // set winner
        Player winner = deadPlayer == redPlayer? bluePlayer : redPlayer;
        // change score
        if(deadPlayer == redPlayer) blueScore++;
        else redScore++;

        // respawn dead Player
        deadPlayer.spigot().respawn();
        deadPlayer.teleport(specSpawn);

        winner.setHealth(20L);

        rounds++;
        int maxRounds = 3;
        if(rounds < maxRounds && redScore != (maxRounds /rounds)+1 && blueScore != (maxRounds /rounds)+1) {
            sendTitleToAll(ChatColor.LIGHT_PURPLE + winner.getName() + ChatColor.GOLD + " won round " + rounds,
                    ChatColor.RED + ""+redScore + ChatColor.WHITE + "/" + ChatColor.BLUE + ""+blueScore, 60);

            // start next round with a delay
            new BukkitRunnable() {
                @Override
                public void run() {
                    beginRound();
                }
            }.runTaskLater(BattleMain.plugin, 3*20L);
        }
        else {
            if(redScore > blueScore) winner = redPlayer;
            else winner = bluePlayer;

            sendTitleToAll(ChatColor.LIGHT_PURPLE + winner.getName() + ChatColor.GOLD + " has won the battle!",
                    ChatColor.RED + ""+redScore + ChatColor.WHITE + "/" + ChatColor.BLUE + ""+blueScore, 60);
            // start next round with a delay
            Player finalWinner = winner;
            new BukkitRunnable() {
                @Override
                public void run() {
                    BattleTournament t = BattleManager.getTournamentWithPlayer(finalWinner);
                    stopGame();
                    if(t != null)
                        t.endRound(finalWinner);
                    cancel();
                }
            }.runTaskLater(BattleMain.plugin, 5*20L);
        }
    }

    public void stopGame() {
        if(active) {
            active = false;
            rounds = 0;
            redScore = 0;
            blueScore = 0;
            for(Player p : new Player[]{redPlayer, bluePlayer}) {
                p.getInventory().clear();
                p.setHealth(20L);
                leave(p);
            }
            redPlayer = null;
            bluePlayer = null;
        }
    }

    private void checkForStart() {
        if(redPlayer != null && bluePlayer != null) {
            // start countdown
            new BukkitRunnable() {
                // set seconds
                int seconds = 16;

                @Override
                public void run() {
                    seconds--;

                    sendTitleToAll(ChatColor.LIGHT_PURPLE + "" + seconds, ChatColor.RED + redPlayer.getName() + " VS " + bluePlayer.getName(), 21);

                    if(redPlayer == null || bluePlayer == null){
                        cancel();
                        return;
                    }

                    // counter ends or a player left
                    if(seconds <= 0) {
                        beginRound();
                        cancel();
                    }
                }
            }.runTaskTimer(BattleMain.plugin, 0, 20);
        }
    }

    public void join(Player player) {
        // check if the player is in an arena
        BattleArena currentArena = BattleManager.getArenaWithPlayer(player);
        if (currentArena != null) currentArena.leave(player);

        if(redPlayer != null && bluePlayer != null) player.sendMessage(ChatColor.RED + "There are already 2 players!");

        if(redPlayer == null) redPlayer = player;
        else bluePlayer = player;

        player.teleport(specSpawn);
        checkForStart();
        player.sendMessage( ChatColor.GREEN + "Joined " + name + " as Player!");
    }

    public void view(Player player) {
        // check if the player is in an arena
        BattleArena currentArena = BattleManager.getArenaWithPlayer(player);
        if (currentArena != null) currentArena.leave(player);

        specPlayers.add(player);
        player.teleport(specSpawn);
        player.sendMessage(ChatColor.GREEN + "Joined " + name + " as Spectator!");
    }

    public void leave(Player player) {
        if(specPlayers.contains(player)) specPlayers.remove(player);
        else if (redPlayer == player) {
            if(active) stopGame();
            redPlayer = null;
        }
        else if (bluePlayer == player) {
            if(active) stopGame();
            bluePlayer = null;
        }

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "Leaved " + name + "!");
    }

    public Location getSpawnByName(String role) {
        return switch (role) {
            case "spec" -> this.specSpawn;
            case "red" -> this.redSpawn;
            case "blue" -> this.blueSpawn;
            default -> null;
        };
    }

    public boolean containsPlayer(Player player) {
        return specPlayers.contains(player) || redPlayer == player || bluePlayer == player;
    }

    public boolean isActivePlayer(Player player) {
        return redPlayer == player || bluePlayer == player;
    }

    public void sendTitleToAll(String title, String subtitle, int durationTicks) {
        redPlayer.sendTitle(title, subtitle, 0, durationTicks, 0);
        bluePlayer.sendTitle(title, subtitle, 0, durationTicks, 0);
        for(Player p : specPlayers)
            p.sendTitle(title, subtitle, 0, durationTicks, 0);
    }

    public void sendMessageToAll(String msg) {
        redPlayer.sendMessage(msg);
        bluePlayer.sendMessage(msg);
        for(Player p : specPlayers)
            p.sendMessage(msg);
    }
}
