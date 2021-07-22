package de.wiese.christoph.mcbattle;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BattleSign implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {

        if(e.getPlayer().hasPermission("battle.admin")) {
            // create join sign
            if(e.getLine(0).equalsIgnoreCase("battle join")) {
                // get arena with given name
                if(e.getLine(1).isBlank()) return;
                BattleArena arena;
                if(BattleManager.bas.containsKey(e.getLine(1).toLowerCase()))
                    arena = BattleManager.bas.get(e.getLine(1).toLowerCase());
                else {
                    e.getPlayer().sendMessage("Invalid arena name!");
                    return;
                }

                e.setLine(0, "\"\\\"§6[§4Battle§6]\\\"\"");
                e.setLine(2, ChatColor.GREEN + "Join");
                e.setLine(3, ChatColor.LIGHT_PURPLE + arena.name);
            }
            // create leave sign
            else if(e.getLine(0).equalsIgnoreCase("battle leave")) {
                e.setLine(0, "§6[§4Battle§6]");
                e.setLine(2, ChatColor.GREEN + "Leave");
            }
        }
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent e){
        // signs
        if (e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Sign) {
            Block b = e.getClickedBlock();
            Sign sign = (Sign) b.getState();

            if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Battle]")) {
                // join
                if(ChatColor.stripColor(sign.getLine(2)).equalsIgnoreCase("Join")) {
                    String arenaName = ChatColor.stripColor(sign.getLine(3));
                    if(BattleManager.bas.containsKey(arenaName.toLowerCase())) {
                        Bukkit.dispatchCommand(e.getPlayer(), "battle join " + arenaName);
                    }
                }
                // leave
                else if(ChatColor.stripColor(sign.getLine(2)).equalsIgnoreCase("Leave"))
                    Bukkit.dispatchCommand(e.getPlayer(), "battle leave");
            }
        }
    }
}
