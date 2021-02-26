package de.wiese.christoph.mcbattle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BattleListener implements Listener {

    // always full food
    @EventHandler
    public void onFeed(FoodLevelChangeEvent e) {
        if(e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        // remove player from arena when he leaves
        BattleArena arena = BattleManager.getArenaWithPlayer(e.getPlayer());
        if(arena != null) arena.leave(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = (Player) e.getEntity();
        // stop round if a player dies in an arena
        BattleArena ba = BattleManager.getArenaWithPlayer(p);
        if(ba != null) {
            e.getDrops().clear();
            ba.endRound(p);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            // set cancelled if not allowed in arena
            BattleArena ba = BattleManager.getArenaWithPlayer(p);
            if(ba != null) {
                if(ba.active) {
                    if(!ba.isPlayer(p)) e.setCancelled(true);
                }else e.setCancelled(true);
            }
        }
    }
}
