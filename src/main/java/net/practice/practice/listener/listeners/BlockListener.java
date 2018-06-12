package net.practice.practice.listener.listeners;

import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.player.Profile;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            Profile profile = Profile.getByPlayer(event.getPlayer());
            if(profile.isInGame()) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(false);
            return;
        }

        Profile profile = Profile.getByPlayer(event.getPlayer());
        switch(profile.getState()) {
            case PLAYING: {
                if(profile.isInGame()) {
                    if(profile.getCurrentDuel().getLadder().isBuildable()) {
                        event.setCancelled(profile.getCurrentDuel().getState() != DuelState.PLAYING);
                        break;
                    }
                }
            }
            default:
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            Profile profile = Profile.getByPlayer(event.getPlayer());
            if(profile.isInGame()) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(false);
            return;
        }

        Profile profile = Profile.getByPlayer(event.getPlayer());
        switch(profile.getState()) {
            case PLAYING: {
                if(profile.isInGame()) {
                    if(profile.getCurrentDuel().getLadder().isBuildable()) {
                        event.setCancelled(profile.getCurrentDuel().getState() != DuelState.PLAYING);
                        break;
                    }
                }
            }
            default:
                event.setCancelled(true);
        }
    }
}
