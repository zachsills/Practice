package net.practice.practice.listener.listeners;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        final Player player = event.getPlayer();
        final BlockState state = event.getClickedBlock().getState();
        if(state instanceof Skull) {
            final Skull skull = (Skull) state;

            player.sendMessage(ChatColor.YELLOW + "This is the head of: " + ChatColor.GOLD  + ((skull.getSkullType() == SkullType.PLAYER) && (skull.hasOwner()) ? skull.getOwner()
                    : "a " + WordUtils.capitalizeFully(skull.getSkullType().name())));
        }
    }
}
