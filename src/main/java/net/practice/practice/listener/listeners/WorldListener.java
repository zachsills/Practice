package net.practice.practice.listener.listeners;

import net.practice.practice.Practice;
import net.practice.practice.game.arena.map.MapLoc;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER)
            return;
        if(event.getCause() != EntityDamageEvent.DamageCause.VOID)
            return;

        event.getEntity().teleport(Practice.getInstance().getSpawn());
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (MapLoc.getArenaWorld() == null) return;
        if (event.getBlock().getWorld().getUID().equals(MapLoc.getArenaWorld().getUID())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (MapLoc.getArenaWorld() == null || MapLoc.getArenaWorld().getUID() == null || event.getIgnitingBlock().getWorld().getUID() == null) return;
        if (event.getIgnitingBlock().getWorld().getUID().equals(MapLoc.getArenaWorld().getUID())) {
            if (event.getCause() == BlockIgniteEvent.IgniteCause.EXPLOSION || event.getCause() == BlockIgniteEvent.IgniteCause.LAVA
                    || event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING || event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }
}
