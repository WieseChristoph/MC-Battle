package de.wiese.christoph.mcbattle;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BattleTournament {
    public final String name;
    public Player creator;
    public boolean active = false;

    public List<Player> available = new ArrayList<>();
    public List<Player> winner = new ArrayList<>();
    public List<Player> looser = new ArrayList<>();

    // convert BattleArena list to array
    private BattleArena[] arenaArr = BattleManager.bas.values().toArray(new BattleArena[BattleManager.bas.size()]);

    public BattleTournament(String name, Player creator) {
        this.name = name;
        this.creator = creator;
        join(creator);
    }

    private void beginRound() {
        if(available.size() > 1) {
            // select random map
            BattleArena chosen = arenaArr[new Random().nextInt(arenaArr.length)];

            // add all players as spectators
            for (int i = 2; i < available.size(); i++) chosen.view(available.get(i));
            for (Player p : winner) chosen.view(p);
            for (Player p : looser) chosen.view(p);

            // add random players as main players
            chosen.join(available.get(0));
            chosen.join(available.get(1));
        } else {
            // if everyone was already in a fight, put all winners back to available for next bracket
            if(winner.size() > 1) {
                for(int i = 0; i < winner.size(); i++)
                    available.add(winner.get(i));
                winner.clear();
                beginRound();
            }
            // only one player is left in winners list
            else {
                sendTitleToAll(ChatColor.LIGHT_PURPLE + winner.get(0).getName(), ChatColor.GOLD + "won the tournament!", 5*20);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(Player p : looser) leave(p);
                        leave(winner.get(0));
                        BattleManager.closeTournament(name);
                        cancel();
                    }
                }.runTaskLater(BattleMain.plugin, 5*20L);
            }
        }
    }

    public void endRound(Player w) {
        // check who won and change entries in lists
        if(available.get(0) == w) {
            winner.add(available.get(0));
            looser.add(available.get(1));
        }else {
            winner.add(available.get(1));
            looser.add(available.get(0));
        }
        available.remove(0);
        available.remove(0);
        // start next round
        beginRound();
    }

    public void beginCountdown() {
        // start countdown
        new BukkitRunnable() {
            // set seconds
            int seconds = 11;

            @Override
            public void run() {
                seconds--;

                sendTitleToAll(ChatColor.LIGHT_PURPLE + "" + seconds, ChatColor.GOLD + "seconds until tournament starts!", 21);

                // counter ends or a player left
                if(seconds <= 0) {
                    Collections.shuffle(available);
                    beginRound();
                    cancel();
                }
            }
        }.runTaskTimer(BattleMain.plugin, 0, 20);
    }

    public void join(Player player) {
        if(active) {
            player.sendMessage(ChatColor.RED + "Tournament already started!");
            return;
        }
        available.add(player);
        player.sendMessage(ChatColor.GREEN + "Joined " + name + " tournament!");
        sendMessageToAll(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " joined the tournament!");
    }

    public void leave(Player player) {
        // check if the player is in an arena and if he ist, let him leave
        BattleArena ba = BattleManager.getArenaWithPlayer(player);
        if(ba != null)
            ba.leave(player);

        // remove player from one of the lists
        if(winner.contains(player)) winner.remove(player);
        else if (looser.contains(player)) looser.remove(player);
        else available.remove(player);

        // change creator
        if(player == creator && winner.size() > 0) changeCreator(winner.get(0));
        else if (player == creator && looser.size() > 0) changeCreator(looser.get(0));
        else if (player == creator && available.size() > 0) changeCreator(available.get(0));

        player.sendMessage(ChatColor.GREEN + "Successfully left the tournament!");
        sendMessageToAll(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " left the tournament!");
    }

    private void changeCreator(Player player) {
        creator = player;
        player.sendMessage(ChatColor.GOLD + "You are the new tournament host!");
    }

    private void sendTitleToAll(String title, String subtitle, int stayTicks) {
        for(Player p : winner)
            p.sendTitle(title, subtitle, 0, stayTicks, 0);
        for(Player p : looser)
            p.sendTitle(title, subtitle, 0, stayTicks, 0);
        for(Player p : available)
            p.sendTitle(title, subtitle, 0, stayTicks, 0);
    }

    private void sendMessageToAll(String msg) {
        for(Player p : winner)
            p.sendMessage(msg);
        for(Player p : looser)
            p.sendMessage(msg);
        for(Player p : available)
            p.sendMessage(msg);
    }

    public boolean containsPlayer(Player player) {
        return winner.contains(player) || looser.contains(player) || available.contains(player);
    }
}
